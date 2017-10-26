package com.atomikos.demotransactionalremoting.client;

import java.util.Map;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.atomikos.demotransactionalremoting.transactions.AtomikosTransactionService;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class ParticipantAdapter implements Participant {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.createLogger(ParticipantAdapter.class);
	
	private final String uri;
	private final String coordId;
	private AtomikosTransactionService remoteParticipant;
	
	public ParticipantAdapter(String uri, String coordId) {
		this.uri = uri;
		this.coordId = coordId;
		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
		invoker.setServiceUrl(uri);
		invoker.setServiceInterface(AtomikosTransactionService.class);
		invoker.afterPropertiesSet();
		remoteParticipant = (AtomikosTransactionService)invoker.getObject();
	}
	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public void setCascadeList(Map<String, Integer> allParticipants)
			throws SysException {
	}

	@Override
	public void setGlobalSiblingCount(int count) {
	}

	@Override
	public int prepare() throws RollbackException, HeurHazardException,
			HeurMixedException, SysException {
		LOGGER.logDebug("Calling prepare on " + getURI());
		return remoteParticipant.prepare(coordId);
	}

	@Override
	public void commit(boolean onePhase) throws HeurRollbackException,
			HeurHazardException, HeurMixedException, RollbackException,
			SysException {
		 remoteParticipant.commit(coordId, onePhase);

	}

	@Override
	public void rollback() throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException {
		 remoteParticipant.rollback(coordId);

	}

	@Override
	public void forget() {
	}

	@Override
	public boolean isRecoverable() {
		return true;
	}

	@Override
	public String getResourceName() {
		return null;
	}

}
