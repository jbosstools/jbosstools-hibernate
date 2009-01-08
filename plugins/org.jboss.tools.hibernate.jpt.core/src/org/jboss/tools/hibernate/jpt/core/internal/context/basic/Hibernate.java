/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.basic;

/**
 * @author Dmitry Geraskov
 *
 */
public interface Hibernate {
	
	// Hibernate package
	String PACKAGE = "org.hibernate.annotations";
	String PACKAGE_ = PACKAGE + ".";
	
	// ********** API **********

	// Hibernate annotations
	String GENERIC_GENERATOR = PACKAGE_ + "GenericGenerator";
		String GENERIC_GENERATOR__NAME = "name";
		String GENERIC_GENERATOR__STRATEGY = "strategy";
		
	
}
