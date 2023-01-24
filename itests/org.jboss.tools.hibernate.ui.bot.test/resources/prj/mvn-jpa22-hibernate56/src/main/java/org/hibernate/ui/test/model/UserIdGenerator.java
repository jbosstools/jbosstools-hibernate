package org.hibernate.ui.test.model;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UserIdGenerator implements IdentifierGenerator {

	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {

		return 1;
	}

	public Serializable generate(SharedSessionContractImplementor arg0, Object arg1) throws HibernateException {
		return 1;
	}

}
