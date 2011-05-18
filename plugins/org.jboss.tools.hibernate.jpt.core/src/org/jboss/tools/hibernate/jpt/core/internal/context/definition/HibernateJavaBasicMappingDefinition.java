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
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.MappingKeys;
import org.eclipse.jpt.jpa.core.context.java.DefaultJavaAttributeMappingDefinition;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.resource.java.BasicAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.ColumnAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.EnumeratedAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.LobAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.TemporalAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaBasicMappingDefinition implements DefaultJavaAttributeMappingDefinition
{
	// singleton
	private static final HibernateJavaBasicMappingDefinition INSTANCE = new HibernateJavaBasicMappingDefinition();

	/**
	 * Return the singleton.
	 */
	public static HibernateJavaBasicMappingDefinition instance() {
		return INSTANCE;
	}

	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaBasicMappingDefinition() {
		super();
	}
	
	public String getKey() {
		return MappingKeys.BASIC_ATTRIBUTE_MAPPING_KEY;
	}

	public String getAnnotationName() {
		return BasicAnnotation.ANNOTATION_NAME;
	}

	public boolean isSpecified(JavaPersistentAttribute persistentAttribute) {
		return persistentAttribute.getResourcePersistentAttribute().getAnnotation(this.getAnnotationName()) != null;
	}

	protected static final String[] HIBERNATE_ANNOTATION_NAMES_ARRAY = new String[] {
		Hibernate.GENERATED,
		Hibernate.INDEX,
		Hibernate.TYPE
	};
	
	private static final String[] SUPPORTING_ANNOTATION_NAMES_ARRAY = new String[] {
		ColumnAnnotation.ANNOTATION_NAME,
		LobAnnotation.ANNOTATION_NAME,
		TemporalAnnotation.ANNOTATION_NAME,
		EnumeratedAnnotation.ANNOTATION_NAME
	};
	private static final Iterable<String> SUPPORTING_ANNOTATION_NAMES = new ArrayIterable<String>(SUPPORTING_ANNOTATION_NAMES_ARRAY);


	@Override
	public Iterable<String> getSupportingAnnotationNames() {
		return new CompositeIterable<String>(SUPPORTING_ANNOTATION_NAMES, new ArrayIterable<String>(HIBERNATE_ANNOTATION_NAMES_ARRAY));
	}
	
	public JavaAttributeMapping buildMapping(JavaPersistentAttribute persistentAttribute, JpaFactory factory) {
		return factory.buildJavaBasicMapping(persistentAttribute);
	}

	public boolean isDefault(JavaPersistentAttribute persistentAttribute) {
		return persistentAttribute.typeIsBasic();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}