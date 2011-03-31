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
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaOneToOneMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaOneToOneMappingDefinition extends AbstractJavaOneToOneMappingDefinition
{
	// singleton
	private static final HibernateJavaOneToOneMappingDefinition INSTANCE = new HibernateJavaOneToOneMappingDefinition();

	/**
	 * Return the singleton.
	 */
	public static HibernateJavaOneToOneMappingDefinition instance() {
		return INSTANCE;
	}


	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaOneToOneMappingDefinition() {
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