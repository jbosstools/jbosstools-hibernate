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

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.JoiningStrategy;
import org.eclipse.jpt.core.context.PersistentType;
import org.eclipse.jpt.core.context.java.JavaAssociationOverride;
import org.eclipse.jpt.core.context.java.JavaAssociationOverrideContainer;
import org.eclipse.jpt.core.context.java.JavaAssociationOverrideRelationshipReference;
import org.eclipse.jpt.core.context.java.JavaBaseColumn.Owner;
import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaEmbeddable;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.context.java.JavaManyToOneMapping;
import org.eclipse.jpt.core.context.java.JavaNamedColumn;
import org.eclipse.jpt.core.context.java.JavaNamedQuery;
import org.eclipse.jpt.core.context.java.JavaOneToManyMapping;
import org.eclipse.jpt.core.context.java.JavaOneToOneMapping;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaSequenceGenerator;
import org.eclipse.jpt.core.internal.jpa1.context.java.GenericJavaAssociationOverrideContainer;
import org.eclipse.jpt.core.internal.jpa1.context.java.GenericJavaColumn;
import org.eclipse.jpt.core.internal.jpa2.GenericMetamodelSynchronizer;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaAssociationOverrideRelationshipReference2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaCacheable2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaCollectionTable2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaDerivedIdentity2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaElementCollectionMapping2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaEmbeddable2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaNamedQuery2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaOrderColumn2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaOrphanRemoval2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaPersistentAttribute2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaPersistentType2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.GenericJavaSequenceGenerator2_0;
import org.eclipse.jpt.core.internal.jpa2.context.java.VirtualAssociationOverride2_0Annotation;
import org.eclipse.jpt.core.jpa2.JpaProject2_0;
import org.eclipse.jpt.core.jpa2.context.MetamodelSourceType;
import org.eclipse.jpt.core.jpa2.context.java.JavaCacheable2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaCacheableHolder2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaCollectionTable2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaDerivedIdentity2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaElementCollectionMapping2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaEmbeddedMapping2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaOrderColumn2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaOrderable2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaOrphanRemovable2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaOrphanRemovalHolder2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaSingleRelationshipMapping2_0;
import org.eclipse.jpt.core.resource.java.AssociationOverrideAnnotation;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2.HibernateJavaManyToOneMapping2_0;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2.HibernateJavaOneToManyMapping2_0;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2.HibernateJavaOneToOneMapping2_0;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaFactory2_0 extends HibernateAbstractJpaFactory {
	
	// ********** Core Model **********
	public JpaProject buildJpaProject(JpaProject.Config config){
		if ( ! (config instanceof JpaProject2_0.Config)) {
			throw new IllegalArgumentException("config must be 2.0-compatible: " + config); //$NON-NLS-1$
		}
		return super.buildJpaProject(config);
	}
	
	// ********** Hibernate Specific **********
	@Override
	public JavaManyToOneMapping buildJavaManyToOneMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaManyToOneMapping2_0(parent);
	}
	
	@Override
	public JavaOneToOneMapping buildJavaOneToOneMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaOneToOneMapping2_0(parent);
	}
	
	@Override
	public JavaOneToManyMapping buildJavaOneToManyMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaOneToManyMapping2_0(parent);
	}
	
	// ********** From GenericJpa2_0 Model **********
	@Override
	public MetamodelSourceType.Synchronizer buildMetamodelSynchronizer(MetamodelSourceType sourceType) {
		return new GenericMetamodelSynchronizer(sourceType);
	}
	
	@Override
	public JavaPersistentType buildJavaPersistentType(PersistentType.Owner owner, JavaResourcePersistentType jrpt) {
		return new GenericJavaPersistentType2_0(owner, jrpt);
	}
	
	@Override
	public JavaPersistentAttribute buildJavaPersistentAttribute(PersistentType parent, JavaResourcePersistentAttribute jrpa) {
		return new GenericJavaPersistentAttribute2_0(parent, jrpa);
	}
	
	@Override
	public JavaEmbeddable buildJavaEmbeddable(JavaPersistentType parent) {
		return new GenericJavaEmbeddable2_0(parent);
	}
	
	@Override
	public JavaSequenceGenerator buildJavaSequenceGenerator(JavaJpaContextNode parent) {
		return new GenericJavaSequenceGenerator2_0(parent);
	}
	
	//The 2.0 JPA spec supports association overrides on an embedded mapping while the 1.0 spec did not
	@Override
	public JavaAssociationOverrideContainer buildJavaAssociationOverrideContainer(JavaEmbeddedMapping2_0 parent, JavaAssociationOverrideContainer.Owner owner) {
		return new GenericJavaAssociationOverrideContainer(parent, owner);
	}
	
	@Override
	public JavaAssociationOverrideRelationshipReference buildJavaAssociationOverrideRelationshipReference(JavaAssociationOverride parent) {
		return new GenericJavaAssociationOverrideRelationshipReference2_0(parent);
	}
	
	@Override
	public AssociationOverrideAnnotation buildJavaVirtualAssociationOverrideAnnotation(JavaResourcePersistentMember jrpm, String name, JoiningStrategy joiningStrategy) {
		return new VirtualAssociationOverride2_0Annotation(jrpm, name, joiningStrategy);
	}
	
	@Override
	public JavaDerivedIdentity2_0 buildJavaDerivedIdentity(JavaSingleRelationshipMapping2_0 parent) {
		return new GenericJavaDerivedIdentity2_0(parent);
	}
	
	@Override
	public JavaElementCollectionMapping2_0 buildJavaElementCollectionMapping2_0(JavaPersistentAttribute parent) {
		return new GenericJavaElementCollectionMapping2_0(parent);
	}
	
	@Override
	public JavaCacheable2_0 buildJavaCacheable(JavaCacheableHolder2_0 parent) {
		return new GenericJavaCacheable2_0(parent);
	}
	
	@Override
	public JavaOrphanRemovable2_0 buildJavaOrphanRemoval(JavaOrphanRemovalHolder2_0 parent) {
		return new GenericJavaOrphanRemoval2_0(parent);
	}

	@Override
	public JavaNamedQuery buildJavaNamedQuery(JavaJpaContextNode parent) {
		return new GenericJavaNamedQuery2_0(parent);
	}
	
	@Override
	public JavaCollectionTable2_0 buildJavaCollectionTable(JavaElementCollectionMapping2_0 parent) {
		return new GenericJavaCollectionTable2_0(parent);
	}

	@Override
	public JavaOrderColumn2_0 buildJavaOrderColumn(JavaOrderable2_0 parent, JavaNamedColumn.Owner owner) {
		return new GenericJavaOrderColumn2_0(parent, owner);
	}
	
	@Override
	public JavaColumn buildJavaMapKeyColumn(JavaJpaContextNode parent, Owner owner) {
		return new GenericJavaColumn(parent, owner);
	}
		
}
