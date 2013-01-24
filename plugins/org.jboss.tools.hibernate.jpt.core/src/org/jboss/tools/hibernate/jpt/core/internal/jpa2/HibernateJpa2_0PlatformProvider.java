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
package org.jboss.tools.hibernate.jpt.core.internal.jpa2;

import java.util.ArrayList;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackageFragmentRoot;
import org.eclipse.jpt.common.utility.internal.collection.CollectionTools;
import org.eclipse.jpt.jpa.core.JpaPlatformProvider;
import org.eclipse.jpt.jpa.core.JpaResourceModelProvider;
import org.eclipse.jpt.jpa.core.ResourceDefinition;
import org.eclipse.jpt.jpa.core.context.java.DefaultJavaAttributeMappingDefinition;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMappingDefinition;
import org.eclipse.jpt.jpa.core.context.java.JavaTypeMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaPlatformProvider;
import org.eclipse.jpt.jpa.core.internal.JarResourceModelProvider;
import org.eclipse.jpt.jpa.core.internal.JavaResourceModelProvider;
import org.eclipse.jpt.jpa.core.internal.OrmResourceModelProvider;
import org.eclipse.jpt.jpa.core.internal.PersistenceResourceModelProvider;
import org.eclipse.jpt.jpa.core.internal.context.java.JarDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaEmbeddableDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaEmbeddedIdMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaMappedSuperclassDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaSourceFileDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaTransientMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaVersionMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.JavaElementCollectionMappingDefinition2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.JavaEmbeddedMappingDefinition2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.JavaOneToManyMappingDefinition2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.orm.GenericOrmXml2_0Definition;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.persistence.GenericPersistenceXml2_0Definition;
import org.eclipse.jpt.jpa.core.resource.orm.XmlEntityMappings;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.jboss.tools.hibernate.jpt.core.internal.HibernatePropertiesResourceModelProvider;
import org.jboss.tools.hibernate.jpt.core.internal.JavaPackageInfoResourceModelProviderPatched;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaBasicMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaEntityDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmXmlDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.jpa2.HibernateOrmXml2_0Definition;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernatePersistenceXmlDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.jpa2.Hibernate2_0PersistenceXmlDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.jpa2.context.definition.HibernateJavaIdMappingDefinition2_0;
import org.jboss.tools.hibernate.jpt.core.internal.jpa2.context.definition.HibernateJavaManyToManyMappingDefinition2_0;
import org.jboss.tools.hibernate.jpt.core.internal.jpa2.context.definition.HibernateJavaManyToOneMappingDefinition2_0;
import org.jboss.tools.hibernate.jpt.core.internal.jpa2.context.definition.HibernateJavaOneToOneMappingDefinition2_0;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpa2_0PlatformProvider extends AbstractJpaPlatformProvider {

	public static final String ID = "hibernate2_0"; //$NON-NLS-1$

	// singleton
	private static final JpaPlatformProvider INSTANCE =
		new HibernateJpa2_0PlatformProvider();


	/**
	 * Return the singleton.
	 */
	public static JpaPlatformProvider instance() {
		return INSTANCE;
	}

	/**
	 * Enforce singleton usage
	 */
	private HibernateJpa2_0PlatformProvider() {
		super();
	}


	// ********** resource models **********

	public JptResourceType getMostRecentSupportedResourceType(IContentType contentType) {
		if (contentType.equals(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE)) {
			return PlatformTools.getResourceType(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE);
		}
		else if (contentType.equals(JavaResourceCompilationUnit.CONTENT_TYPE)) {
			return PlatformTools.getResourceType(JavaResourceCompilationUnit.CONTENT_TYPE);
		}
		else if (contentType.equals(JavaResourcePackageFragmentRoot.JAR_CONTENT_TYPE)) {
			return PlatformTools.getResourceType(JavaResourcePackageFragmentRoot.JAR_CONTENT_TYPE);
		}
		else if (contentType.equals(XmlPersistence.CONTENT_TYPE)) {
			return PlatformTools.getResourceType(XmlPersistence.CONTENT_TYPE);
		}
		else if (contentType.equals(XmlEntityMappings.CONTENT_TYPE)) {
			return PlatformTools.getResourceType(XmlEntityMappings.CONTENT_TYPE);
		}
		else {
			throw new IllegalArgumentException(contentType.toString());
		}
	}

	@Override
	protected void addMostRecentSupportedResourceTypesTo(ArrayList<JptResourceType> types) {
		CollectionTools.addAll(types, MOST_RECENT_SUPPORTED_RESOURCE_TYPES);
	}

	// order should not be important here
	protected static final JptResourceType[] MOST_RECENT_SUPPORTED_RESOURCE_TYPES = new JptResourceType[] {
		JavaSourceFileDefinition.instance().getResourceType(),
		JarDefinition.instance().getResourceType(),
		GenericPersistenceXml2_0Definition.instance().getResourceType(),
		GenericOrmXml2_0Definition.instance().getResourceType()
	};

	@Override
	protected void addResourceModelProvidersTo(ArrayList<JpaResourceModelProvider> providers) {
		CollectionTools.addAll(providers, RESOURCE_MODEL_PROVIDERS);
	}

	// order should not be important here
	protected static final JpaResourceModelProvider[] RESOURCE_MODEL_PROVIDERS = new JpaResourceModelProvider[] {
		JavaPackageInfoResourceModelProviderPatched.instance(),//replace with original when NPE will be fixed
		JavaResourceModelProvider.instance(),
		JarResourceModelProvider.instance(),
		PersistenceResourceModelProvider.instance(),
		OrmResourceModelProvider.instance(),
		HibernatePropertiesResourceModelProvider.instance()
	};


	// ********** Java type mappings **********

	@Override
	protected void addJavaTypeMappingDefinitionsTo(ArrayList<JavaTypeMappingDefinition> definitions) {
		CollectionTools.addAll(definitions, JAVA_TYPE_MAPPING_DEFINITIONS);
	}

	// order matches that used by the Reference Implementation (EclipseLink)
	protected static final JavaTypeMappingDefinition[] JAVA_TYPE_MAPPING_DEFINITIONS = new JavaTypeMappingDefinition[] {
		HibernateJavaEntityDefinition.instance(),
		JavaEmbeddableDefinition.instance(),
		JavaMappedSuperclassDefinition.instance()
	};


	// ********** Java attribute mappings **********

	@Override
	protected void addDefaultJavaAttributeMappingDefinitionsTo(ArrayList<DefaultJavaAttributeMappingDefinition> definitions) {
		CollectionTools.addAll(definitions, DEFAULT_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS);
	}

	// order matches that used by the Reference Implementation (EclipseLink)
	protected static final DefaultJavaAttributeMappingDefinition[] DEFAULT_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS = new DefaultJavaAttributeMappingDefinition[] {
		JavaEmbeddedMappingDefinition2_0.instance(),
		HibernateJavaBasicMappingDefinition.instance()
	};

	@Override
	protected void addSpecifiedJavaAttributeMappingDefinitionsTo(ArrayList<JavaAttributeMappingDefinition> definitions) {
		CollectionTools.addAll(definitions, SPECIFIED_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS);
	}

	// order matches that used by the Reference Implementation (EclipseLink)
	protected static final JavaAttributeMappingDefinition[] SPECIFIED_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS = new JavaAttributeMappingDefinition[] {
		JavaTransientMappingDefinition.instance(),
		JavaElementCollectionMappingDefinition2_0.instance(),
		HibernateJavaIdMappingDefinition2_0.instance(),
		JavaVersionMappingDefinition.instance(),
		HibernateJavaBasicMappingDefinition.instance(),
		JavaEmbeddedMappingDefinition2_0.instance(),
		JavaEmbeddedIdMappingDefinition.instance(),
		HibernateJavaManyToManyMappingDefinition2_0.instance(),
		HibernateJavaManyToOneMappingDefinition2_0.instance(),
		JavaOneToManyMappingDefinition2_0.instance(),
		HibernateJavaOneToOneMappingDefinition2_0.instance()
	};


	// ********** resource definitions **********

	@Override
	protected void addResourceDefinitionsTo(ArrayList<ResourceDefinition> definitions) {
		CollectionTools.addAll(definitions, RESOURCE_DEFINITIONS);
	}

	protected static final ResourceDefinition[] RESOURCE_DEFINITIONS = new ResourceDefinition[] {
		HibernatePersistenceXmlDefinition.instance(),
		Hibernate2_0PersistenceXmlDefinition.instance(),
		HibernateOrmXmlDefinition.instance(),
		HibernateOrmXml2_0Definition.instance()
	};

}
