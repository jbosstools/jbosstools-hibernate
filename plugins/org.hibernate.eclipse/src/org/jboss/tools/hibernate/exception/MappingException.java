package org.jboss.tools.hibernate.exception;

import org.jboss.tools.hibernate.spi.HibernateException;


@SuppressWarnings("serial")
public class MappingException extends HibernateException {


	public MappingException(String msg, Throwable root) {
		super( msg, root );
	}

	public MappingException(Throwable root) {
		super(root);
	}

	public MappingException(String s) {
		super(s);
	}

}
