/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java;

import java.util.List;

import org.eclipse.jpt.jpa.core.context.AttributeMapping;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.ui.ResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.details.java.DefaultJavaAttributeMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.details.java.JavaAttributeMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.details.java.JavaTypeMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.details.java.JavaUiFactory;
import org.eclipse.jpt.jpa.ui.internal.details.java.AbstractJavaResourceUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.DefaultBasicMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.DefaultEmbeddedMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaBasicMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaEmbeddableUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaEmbeddedIdMappingUDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaEmbeddedMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaEntityUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaIdMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaManyToManyMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaManyToOneMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaMappedSuperclassUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaOneToManyMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaOneToOneMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaTransientMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaVersionMappingUiDefinition;
import org.eclipse.jpt.jpa.ui.internal.details.java.NullJavaAttributeMappingUiDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaResourceUiDefinition extends AbstractJavaResourceUiDefinition {
	
	// singleton
	private static final ResourceUiDefinition INSTANCE = new HibernateJavaResourceUiDefinition();

	/**
	 * Return the singleton.
	 */
	public static ResourceUiDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * zero-argument constructor
	 */
	protected HibernateJavaResourceUiDefinition() {
		super();
	}
	
	@Override
	protected JavaUiFactory buildJavaUiFactory() {
		return new HibernateJavaUiFactory();
	}
	
	@Override
	protected void addSpecifiedAttributeMappingUiDefinitionsTo(List<JavaAttributeMappingUiDefinition<? extends AttributeMapping>> definitions) {
		definitions.add(JavaIdMappingUiDefinition.instance());
		definitions.add(JavaEmbeddedIdMappingUDefinition.instance());
		definitions.add(JavaBasicMappingUiDefinition.instance());
		definitions.add(JavaVersionMappingUiDefinition.instance());
		definitions.add(JavaManyToOneMappingUiDefinition.instance());
		definitions.add(JavaOneToManyMappingUiDefinition.instance());
		definitions.add(JavaOneToOneMappingUiDefinition.instance());
		definitions.add(JavaManyToManyMappingUiDefinition.instance());
		definitions.add(JavaEmbeddedMappingUiDefinition.instance());
		definitions.add(JavaTransientMappingUiDefinition.instance());
	}
	
	@Override
	protected void addDefaultAttributeMappingUiDefinitionsTo(List<DefaultJavaAttributeMappingUiDefinition<?>> definitions) {
		definitions.add(DefaultBasicMappingUiDefinition.instance());
		definitions.add(DefaultEmbeddedMappingUiDefinition.instance());
		definitions.add(NullJavaAttributeMappingUiDefinition.instance());
	}
	
	@Override
	protected void addSpecifiedTypeMappingUiDefinitionsTo(List<JavaTypeMappingUiDefinition<? extends TypeMapping>> definitions) {
		definitions.add(JavaEntityUiDefinition.instance());
		definitions.add(JavaMappedSuperclassUiDefinition.instance());
		definitions.add(JavaEmbeddableUiDefinition.instance());
	}

}
