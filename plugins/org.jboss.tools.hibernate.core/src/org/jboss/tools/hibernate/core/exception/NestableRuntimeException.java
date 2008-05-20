/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core.exception;


/**
 * The base class of all runtime exceptions which can contain other
 * exceptions.
 */
public class NestableRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	/**
	 * Holds the reference to the exception or error that caused
	 * this exception to be thrown.
	 */
	private Throwable cause = null;

	/**
	 * Constructs a new <code>NestableRuntimeException</code> without specified
	 * detail message.
	 */
	public NestableRuntimeException() {
		super();
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified
	 * detail message.
	 *
	 * @param msg the error message
	 */
	public NestableRuntimeException(String msg) {
		super( msg );
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified
	 * nested <code>Throwable</code>.
	 *
	 * @param cause the exception or error that caused this exception to be
	 *              thrown
	 */
	public NestableRuntimeException(Throwable cause) {
		super();
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>NestableRuntimeException</code> with specified
	 * detail message and nested <code>Throwable</code>.
	 *
	 * @param msg   the error message
	 * @param cause the exception or error that caused this exception to be
	 *              thrown
	 */
	public NestableRuntimeException(String msg, Throwable cause) {
		super( msg );
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	/**
	 * Returns the detail message string of this throwable. If it was
	 * created with a null message, returns the following:
	 * ( cause==null ? null : cause.toString( ).
	 */
	public String getMessage() {
		if ( super.getMessage() != null ) {
			return super.getMessage();
		}
		else if ( cause != null ) {
			return cause.toString();
		}
		else {
			return null;
		}
	}

}
