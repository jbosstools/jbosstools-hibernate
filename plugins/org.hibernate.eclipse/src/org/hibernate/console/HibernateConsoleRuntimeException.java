/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.console;

/**
 * @author max
 *
 */
public class HibernateConsoleRuntimeException extends RuntimeException {

	
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
