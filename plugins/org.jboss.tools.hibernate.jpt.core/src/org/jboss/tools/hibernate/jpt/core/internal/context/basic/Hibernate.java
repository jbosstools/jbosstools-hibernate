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
	String PACKAGE = "org.hibernate.annotations"; //$NON-NLS-1$
	String PACKAGE_ = PACKAGE + "."; //$NON-NLS-1$
	
	// ********** API **********

	// Hibernate annotations
	String GENERIC_GENERATOR = PACKAGE_ + "GenericGenerator"; //$NON-NLS-1$
		String GENERIC_GENERATOR__NAME = "name"; //$NON-NLS-1$
		String GENERIC_GENERATOR__STRATEGY = "strategy"; //$NON-NLS-1$
		String GENERIC_GENERATOR__PARAMETERS = "parameters"; //$NON-NLS-1$
		
	String GENERATOR_PARAMETER = PACKAGE_ + "Parameter"; //$NON-NLS-1$
		String GENERATOR_PARAMETER__NAME = "name"; //$NON-NLS-1$
		String GENERATOR_PARAMETER__VALUE = "value"; //$NON-NLS-1$
		
	String NAMED_QUERY = PACKAGE_ + "NamedQuery";
		String NAMED_QUERY__NAME = "name";
		String NAMED_QUERY__QUERY = "query";
		String NAMED_QUERY__HINTS = "hints";
		
	String NAMED_QUERIES = PACKAGE_ + "NamedQueries";
		String NAMED_QUERIES__VALUE = "value";
	
}
