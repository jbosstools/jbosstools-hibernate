/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console.ext;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public HibernateException(RuntimeException cause) {
		super(cause);
	}
	
	public HibernateException(String message){
		super(message);
	}

}
