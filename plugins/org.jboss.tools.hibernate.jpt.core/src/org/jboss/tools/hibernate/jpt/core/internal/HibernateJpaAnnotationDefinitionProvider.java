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
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.List;

import org.eclipse.jpt.core.JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.core.internal.platform.AbstractJpaAnnotationDefintionProvider;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotationImpl.GenericGeneratorAnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueriesAnnotationDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaAnnotationDefinitionProvider extends AbstractJpaAnnotationDefintionProvider
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
	protected void addTypeMappingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions) {
	}

	@Override
	protected void addTypeSupportingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions) {
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
		definitions.add(HibernateNamedQueryAnnotationDefinition.instance());
		definitions.add(HibernateNamedQueriesAnnotationDefinition.instance());
	}
	
	@Override
	protected void addAttributeMappingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions) {
	}
	
	@Override
	protected void addAttributeSupportingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions) {
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
	}
}
