package com.atomikos.demotransactionalremoting;

import com.atomikos.demotransactionalremoting.transactions.AtomikosTransactionService;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosTransactionPort implements AtomikosTransactionService {

	private static final Logger LOGGER = LoggerFactory
			.createLogger(AtomikosTransactionPort.class);

	@Override
	public int prepare(String coordId) throws RollbackException,
			HeurHazardException, HeurMixedException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("prepare ( ... ) received for root " + coordId);
		}
		TransactionService service_ = Configuration.getTransactionService();

		Participant part = service_.getParticipant(coordId);

		return part.prepare();

	}

	@Override
	public void commit(String coordId, boolean onePhase)
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("commit() received for root " + coordId + " onePhase = " + onePhase);
		}
		TransactionService service_ = Configuration.getTransactionService();
		Participant part = service_.getParticipant(coordId);
		if (part == null && !onePhase) {
				throw new HeurMixedException();
		} else {
			part.commit(onePhase);
		}

	}

	@Override
	public void rollback(String coordId) throws HeurCommitException,
			HeurMixedException, HeurHazardException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("rollback() received for root " + coordId);
		}
		TransactionService service_ = Configuration.getTransactionService();
		Participant part = service_.getParticipant(coordId);
		if (part != null) {

			part.rollback();
		}

	}

}
