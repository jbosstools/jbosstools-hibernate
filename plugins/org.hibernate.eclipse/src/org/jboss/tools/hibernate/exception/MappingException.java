package org.jboss.tools.hibernate.exception;


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
