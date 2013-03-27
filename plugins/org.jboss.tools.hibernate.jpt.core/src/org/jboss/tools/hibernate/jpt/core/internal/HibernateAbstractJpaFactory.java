/*******************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal;


import org.eclipse.jpt.common.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackage;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.JoinColumn;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.context.NamedDiscriminatorColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaIdMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaManyToManyMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaManyToOneMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaOneToManyMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaOneToOneMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedDiscriminatorColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedJoinColumn;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedJoinTable;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedJoinTableRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedSecondaryTable;
import org.eclipse.jpt.jpa.core.context.java.JavaSpecifiedTable;
import org.eclipse.jpt.jpa.core.context.java.JavaTable;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaFactory;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.resource.java.CompleteJoinColumnAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.EntityAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.SecondaryTableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.ForeignKeyAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.ForeignKeyImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateGenericGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaBasicMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaDiscriminatorColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaGeneratorContainerImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMappingImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinColumnImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaOneToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainerImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaSecondaryTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainerImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedNativeQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfoImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.IndexImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDbGenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDbGenericGeneratorImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormulaImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaIndex;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaType;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeConverterImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDef;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDefImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.TypeImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernateClassRef;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public abstract class HibernateAbstractJpaFactory extends AbstractJpaFactory {

	// ********** Core Model **********
	@Override
	public JpaProject buildJpaProject(JpaProject.Config config){
		return new HibernateJpaProject(config);
	}

	// ********** Java Context Model **********
	@Override
	public JavaEntity buildJavaEntity(JavaPersistentType parent,
			EntityAnnotation entityAnnotation) {
		return new HibernateJavaEntityImpl(parent, entityAnnotation);
	}

	@Override
	public JavaIdMapping buildJavaIdMapping(JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaIdMappingImpl(parent);
	}

	public JavaDbGenericGenerator buildJavaGenericGenerator(HibernateGenericGeneratorContainer parent, GenericGeneratorAnnotation annotation) {
		return new JavaDbGenericGeneratorImpl(parent, annotation);
	}

	public JavaTypeDef buildJavaTypeDef(JpaContextModel parent, TypeDefAnnotation typeDefResource) {
		return new JavaTypeDefImpl(parent, typeDefResource);
	}

	@Override
	public JavaSpecifiedDiscriminatorColumn buildJavaDiscriminatorColumn(
			JavaSpecifiedDiscriminatorColumn.ParentAdapter parent) {
		return new HibernateJavaDiscriminatorColumnImpl(parent);
	}

	public HibernateJavaNamedQuery buildHibernateJavaNamedQuery(JavaQueryContainer parent, HibernateNamedQueryAnnotation hibernateNamedQueryAnnotation) {
		return new HibernateNamedQueryImpl(parent, hibernateNamedQueryAnnotation);
	}

	public HibernateJavaNamedNativeQuery buildHibernateJavaNamedNativeQuery(JavaQueryContainer parent, HibernateNamedNativeQueryAnnotation namedNativeQueryAnnotation) {
		return new HibernateNamedNativeQueryImpl(parent, namedNativeQueryAnnotation);
	}

	public JavaParameter buildJavaParameter(JpaContextModel parent, ParameterAnnotation resourceParameter) {
		return new HibernateJavaParameter(parent, resourceParameter);
	}

	public JavaDiscriminatorFormula buildJavaDiscriminatorFormula(
			HibernateJavaEntity hibernateJavaEntity, DiscriminatorFormulaAnnotation annotation) {
		return new JavaDiscriminatorFormulaImpl(hibernateJavaEntity, annotation);
	}

	@Override
	public JavaSpecifiedColumn buildJavaColumn(JavaSpecifiedColumn.ParentAdapter parent) {
		return new HibernateJavaColumnImpl(parent);
	}
	
	@Override
	public JavaManyToOneMapping buildJavaManyToOneMapping(
			JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaManyToOneMapping(parent);
	}

	@Override
	public JavaOneToOneMapping buildJavaOneToOneMapping(
			JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaOneToOneMapping(parent);
	}

	@Override
	public JavaOneToManyMapping buildJavaOneToManyMapping(
			JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaOneToManyMapping(parent);
	}
	
	@Override
	public JavaManyToManyMapping buildJavaManyToManyMapping(
			JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaManyToManyMapping(parent);
	}

	@Override
	public JavaSpecifiedJoinColumn buildJavaJoinColumn(
			JoinColumn.ParentAdapter owner,
			CompleteJoinColumnAnnotation joinColumnAnnotation) {
		return new HibernateJavaJoinColumnImpl(owner, joinColumnAnnotation);
	}

	@Override
	public JavaSpecifiedSecondaryTable buildJavaSecondaryTable(JavaSpecifiedSecondaryTable.ParentAdapter parent,
			SecondaryTableAnnotation tableAnnotation) {
		return new HibernateJavaSecondaryTableImpl(parent, tableAnnotation);
	}

	@Override
	public JavaSpecifiedJoinTable buildJavaJoinTable(
			JavaSpecifiedJoinTable.ParentAdapter parent) {
		return new HibernateJavaJoinTableImpl(parent);
	}
	
	@Override
	public JavaSpecifiedTable buildJavaTable(JavaTable.ParentAdapter parentAdapter) {
		return new HibernateJavaTableImpl(parentAdapter);
	}

	@Override
	public JavaBasicMapping buildJavaBasicMapping(JavaSpecifiedPersistentAttribute parent) {
		return new HibernateJavaBasicMappingImpl(parent);
	}

	@Override
	public JavaQueryContainer buildJavaQueryContainer(JavaQueryContainer.Parent parent) {
		return new HibernateJavaQueryContainerImpl(parent);
	}
	
	@Override
	public JavaGeneratorContainer buildJavaGeneratorContainer(
			JavaGeneratorContainer.Parent parentAdapter) {
		return new HibernateJavaGeneratorContainerImpl(parentAdapter);
	}

	public HibernateJavaTypeDefContainer buildJavaTypeDefContainer(JpaContextModel parent, JavaResourceAnnotatedElement annotatedElement) {
		return new HibernateJavaTypeDefContainerImpl(parent, annotatedElement);
	}

	public JavaIndex buildIndex(JpaContextModel parent, IndexAnnotation annotation) {
		return new IndexImpl(parent, annotation);
	}

	public ForeignKey buildForeignKey(JpaContextModel parent, ForeignKeyAnnotation annotation) {
		return new ForeignKeyImpl(parent, annotation);
	}

	public JavaType buildType(JpaContextModel parent, TypeAnnotation annotation) {
		return new TypeImpl(parent, annotation);
	}

	public JavaConverter buildJavaTypeConverter(JavaAttributeMapping parent,
			TypeAnnotation converterAnnotation, JavaConverter.ParentAdapter owner) {
		return new JavaTypeConverterImpl(parent, converterAnnotation, owner);
	}

	public HibernatePackageInfo buildJavaPackageInfo(
			HibernateClassRef hibernateClassRef, JavaResourcePackage jrpt) {
		return new HibernatePackageInfoImpl(hibernateClassRef, jrpt);
	}

}
