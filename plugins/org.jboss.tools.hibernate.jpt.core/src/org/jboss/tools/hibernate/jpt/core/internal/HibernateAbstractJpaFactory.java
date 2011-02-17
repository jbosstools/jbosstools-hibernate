package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.java.JavaBaseColumn;
import org.eclipse.jpt.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaDiscriminatorColumn;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.core.context.java.JavaIdMapping;
import org.eclipse.jpt.core.context.java.JavaJoinColumn;
import org.eclipse.jpt.core.context.java.JavaJoinTable;
import org.eclipse.jpt.core.context.java.JavaJoinTableJoiningStrategy;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.context.java.JavaManyToManyMapping;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.core.context.java.JavaSecondaryTable;
import org.eclipse.jpt.core.internal.AbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.ForeignKeyImpl;
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
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainerImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaSecondaryTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTableImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedNativeQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.IndexImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDiscriminatorFormulaImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaIndex;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaParameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaType;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.TypeImpl;

public abstract class HibernateAbstractJpaFactory extends AbstractJpaFactory {
	
	// ********** Core Model **********
	public JpaProject buildJpaProject(JpaProject.Config config){
		return new HibernateJpaProject(config);
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
	
	public HibernateNamedQuery buildHibernateJavaNamedQuery(JavaJpaContextNode parent) {
		return new HibernateNamedQueryImpl(parent);
	}
	
	public HibernateNamedNativeQuery buildHibernateJavaNamedNativeQuery(JavaJpaContextNode parent) {
		return new HibernateNamedNativeQueryImpl(parent);
	}

	public JavaParameter buildJavaParameter(JavaGenericGeneratorImpl javaGenericGeneratorImpl) {
		return new HibernateJavaParameter(javaGenericGeneratorImpl);
	}

	public JavaDiscriminatorFormula buildJavaDiscriminatorFormula(
			HibernateJavaEntity hibernateJavaEntity) {
		return new JavaDiscriminatorFormulaImpl(hibernateJavaEntity);
	}
	
	@Override
	public JavaColumn buildJavaColumn(JavaJpaContextNode parent, JavaBaseColumn.Owner owner) {
		return new HibernateJavaColumnImpl(parent, owner);
	}

	@Override
	public JavaManyToManyMapping buildJavaManyToManyMapping(
			JavaPersistentAttribute parent) {
		//same for jpa2_0 and 1_0
		return new HibernateJavaManyToManyMapping(parent);
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
	
	@Override
	public JavaBasicMapping buildJavaBasicMapping(JavaPersistentAttribute parent) {
		return new HibernateJavaBasicMappingImpl(parent);
	}
	
	@Override
	public JavaQueryContainer buildJavaQueryContainer(JavaJpaContextNode parent) {
		return new HibernateJavaQueryContainerImpl(parent);
	}
	
	@Override
	public JavaGeneratorContainer buildJavaGeneratorContainer(JavaJpaContextNode parent) {
		return new HibernateJavaGeneratorContainerImpl(parent);
	}
	
	public JavaIndex buildIndex(JavaJpaContextNode parent) {
		return new IndexImpl(parent);
	}

	public ForeignKey buildForeignKey(JavaJpaContextNode parent) {
		return new ForeignKeyImpl(parent);
	}

	public JavaType buildType(JavaJpaContextNode parent) {
		return new TypeImpl(parent);
	}

}
