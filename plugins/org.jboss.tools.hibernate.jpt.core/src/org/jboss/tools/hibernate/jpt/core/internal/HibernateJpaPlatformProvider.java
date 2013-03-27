/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
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

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackageFragmentRoot;
import org.eclipse.jpt.common.utility.internal.collection.CollectionTools;
import org.eclipse.jpt.jpa.core.JpaPlatformProvider;
import org.eclipse.jpt.jpa.core.JpaResourceDefinition;
import org.eclipse.jpt.jpa.core.JpaResourceModelProvider;
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
import org.eclipse.jpt.jpa.core.internal.context.java.JavaEmbeddedMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaMappedSuperclassDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaOneToManyMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaSourceFileDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaTransientMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.context.java.JavaVersionMappingDefinition;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmXmlDefinition;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.persistence.GenericPersistenceXmlDefinition;
import org.eclipse.jpt.jpa.core.resource.orm.XmlEntityMappings;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaBasicMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaEntityDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaIdMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaManyToManyMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaManyToOneMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.definition.HibernateJavaOneToOneMappingDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmXmlDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernatePersistenceXmlDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaPlatformProvider extends AbstractJpaPlatformProvider {

	public static final String ID = "hibernate"; //$NON-NLS-1$

	// singleton
	private static final JpaPlatformProvider INSTANCE =
		new HibernateJpaPlatformProvider();


	/**
	 * Return the singleton.
	 */
	public static JpaPlatformProvider instance() {
		return INSTANCE;
	}


	/**
	 * Enforce singleton usage
	 */
	private HibernateJpaPlatformProvider() {
		super();
	}


	// ********** resource models **********

	public JptResourceType getMostRecentSupportedResourceType(IContentType contentType) {
		if (contentType.equals(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE)) {
			return PlatformTools.getResourceType(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE);
		} else if (contentType.equals(JavaResourceCompilationUnit.CONTENT_TYPE)) {
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
		GenericPersistenceXmlDefinition.instance().getResourceType(),
		GenericOrmXmlDefinition.instance().getResourceType()
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
		JavaEmbeddedMappingDefinition.instance(),
		HibernateJavaBasicMappingDefinition.instance()
	};

	@Override
	protected void addSpecifiedJavaAttributeMappingDefinitionsTo(ArrayList<JavaAttributeMappingDefinition> definitions) {
		CollectionTools.addAll(definitions, SPECIFIED_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS);
	}

	// order matches that used by the Reference Implementation (EclipseLink)
	protected static final JavaAttributeMappingDefinition[] SPECIFIED_JAVA_ATTRIBUTE_MAPPING_DEFINITIONS = new JavaAttributeMappingDefinition[] {
		JavaTransientMappingDefinition.instance(),
		HibernateJavaIdMappingDefinition.instance(),
		JavaVersionMappingDefinition.instance(),
		HibernateJavaBasicMappingDefinition.instance(),
		JavaEmbeddedMappingDefinition.instance(),
		JavaEmbeddedIdMappingDefinition.instance(),
		HibernateJavaManyToManyMappingDefinition.instance(),
		HibernateJavaManyToOneMappingDefinition.instance(),
		JavaOneToManyMappingDefinition.instance(),
		HibernateJavaOneToOneMappingDefinition.instance(),
	};


	// ********** resource definitions **********

	@Override
	protected void addResourceDefinitionsTo(ArrayList<JpaResourceDefinition> definitions) {
		CollectionTools.addAll(definitions, RESOURCE_DEFINITIONS);
	}

	protected static final JpaResourceDefinition[] RESOURCE_DEFINITIONS = new JpaResourceDefinition[] {
		HibernatePersistenceXmlDefinition.instance(),
		HibernateOrmXmlDefinition.instance()};

}
