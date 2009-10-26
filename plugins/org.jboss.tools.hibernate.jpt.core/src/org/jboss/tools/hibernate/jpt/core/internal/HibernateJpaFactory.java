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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.XmlContextNode;
import org.eclipse.jpt.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaDiscriminatorColumn;
import org.eclipse.jpt.core.context.java.JavaEmbeddable;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.context.java.JavaIdMapping;
import org.eclipse.jpt.core.context.java.JavaJoinColumn;
import org.eclipse.jpt.core.context.java.JavaJoinTable;
import org.eclipse.jpt.core.context.java.JavaJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.context.java.JavaManyToManyMapping;
import org.eclipse.jpt.core.context.java.JavaManyToOneMapping;
import org.eclipse.jpt.core.context.java.JavaOneToManyMapping;
import org.eclipse.jpt.core.context.java.JavaOneToOneMapping;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaSecondaryTable;
import org.eclipse.jpt.core.context.java.JavaColumn.Owner;
import org.eclipse.jpt.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.core.context.orm.OrmColumn;
import org.eclipse.jpt.core.context.orm.OrmEntity;
import org.eclipse.jpt.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.core.context.orm.OrmJoinColumn;
import org.eclipse.jpt.core.context.orm.OrmJoinTable;
import org.eclipse.jpt.core.context.orm.OrmJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.orm.OrmManyToManyMapping;
import org.eclipse.jpt.core.context.orm.OrmManyToOneMapping;
import org.eclipse.jpt.core.context.orm.OrmOneToManyMapping;
import org.eclipse.jpt.core.context.orm.OrmOneToOneMapping;
import org.eclipse.jpt.core.context.orm.OrmPersistentAttribute;
import org.eclipse.jpt.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.core.context.orm.OrmTable;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.platform.GenericJpaFactory;
import org.eclipse.jpt.core.resource.orm.XmlBasic;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.core.resource.orm.XmlId;
import org.eclipse.jpt.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.core.resource.orm.XmlJoinTableMapping;
import org.eclipse.jpt.core.resource.orm.XmlManyToMany;
import org.eclipse.jpt.core.resource.orm.XmlManyToOne;
import org.eclipse.jpt.core.resource.orm.XmlOneToMany;
import org.eclipse.jpt.core.resource.orm.XmlOneToOne;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Index;
import org.jboss.tools.hibernate.jpt.core.internal.context.IndexImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaBasicMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaDiscriminatorColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEmbeddable;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaSecondaryTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedNativeQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormulaImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmBasicMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmOneToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmOneToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmTableImpl;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaFactory extends GenericJpaFactory {
	
	// ********** Core Model **********
	public JpaProject buildJpaProject(JpaProject.Config config) throws CoreException {
		return new HibernateJpaProject(config);
	}

	// ********** Persistence Context Model **********	
	@Override
	public PersistenceUnit buildPersistenceUnit(Persistence parent, XmlPersistenceUnit persistenceUnit) {
		return new HibernatePersistenceUnit(parent, persistenceUnit);
	}
	
	// ********** Java Context Model **********
	@Override
	public JavaEntity buildJavaEntity(JavaPersistentType parent) {
		return new HibernateJavaEntityImpl(parent);
	}
	
	@Override
	public JavaIdMapping buildJavaIdMapping(JavaPersistentAttribute parent) {
		return new HibernateJavaIdMappingImpl(parent);
	}
	
	public JavaGenericGenerator buildJavaGenericGenerator(JavaJpaContextNode parent) {
		return new JavaGenericGeneratorImpl(parent);
	}
	
	@Override
	public JavaDiscriminatorColumn buildJavaDiscriminatorColumn(
			JavaEntity parent,
			org.eclipse.jpt.core.context.java.JavaDiscriminatorColumn.Owner owner) {
		return new HibernateJavaDiscriminatorColumnImpl(parent, owner);
	}
	
	public HibernateNamedQuery buildHibernateNamedQuery(JavaJpaContextNode parent) {
		return new HibernateNamedQueryImpl(parent);
	}
	
	public HibernateNamedNativeQuery buildHibernateNamedNativeQuery(JavaJpaContextNode parent) {
		return new HibernateNamedNativeQueryImpl(parent);
	}

	public JavaParameter buildJavaParameter(JavaGenericGeneratorImpl javaGenericGeneratorImpl) {
		return new HibernateJavaParameter(javaGenericGeneratorImpl);
	}

	public JavaDiscriminatorFormula buildJavaDiscriminatorFormula(
			HibernateJavaEntity hibernateJavaEntity) {
		return new JavaDiscriminatorFormulaImpl(hibernateJavaEntity);
	}
	
	public JavaEmbeddable buildJavaEmbeddable(JavaPersistentType parent) {
		return new HibernateJavaEmbeddable(parent);
	}
	
	@Override
	public JavaColumn buildJavaColumn(JavaJpaContextNode parent, Owner owner) {
		return new HibernateJavaColumnImpl(parent, owner);
	}
	
	@Override
	public JavaOneToManyMapping buildJavaOneToManyMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaOneToManyMapping(parent);
	}
	
	@Override
	public JavaManyToManyMapping buildJavaManyToManyMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaManyToManyMapping(parent);
	}
	
	@Override
	public JavaManyToOneMapping buildJavaManyToOneMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaManyToOneMapping(parent);
	}
	
	@Override
	public JavaOneToOneMapping buildJavaOneToOneMapping(
			JavaPersistentAttribute parent) {
		return new HibernateJavaOneToOneMapping(parent);
	}
	
	@Override
	public JavaJoinColumn buildJavaJoinColumn(JavaJpaContextNode parent,
			org.eclipse.jpt.core.context.java.JavaJoinColumn.Owner owner) {
		return new HibernateJavaJoinColumnImpl(parent, owner);
	}
	
	@Override
	public JavaSecondaryTable buildJavaSecondaryTable(JavaEntity parent) {
		return new HibernateJavaSecondaryTableImpl(parent);
	}
	
	@Override
	public JavaJoinTable buildJavaJoinTable(JavaJoinTableJoiningStrategy parent) {
		return new HibernateJavaJoinTableImpl(parent);
	}
	
	@Override
	public HibernateJavaTable buildJavaTable(JavaEntity parent) {
		return new HibernateJavaTableImpl(parent);
	}
	
	public JavaBasicMapping buildJavaBasicMapping(JavaPersistentAttribute parent) {
		return new HibernateJavaBasicMappingImpl(parent);
	}
	
	// ********** ORM Context Model **********
	
	@Override
	public OrmBasicMapping buildOrmBasicMapping(OrmPersistentAttribute parent,
			XmlBasic resourceMapping) {
		return new HibernateOrmBasicMapping(parent, resourceMapping);
	}
	
	@Override
	public OrmIdMapping buildOrmIdMapping(OrmPersistentAttribute parent,
			XmlId resourceMapping) {
		return new HibernateOrmIdMappingImpl(parent, resourceMapping);
	}
	
		@Override
	public OrmEntity buildOrmEntity(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		return new HibernateOrmEntityImpl(parent, resourceMapping);
	}
		
	@Override
	public OrmTable buildOrmTable(OrmEntity parent) {
		return new HibernateOrmTableImpl(parent);
	}

    @Override
	public OrmJoinTable buildOrmJoinTable(OrmJoinTableJoiningStrategy parent,
			XmlJoinTableMapping resourceMapping) {
		return new HibernateOrmJoinTableImpl(parent, resourceMapping);
	}	
	
	@Override
	public OrmColumn buildOrmColumn(XmlContextNode parent,
			org.eclipse.jpt.core.context.orm.OrmColumn.Owner owner) {
		return new HibernateOrmColumnImpl(parent, owner);
	}
	
	@Override
	public OrmJoinColumn buildOrmJoinColumn(XmlContextNode parent,
			org.eclipse.jpt.core.context.orm.OrmJoinColumn.Owner owner,
			XmlJoinColumn resourceJoinColumn) {
		return new HibernateOrmJoinColumnImpl(parent, owner, resourceJoinColumn);
	}
	
	@SuppressWarnings("unchecked")
	public OrmManyToManyMapping buildOrmManyToManyMapping(OrmPersistentAttribute parent, XmlManyToMany resourceMapping) {
		return new HibernateOrmManyToManyMapping(parent, resourceMapping);
	}
	
	@SuppressWarnings("unchecked")
	public OrmManyToOneMapping buildOrmManyToOneMapping(OrmPersistentAttribute parent, XmlManyToOne resourceMapping) {
		return new HibernateOrmManyToOneMapping(parent, resourceMapping);
	}
	
	@SuppressWarnings("unchecked")
	public OrmOneToManyMapping buildOrmOneToManyMapping(OrmPersistentAttribute parent, XmlOneToMany resourceMapping) {
		return new HibernateOrmOneToManyMapping(parent, resourceMapping);
	}
	
	@SuppressWarnings("unchecked")
	public OrmOneToOneMapping buildOrmOneToOneMapping(OrmPersistentAttribute parent, XmlOneToOne resourceMapping) {
		return new HibernateOrmOneToOneMapping(parent, resourceMapping);
	}

	public Index buildIndex(JavaJpaContextNode parent) {
		return new IndexImpl(parent);
	}

}
