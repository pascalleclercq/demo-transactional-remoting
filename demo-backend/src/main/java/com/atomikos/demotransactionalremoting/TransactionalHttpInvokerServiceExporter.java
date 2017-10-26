package com.atomikos.demotransactionalremoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class TransactionalHttpInvokerServiceExporter extends
		HttpInvokerServiceExporter {
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionalHttpInvokerServiceExporter.class);
	private static final String TRUE = Boolean.toString(true);
	@Override
	protected InputStream decorateInputStream(
			HttpServletRequest request, InputStream is)
			throws IOException {
		LOGGER.logTrace("Filtering incoming request...");
		
		String transactionId = request.getHeader("Transaction-Id");
		if (transactionId != null) {		
			String expiry = request.getHeader("Transaction-Expiry");
			long timeout = Long.valueOf(expiry) - System.currentTimeMillis();
			try {
				importTransaction(transactionId, timeout);
			} catch (RollbackException e) {
				throw new IOException(e);
			}
		} else {
			LOGGER.logWarning("No transaction context found in incoming request - this request will commit independently of any client transaction!");
		}

		
		return super.decorateInputStream(request, is);
	}
	private void importTransaction(final String transactionId, long timeout) throws RollbackException {
		if (timeout <= 0 ) {
			throw new RollbackException("Attempting to import a transaction that has already expired");
		}
		LOGGER.logDebug("Importing (joining) remote transaction: " + transactionId);
		Propagation propagation = new PropagationImp(transactionId, true, timeout);
		CompositeTransactionManager ctm = getCompostiteTransactionManager();
		CompositeTransaction ct = ctm.recreateCompositeTransaction(propagation, false, false);
		ct.setProperty(TransactionManagerImp.JTA_PROPERTY_NAME, TRUE);
	}
	
	private CompositeTransactionManager getCompostiteTransactionManager() {
		CompositeTransactionManager compositeTransactionManager = Configuration.getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			throw new IllegalStateException("Transaction service not initialized !!!!");
		}
		return compositeTransactionManager;
	}

	
	@Override
	protected OutputStream decorateOutputStream(
			HttpServletRequest request, HttpServletResponse response,
			OutputStream os) throws IOException {
		String transactionId = request.getHeader("Transaction-Id");
		LOGGER.logTrace("Filtering outgoing response for remote transaction: " + transactionId);
		if (transactionId != null) {
			try {		
				
				String requestURI = getURL(request);
				String coordId;
				
				if(response.getStatus() >= 200 && response.getStatus() < 300) {
					coordId = terminated(requestURI, false);
				} else {
					coordId = terminated(requestURI, true);
				}
				if (coordId != null) {
//					Link atomikos = Link.fromUri(uriInfo.getBaseUriBuilder()
//							.path(AtomikosRestPort.class).path(coordId).build()).rel("atomikos").build();
					
					response.addHeader("Atomikos-URI", requestURI+"/atomikos");
					response.addHeader("Atomikos-CoordId", coordId);
					//responseContext.getHeaders().add(HttpHeaders.LINK, atomikos.toString());
				}
				
			} catch (RollbackException e) {
				String msg = "Transaction was rolled back - probably due to a timeout?";
				LOGGER.logWarning(msg, e);
				throw new IOException(msg, e);
			} catch (Exception e) {
				LOGGER.logError("Unexpected error while terminating transaction", e);
				throw e;
			}
			
		} 
		return super.decorateOutputStream(request, response, os);
	}
	
	public  String getURL(HttpServletRequest req) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
	    
	    // Reconstruct original requesting URL
	    StringBuilder url = new StringBuilder();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443) {
	        url.append(":").append(serverPort);
	    }

	    url.append(contextPath);
	    return url.toString();
	}
	
	private String terminated(String uriInfo, boolean error) throws RollbackException {
		
		CompositeTransactionManager ctm = getCompostiteTransactionManager();
		CompositeTransaction current = ctm.getCompositeTransaction();
		
		if(current != null) {
			String rootId = current.getCompositeCoordinator().getRootId();
		
			try {
				if (error)
					current.rollback();
				else
					current.commit();
				
			} catch (RollbackException rb) {
				throw rb;
			} catch (Exception e) {
				throw new SysException("Error in termination: " + e.getMessage(), e);
			}
		
			return rootId;
		}
		return null;
	}
}
