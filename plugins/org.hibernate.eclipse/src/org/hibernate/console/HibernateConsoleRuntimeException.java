/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.console;

import org.hibernate.HibernateException;

/**
 * @author max
 *
 */
public class HibernateConsoleRuntimeException extends HibernateException {

	
	public HibernateConsoleRuntimeException(String message) {
		super(message);
	}

	public HibernateConsoleRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public HibernateConsoleRuntimeException(Throwable cause) {
		super(cause);
	}
}
