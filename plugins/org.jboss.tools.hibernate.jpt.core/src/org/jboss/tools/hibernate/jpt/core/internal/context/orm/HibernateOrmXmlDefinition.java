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
package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.jpt.core.JpaResourceType;
import org.eclipse.jpt.core.JptCorePlugin;
import org.eclipse.jpt.core.context.orm.NullOrmAttributeMappingDefinition;
import org.eclipse.jpt.core.context.orm.OrmAttributeMappingDefinition;
import org.eclipse.jpt.core.context.orm.OrmTypeMappingDefinition;
import org.eclipse.jpt.core.context.orm.OrmXmlContextNodeFactory;
import org.eclipse.jpt.core.context.orm.OrmXmlDefinition;
import org.eclipse.jpt.core.internal.context.orm.AbstractOrmXmlDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmBasicMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmEmbeddableDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmEmbeddedIdMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmEmbeddedMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmEntityDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmIdMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmManyToManyMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmManyToOneMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmMappedSuperclassDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmOneToManyMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmOneToOneMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmTransientMappingDefinition;
import org.eclipse.jpt.core.internal.context.orm.OrmVersionMappingDefinition;
import org.eclipse.jpt.core.resource.orm.OrmFactory;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXmlDefinition extends AbstractOrmXmlDefinition {

	// singleton
	private static final OrmXmlDefinition INSTANCE = 
			new HibernateOrmXmlDefinition();
	
	
	/**
	 * Return the singleton
	 */
	public static OrmXmlDefinition instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private HibernateOrmXmlDefinition() {
		super();
	}
	
	
	public EFactory getResourceNodeFactory() {
		return OrmFactory.eINSTANCE;
	}
	
	@Override
	protected OrmXmlContextNodeFactory buildContextNodeFactory() {
		return new HibernateOrmXmlContextNodeFactory();
	}
	
	public JpaResourceType getResourceType() {
		return JptCorePlugin.ORM_XML_1_0_RESOURCE_TYPE;
	}
	
	
	// ********** ORM type mappings **********
	
	@Override
	protected OrmTypeMappingDefinition[] buildOrmTypeMappingDefinitions() {
		// order should not matter here, but we'll use the same order as for java 
		// (@see {@link GenericJpaPlatformProvider})
		return new OrmTypeMappingDefinition[] {
			OrmEntityDefinition.instance(),
			OrmEmbeddableDefinition.instance(),
			OrmMappedSuperclassDefinition.instance()};
	}
	
	
	// ********** ORM attribute mappings **********
	
	@Override
	protected OrmAttributeMappingDefinition[] buildOrmAttributeMappingDefinitions() {
		// order should not matter here, but we'll use the same order as for java
		// (@see {@link GenericJpaPlatformProvider})
		return new OrmAttributeMappingDefinition[] {
			OrmTransientMappingDefinition.instance(),
			OrmIdMappingDefinition.instance(),
			OrmVersionMappingDefinition.instance(),
			OrmBasicMappingDefinition.instance(),
			OrmEmbeddedMappingDefinition.instance(),
			OrmEmbeddedIdMappingDefinition.instance(),
			OrmManyToManyMappingDefinition.instance(),
			OrmManyToOneMappingDefinition.instance(),
			OrmOneToManyMappingDefinition.instance(),
			OrmOneToOneMappingDefinition.instance(),
			NullOrmAttributeMappingDefinition.instance()};
	}
}
