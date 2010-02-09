package org.hibernate.console;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.transaction.TransactionManagerLookup;

public class FakeTransactionManagerLookup implements TransactionManagerLookup {

	public TransactionManager getTransactionManager(Properties props)
	throws HibernateException {
		if ( FakeTransactionManager.INSTANCE == null ) {
			FakeTransactionManager.INSTANCE = new FakeTransactionManager(props);
		}
		return FakeTransactionManager.INSTANCE;
	}

	public String getUserTransactionName() {
		return null; //throw new UnsupportedOperationException();
	}

	public Object getTransactionIdentifier(Transaction transaction) {		
		return transaction;
	}

}

class FakeTransactionManager implements TransactionManager {

	public static FakeTransactionManager INSTANCE;

	private FakeTransaction current;
	ConnectionProvider connections;
	
	public FakeTransactionManager(Properties props) {
		connections = ConnectionProviderFactory.newConnectionProvider();
	}
	
	public void begin() throws NotSupportedException, SystemException {
		current = new FakeTransaction(this);
		current.begin();
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		if(current!=null) current.commit();
	}


	public int getStatus() throws SystemException {
		if(current!=null) {
			return current.getStatus();
		} else {
			return Status.STATUS_NO_TRANSACTION;
		}
	}

	public Transaction getTransaction() throws SystemException {
		return current;
	}

	public void resume(Transaction tx) throws InvalidTransactionException,
			IllegalStateException, SystemException {
		current = (FakeTransaction) tx;		
	}

	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		if(current!=null) current.rollback();
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		if(current!=null) current.setRollbackOnly();
	}

	public void setTransactionTimeout(int t) throws SystemException {
	}
	
	public Transaction suspend() throws SystemException {
		Transaction result = current;
		current = null;
		return result;
	}

	public FakeTransaction getCurrent() {
		return current;
	}
	
	void endCurrent(FakeTransaction tx) {
		if (current==tx) current=null;
	}

}

class FakeTransaction implements Transaction {
	
	int status;
	private Connection connection;
	List<Synchronization> synchronizations = new ArrayList<Synchronization>();
	private FakeTransactionManager transactionManager;
	
	FakeTransaction(FakeTransactionManager transactionManager) {
		status = Status.STATUS_NO_TRANSACTION;
		this.transactionManager = transactionManager;
	}
	
	public void begin() throws SystemException {
		try {
			connection = transactionManager.connections.getConnection();
		}
		catch (SQLException sqle) {
			
			sqle.printStackTrace();
			throw new SystemException(sqle.toString());
		}
		status = Status.STATUS_ACTIVE;
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		
		if (status == Status.STATUS_MARKED_ROLLBACK) {
			rollback();
		}
		else {
			status = Status.STATUS_PREPARING;
			
			for (Synchronization s : synchronizations) {
				s.beforeCompletion();
			}
			
			status = Status.STATUS_COMMITTING;
			
			try {
				connection.commit();
				connection.close();
			}
			catch (SQLException sqle) {
				status = Status.STATUS_UNKNOWN;
				throw new SystemException();
			}
			
			status = Status.STATUS_COMMITTED;

			for (Synchronization s : synchronizations) {
				s.afterCompletion(status);
			}
			
			//status = Status.STATUS_NO_TRANSACTION;
			transactionManager.endCurrent(this);
		}

	}
	
	public boolean delistResource(XAResource arg0, int arg1)
			throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean enlistResource(XAResource arg0) throws RollbackException,
			IllegalStateException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getStatus() throws SystemException {
		return status;
	}
	
	public void registerSynchronization(Synchronization sync)
			throws RollbackException, IllegalStateException, SystemException {
		synchronizations.add(sync);
	}
	
	public void rollback() throws IllegalStateException, SystemException {

		status = Status.STATUS_ROLLING_BACK;

// Synch.beforeCompletion() should *not* be called for rollbacks
//		for ( int i=0; i<synchronizations.size(); i++ ) {
//			Synchronization s = (Synchronization) synchronizations.get(i);
//			s.beforeCompletion();
//		}
		
		status = Status.STATUS_ROLLEDBACK;
		
		try {
			connection.rollback();
			connection.close();
		}
		catch (SQLException sqle) {
			status = Status.STATUS_UNKNOWN;
			throw new SystemException();
		}
		
		for (Synchronization s : synchronizations) {
			s.afterCompletion(status);
		}
		
		//status = Status.STATUS_NO_TRANSACTION;
		transactionManager.endCurrent(this);
	}
	
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		status = Status.STATUS_MARKED_ROLLBACK;
	}

	void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}
}
