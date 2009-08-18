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
import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaEmbeddable;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.context.java.JavaIdMapping;
import org.eclipse.jpt.core.context.java.JavaJoinColumn;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.context.java.JavaManyToManyMapping;
import org.eclipse.jpt.core.context.java.JavaManyToOneMapping;
import org.eclipse.jpt.core.context.java.JavaOneToManyMapping;
import org.eclipse.jpt.core.context.java.JavaOneToOneMapping;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaColumn.Owner;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.platform.GenericJpaFactory;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEmbeddable;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedNativeQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormulaImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaParameter;


/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
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
		return new HibernateJavaIdMapping(parent);
	}
	
	public JavaGenericGenerator buildJavaGenericGenerator(JavaJpaContextNode parent) {
		return new JavaGenericGeneratorImpl(parent);
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
		return new HibernateJavaColumn(parent, owner);
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
		return new HibernateJavaJoinColumn(parent, owner);
	}

}
