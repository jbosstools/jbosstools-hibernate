package org.jboss.tools.hibernate4_3;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;

@SuppressWarnings("serial")
public class FakeJtaPlatform  implements JtaPlatform {

	@Override
	public TransactionManager retrieveTransactionManager() {
		if ( FakeTransactionManager.INSTANCE == null ) {
			FakeTransactionManager.INSTANCE = new FakeTransactionManager();
		}
		return FakeTransactionManager.INSTANCE;
	}

	@Override
	public UserTransaction retrieveUserTransaction() {
		return null;
	}

	@Override
	public Object getTransactionIdentifier(Transaction transaction) {
		return transaction;
	}

	@Override
	public boolean canRegisterSynchronization() {
		return false;
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) {
	}

	@Override
	public int getCurrentStatus() throws SystemException {
		return 0;
	}

}
