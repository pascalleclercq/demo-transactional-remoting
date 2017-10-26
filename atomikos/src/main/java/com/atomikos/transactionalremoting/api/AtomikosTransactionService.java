package com.atomikos.transactionalremoting.api;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

public interface AtomikosTransactionService {

     public static final String TRANSACTION_ID = "Transaction-Id";
	public static final String TRANSACTION_EXPIRY = "Transaction-Expiry";
	public static final String ATOMIKOS_COORD_ID = "Atomikos-CoordId";
	public static final String ATOMIKOS_URI = "Atomikos-URI";

	int prepare(String coordId)
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException;

     void commit ( String coordId, boolean onePhase )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException;

     void rollback(String coordId)
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException;
}
