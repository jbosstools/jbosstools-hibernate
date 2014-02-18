package org.jboss.tools.hibernate4_3;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;

public class FakeTransactionManager implements TransactionManager {

	public static FakeTransactionManager INSTANCE;

	private FakeTransaction current;
	ConnectionProvider connections;
	
	public FakeTransactionManager() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		ServiceRegistry buildServiceRegistry = builder.build();
		connections = buildServiceRegistry.getService(ConnectionProvider.class);
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