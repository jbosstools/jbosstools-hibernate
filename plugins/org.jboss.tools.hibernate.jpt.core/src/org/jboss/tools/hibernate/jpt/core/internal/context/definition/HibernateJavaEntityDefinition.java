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
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.MappingKeys;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaTypeMappingDefinition;
import org.eclipse.jpt.jpa.core.resource.java.Annotation;
import org.eclipse.jpt.jpa.core.resource.java.AssociationOverrideAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.AssociationOverridesAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.AttributeOverrideAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.AttributeOverridesAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.DiscriminatorColumnAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.DiscriminatorValueAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.EntityAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.IdClassAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.InheritanceAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.JPA;
import org.eclipse.jpt.jpa.core.resource.java.NamedNativeQueriesAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.NamedNativeQueryAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.NamedQueriesAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.NamedQueryAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.PrimaryKeyJoinColumnAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.PrimaryKeyJoinColumnsAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.SecondaryTableAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.SecondaryTablesAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.SequenceGeneratorAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.TableAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.TableGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaEntityDefinition implements JavaTypeMappingDefinition
{
	// singleton
	private static final HibernateJavaEntityDefinition INSTANCE = new HibernateJavaEntityDefinition();

	/**
	 * Return the singleton
	 */
	public static HibernateJavaEntityDefinition instance() {
		return INSTANCE;
	}


	/**
	 * Enforce singleton usage
	 */
	private HibernateJavaEntityDefinition() {
		super();
	}

	public String getKey() {
		return MappingKeys.ENTITY_TYPE_MAPPING_KEY;
	}

	public String getAnnotationName() {
		return EntityAnnotation.ANNOTATION_NAME;
	}

	public Iterable<String> getSupportingAnnotationNames() {
		return SUPPORTING_ANNOTATION_NAMES;
	}

	protected static final String[] SUPPORTING_ANNOTATION_NAMES_ARRAY = new String[] {
			TableAnnotation.ANNOTATION_NAME,
			SecondaryTableAnnotation.ANNOTATION_NAME,
			SecondaryTablesAnnotation.ANNOTATION_NAME,
			PrimaryKeyJoinColumnAnnotation.ANNOTATION_NAME,
			PrimaryKeyJoinColumnsAnnotation.ANNOTATION_NAME,
			IdClassAnnotation.ANNOTATION_NAME,
			InheritanceAnnotation.ANNOTATION_NAME,
			DiscriminatorValueAnnotation.ANNOTATION_NAME,
			DiscriminatorColumnAnnotation.ANNOTATION_NAME,
			SequenceGeneratorAnnotation.ANNOTATION_NAME,
			TableGeneratorAnnotation.ANNOTATION_NAME,
			NamedQueryAnnotation.ANNOTATION_NAME,
			NamedQueriesAnnotation.ANNOTATION_NAME,
			NamedNativeQueryAnnotation.ANNOTATION_NAME,
			NamedNativeQueriesAnnotation.ANNOTATION_NAME,
			JPA.SQL_RESULT_SET_MAPPING,
			JPA.EXCLUDE_DEFAULT_LISTENERS,
			JPA.EXCLUDE_SUPERCLASS_LISTENERS,
			JPA.ENTITY_LISTENERS,
			JPA.PRE_PERSIST,
			JPA.POST_PERSIST,
			JPA.PRE_REMOVE,
			JPA.POST_REMOVE,
			JPA.PRE_UPDATE,
			JPA.POST_UPDATE,
			JPA.POST_LOAD,
			AttributeOverrideAnnotation.ANNOTATION_NAME,
			AttributeOverridesAnnotation.ANNOTATION_NAME,
			AssociationOverrideAnnotation.ANNOTATION_NAME,
			AssociationOverridesAnnotation.ANNOTATION_NAME,
			//TODO add Hibernate annotations here
			Hibernate.GENERIC_GENERATOR,
			Hibernate.GENERIC_GENERATORS,
			Hibernate.TYPE_DEF,
			Hibernate.TYPE_DEFS,
			Hibernate.NAMED_QUERY,
			Hibernate.NAMED_QUERIES,
			Hibernate.NAMED_NATIVE_QUERY,
			Hibernate.NAMED_NATIVE_QUERIES,
			Hibernate.DISCRIMINATOR_FORMULA,
	};
	protected static final Iterable<String> SUPPORTING_ANNOTATION_NAMES = new ArrayIterable<String>(SUPPORTING_ANNOTATION_NAMES_ARRAY);

	public JavaEntity buildMapping(JavaPersistentType persistentType, Annotation mappingAnnotation, JpaFactory factory) {
		return factory.buildJavaEntity(persistentType, (EntityAnnotation) mappingAnnotation);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
