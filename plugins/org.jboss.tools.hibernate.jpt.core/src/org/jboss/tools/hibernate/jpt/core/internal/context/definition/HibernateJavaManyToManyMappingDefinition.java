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
package org.jboss.tools.hibernate.jpt.core.internal.context.definition;

import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaManyToManyMappingDefinition;


/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaManyToManyMappingDefinition extends AbstractJavaManyToManyMappingDefinition {

	// singleton
	private static final HibernateJavaManyToManyMappingDefinition INSTANCE = new HibernateJavaManyToManyMappingDefinition();

	/**
	 * Return the singleton.
	 */
	public static HibernateJavaManyToManyMappingDefinition instance() {
		return INSTANCE;
	}


	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaManyToManyMappingDefinition() {
		super();
	}

}
