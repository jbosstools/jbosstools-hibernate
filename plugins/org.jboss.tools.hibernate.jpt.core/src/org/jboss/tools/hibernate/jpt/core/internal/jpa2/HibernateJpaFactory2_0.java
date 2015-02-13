/*******************************************************************************
  * Copyright (c) 2008-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.jpa2;


import org.eclipse.jpt.common.core.resource.java.JavaResourceType;
import org.eclipse.jpt.jpa.core.JpaDataSource;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.Orderable;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaAssociationOverrideContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaEmbeddable;
import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaNamedQuery;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaSequenceGenerator;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedColumn.ParentAdapter;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaAssociationOverrideContainer;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaColumn;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaNamedQuery;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaOrderable;
import org.eclipse.jpt.jpa.core.internal.jpa2.GenericJpaDatabaseIdentifierAdapter2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.GenericMetamodelSynchronizer2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaCacheable2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaCollectionTable2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaDerivedIdentity2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaEmbeddable2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaOrderColumn2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaOrphanRemoval2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaPersistentType2_0;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.GenericJavaSequenceGenerator2_0;
import org.eclipse.jpt.jpa.core.jpa2.JpaFactory2_0;
import org.eclipse.jpt.jpa.core.jpa2.JpaProject2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.MetamodelSourceType2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.OrphanRemovable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.OrphanRemovalMapping2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaCacheableReference2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaCollectionTable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaDerivedIdentity2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaElementCollectionMapping2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaOrderable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaSingleRelationshipMapping2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaSpecifiedOrderColumn2_0;
import org.eclipse.jpt.jpa.core.jpa2.resource.java.NamedQueryAnnotation2_0;
import org.eclipse.jpt.jpa.core.jpa2.resource.java.SequenceGeneratorAnnotation2_0;
import org.eclipse.jpt.jpa.core.resource.java.EmbeddableAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.NamedQueryAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.SequenceGeneratorAnnotation;
import org.eclipse.jpt.jpa.db.DatabaseIdentifierAdapter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2.HibernateJavaElementCollectionMapping2_0;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;



/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaFactory2_0 extends HibernateAbstractJpaFactory implements JpaFactory2_0 {

	// ********** Hibernate Specific **********
	public JavaElementCollectionMapping2_0 buildJavaElementCollectionMapping2_0(JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaElementCollectionMapping2_0(parent);
	}

	// ********** From GenericJpa2_0 Model **********
	// ********** Core Model **********

	@Override
	public JpaProject buildJpaProject(JpaProject.Config config) {
		if ( ! (config instanceof JpaProject2_0.Config)) {
			throw new IllegalArgumentException("config must be 2.0-compatible: " + config); //$NON-NLS-1$
		}
		return super.buildJpaProject(config);
	}

	public MetamodelSourceType2_0.Synchronizer buildMetamodelSynchronizer(MetamodelSourceType2_0 sourceType) {
		return new GenericMetamodelSynchronizer2_0(sourceType);
	}

	public DatabaseIdentifierAdapter buildDatabaseIdentifierAdapter(JpaDataSource dataSource) {
		return new GenericJpaDatabaseIdentifierAdapter2_0(dataSource);
	}


	// ********** Java Context Model **********
	@Override
	public JavaPersistentType buildJavaPersistentType(PersistentType.Parent owner,
			JavaResourceType jrt) {
		return new GenericJavaPersistentType2_0(owner, jrt);
	}
	
	@Override
	public JavaEmbeddable buildJavaEmbeddable(JavaPersistentType parent, EmbeddableAnnotation embeddableAnnotation) {
		return new GenericJavaEmbeddable2_0(parent, embeddableAnnotation);
	}

	@Override
	public JavaSequenceGenerator buildJavaSequenceGenerator(JavaGeneratorContainer parent, SequenceGeneratorAnnotation annotation) {
		return new GenericJavaSequenceGenerator2_0(parent, (SequenceGeneratorAnnotation2_0) annotation);
	}

	//The 2.0 JPA spec supports association overrides on an embedded mapping while the 1.0 spec did not
	public JavaAssociationOverrideContainer buildJavaAssociationOverrideContainer(JavaAssociationOverrideContainer.ParentAdapter owner) {
		return new GenericJavaAssociationOverrideContainer(owner);
	}

	public JavaDerivedIdentity2_0 buildJavaDerivedIdentity(JavaSingleRelationshipMapping2_0 parent) {
		return new GenericJavaDerivedIdentity2_0(parent);
	}

	public Cacheable2_0 buildJavaCacheable(JavaCacheableReference2_0 parent) {
		return new GenericJavaCacheable2_0(parent);
	}

	public OrphanRemovable2_0 buildJavaOrphanRemoval(OrphanRemovalMapping2_0 parent) {
		return new GenericJavaOrphanRemoval2_0(parent);
	}

	@Override
	public JavaNamedQuery buildJavaNamedQuery(JavaQueryContainer parent, NamedQueryAnnotation annotation) {
		return new GenericJavaNamedQuery(parent, (NamedQueryAnnotation2_0) annotation);
	}

	public JavaCollectionTable2_0 buildJavaCollectionTable( JavaCollectionTable2_0.ParentAdapter owner) {
		return new GenericJavaCollectionTable2_0(owner);
	}

	public JavaSpecifiedOrderColumn2_0 buildJavaOrderColumn(JavaSpecifiedOrderColumn2_0.ParentAdapter owner) {
		return new GenericJavaOrderColumn2_0(owner);
	}

	public JavaSpecifiedColumn buildJavaMapKeyColumn(ParentAdapter owner) {
		return new GenericJavaColumn(owner);
	}

	public JavaOrderable2_0 buildJavaOrderable(JavaOrderable2_0.ParentAdapter owner) {
		return new GenericJavaOrderable(owner);
	}

	@Override
	public Orderable buildJavaOrderable(JavaAttributeMapping parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaElementCollectionMapping2_0 buildJavaElementCollectionMapping(
			JavaSpecifiedPersistentAttribute parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IService getHibernateService() {
		return ServiceLookup.findService("4.0"); //$NON-NLS-1$
	}
}