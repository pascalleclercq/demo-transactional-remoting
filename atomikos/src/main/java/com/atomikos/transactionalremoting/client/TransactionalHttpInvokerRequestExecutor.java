package com.atomikos.transactionalremoting.client;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.transactionalremoting.api.AtomikosTransactionService;

public class TransactionalHttpInvokerRequestExecutor extends
		SimpleHttpInvokerRequestExecutor {

	private final Logger LOGGER = LoggerFactory
			.createLogger(TransactionalHttpInvokerRequestExecutor.class);

	private boolean isJta(CompositeTransaction ct) {
		return ct != null
				&& ct.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME) != null;
	}

	private CompositeTransaction getTransaction() {
		CompositeTransactionManager compositeTransactionManager = Configuration
				.getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			throw new IllegalStateException(
					"Transaction service not initialized !!!!");
		}
		return compositeTransactionManager.getCompositeTransaction();
	}

	@Override
	protected void prepareConnection(HttpURLConnection con, int contentLength)
			throws IOException {
		LOGGER.logTrace("Filtering request...");
		CompositeTransaction ct = getTransaction();

		if (isJta(ct)) {
			con.setRequestProperty(AtomikosTransactionService.TRANSACTION_ID, ct
					.getCompositeCoordinator().getCoordinatorId());
			con.setRequestProperty(AtomikosTransactionService.TRANSACTION_EXPIRY, String.valueOf(System
					.currentTimeMillis() + ct.getTimeout()));
		} else {
			LOGGER.logDebug("No transaction bound to thread while calling rest service - request will not be transactional!");
		}

		super.prepareConnection(con, contentLength);
	}

	@Override
	protected void validateResponse(HttpInvokerClientConfiguration config,
			HttpURLConnection con) throws IOException {

		super.validateResponse(config, con);

		LOGGER.logTrace("Filtering response...");

		CompositeTransaction ct = getTransaction();
		String uri = con.getHeaderField(AtomikosTransactionService.ATOMIKOS_URI);
		String coordId = con.getHeaderField(AtomikosTransactionService.ATOMIKOS_COORD_ID);
		if (isJta(ct)) {
			if (uri != null && coordId != null) {
				ct.addParticipant(new ParticipantAdapter(uri, coordId));
			} else {
				LOGGER.logWarning("No atomikos header found in response - request will not be part of transaction");
			}
		} else {
			LOGGER.logDebug("No transaction bound to thread after calling rest service - request will not be transactional");
		}
	}

}
