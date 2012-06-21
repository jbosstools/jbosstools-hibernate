/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.ArrayList;

import org.eclipse.jpt.common.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.common.core.resource.java.NestableAnnotationDefinition;
import org.eclipse.jpt.jpa.core.JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaAnnotationDefinitionProvider;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotationImpl.DiscriminatorFormulaAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ForeignKeyAnnotationImpl.ForeignKeyAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GeneratedAnnotationImpl.GeneratedAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorsAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueriesAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueriesAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotationImpl.IndexAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotationImpl.TypeAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefsAnnotationDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaAnnotationDefinitionProvider extends AbstractJpaAnnotationDefinitionProvider
{
	// singleton
	private static final JpaAnnotationDefinitionProvider INSTANCE = new HibernateJpaAnnotationDefinitionProvider();

	/**
	 * Return the singleton.
	 */
	public static JpaAnnotationDefinitionProvider instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private HibernateJpaAnnotationDefinitionProvider() {
		super();
	}

	@Override
	protected void addAnnotationDefinitionsTo(ArrayList<AnnotationDefinition> definitions) {
		//definitions.add(GenericGeneratorAnnotationDefinition.instance());
		definitions.add(GenericGeneratorsAnnotationDefinition.instance());
		//definitions.add(TypeDefAnnotationDefinition.instance());
		definitions.add(TypeDefsAnnotationDefinition.instance());
		//definitions.add(HibernateNamedQueryAnnotationDefinition.instance());
		definitions.add(HibernateNamedQueriesAnnotationDefinition.instance());
		//definitions.add(HibernateNamedNativeQueryAnnotationDefinition.instance());
		definitions.add(HibernateNamedNativeQueriesAnnotationDefinition.instance());
		definitions.add(DiscriminatorFormulaAnnotationDefinition.instance());
		definitions.add(ForeignKeyAnnotationDefinition.instance());
	//addAttributeAnnotationDefinitionsTo
		//definitions.add(GenericGeneratorAnnotationDefinition.instance());
		definitions.add(GeneratedAnnotationDefinition.instance());
		//definitions.add(TypeDefAnnotationDefinition.instance());
		//definitions.add(TypeDefsAnnotationDefinition.instance());
		definitions.add(IndexAnnotationDefinition.instance());
		definitions.add(TypeAnnotationDefinition.instance());
		//definitions.add(ForeignKeyAnnotationDefinition.instance());
	//addPackageAnnotationDefinitionsTo
		//definitions.add(GenericGeneratorAnnotationDefinition.instance());
		//definitions.add(GenericGeneratorsAnnotationDefinition.instance());
		//definitions.add(HibernateNamedQueryAnnotationDefinition.instance());
		//definitions.add(HibernateNamedQueriesAnnotationDefinition.instance());
		//definitions.add(HibernateNamedNativeQueryAnnotationDefinition.instance());
		//definitions.add(HibernateNamedNativeQueriesAnnotationDefinition.instance());
		//definitions.add(TypeDefAnnotationDefinition.instance());
		//definitions.add(TypeDefsAnnotationDefinition.instance());
	}

	@Override
	protected void addNestableAnnotationDefinitionsTo(ArrayList<NestableAnnotationDefinition> definitions) {
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
		definitions.add(TypeDefAnnotationDefinition.instance());
		definitions.add(HibernateNamedQueryAnnotationDefinition.instance());
		definitions.add(HibernateNamedNativeQueryAnnotationDefinition.instance());
	}

}
