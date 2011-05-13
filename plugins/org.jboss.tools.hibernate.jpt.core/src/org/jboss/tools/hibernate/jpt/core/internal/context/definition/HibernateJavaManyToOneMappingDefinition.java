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

import org.eclipse.jpt.common.utility.internal.iterables.ArrayIterable;
import org.eclipse.jpt.common.utility.internal.iterables.CompositeIterable;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaManyToOneMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaManyToOneMappingDefinition extends AbstractJavaManyToOneMappingDefinition
{
	// singleton
	private static final HibernateJavaManyToOneMappingDefinition INSTANCE = new HibernateJavaManyToOneMappingDefinition();

	/**
	 * Return the singleton.
	 */
	public static JavaAttributeMappingDefinition instance() {
		return INSTANCE;
	}


	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaManyToOneMappingDefinition() {
		super();
	}

	protected static final String[] HIBERNATE_ANNOTATION_NAMES_ARRAY = new String[] {
		Hibernate.FOREIGN_KEY,
	};

	@Override
	public Iterable<String> getSupportingAnnotationNames() {
		return new CompositeIterable<String>(super.getSupportingAnnotationNames(), new ArrayIterable<String>(HIBERNATE_ANNOTATION_NAMES_ARRAY));
	}
}