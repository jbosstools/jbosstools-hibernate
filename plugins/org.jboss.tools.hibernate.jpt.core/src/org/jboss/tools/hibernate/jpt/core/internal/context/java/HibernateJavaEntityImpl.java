/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.BaseJoinColumn;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.NamedColumn;
import org.eclipse.jpt.core.context.Table;
import org.eclipse.jpt.core.context.TypeMapping;
import org.eclipse.jpt.core.context.java.JavaBaseJoinColumn;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaEntity;
import org.eclipse.jpt.core.internal.jpa2.context.java.NullJavaCacheable2_0;
import org.eclipse.jpt.core.jpa2.context.java.JavaCacheable2_0;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.iterables.ArrayIterable;
import org.eclipse.jpt.utility.internal.iterables.CompositeIterable;
import org.eclipse.jpt.utility.internal.iterators.TransformationIterator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaEntityImpl extends AbstractJavaEntity 
implements HibernateJavaEntity {
	
	protected JavaDiscriminatorFormula discriminatorFormula;
	
	protected JavaCacheable2_0 cachable;
	
	protected ForeignKey foreignKey;
	
	public HibernateJavaEntityImpl(JavaPersistentType parent) {
		super(parent);
		this.cachable = buildJavaCachable();
	}
	
	protected JavaCacheable2_0 buildJavaCachable() {
		return new NullJavaCacheable2_0(this);
	}

	@Override
	public void initialize(JavaResourcePersistentType resourcePersistentType) {
		super.initialize(resourcePersistentType);
		this.initializeDiscriminatorFormula();
		this.initializeForeignKey();
	}
	
	@Override
	public void update(JavaResourcePersistentType resourcePersistentType) {
		super.update(resourcePersistentType);
		this.updateDiscriminatorFormula();
		this.updateForeignKey();
	}
	
	protected HibernateJpaFactory getJpaFactory() {
		return (HibernateJpaFactory) this.getJpaPlatform().getJpaFactory();
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	
	protected static final String[] SUPPORTING_ANNOTATION_NAMES_ARRAY2 = new String[] {
		Hibernate.GENERIC_GENERATOR,
		Hibernate.GENERIC_GENERATORS, 
		Hibernate.NAMED_QUERY,
		Hibernate.NAMED_QUERIES,
		Hibernate.NAMED_NATIVE_QUERY,
		Hibernate.NAMED_NATIVE_QUERIES,
		Hibernate.DISCRIMINATOR_FORMULA,
		Hibernate.FOREIGN_KEY
	};
	
	protected static final Iterable<String> SUPPORTING_ANNOTATION_NAMES2 = new ArrayIterable<String>(SUPPORTING_ANNOTATION_NAMES_ARRAY2);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<String> getSupportingAnnotationNames() {
		return new CompositeIterable<String>(
				SUPPORTING_ANNOTATION_NAMES2,
				super.getSupportingAnnotationNames());
	}
	
	public HibernateJavaTable getTable() {
		return (HibernateJavaTable) super.getTable();
	}
	
	// ********************* DiscriminatorFormula **************	
	public JavaDiscriminatorFormula getDiscriminatorFormula() {
		return this.discriminatorFormula;
	}
	
	protected void setDiscriminatorFormula(JavaDiscriminatorFormula newDiscriminatorFormula) {
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = newDiscriminatorFormula;
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula, newDiscriminatorFormula);
	}
	
	public JavaDiscriminatorFormula addDiscriminatorFormula() {
		if (getDiscriminatorFormula() != null) {
			throw new IllegalStateException("discriminatorFormula already exists"); //$NON-NLS-1$
		}
		this.discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this);
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = (DiscriminatorFormulaAnnotation) this.javaResourcePersistentType.addAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		this.discriminatorFormula.initialize(discriminatorFormulaResource);
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, null, this.discriminatorFormula);
		return this.discriminatorFormula;
	}
	
	public void removeDiscriminatorFormula() {
		if (getDiscriminatorFormula() == null) {
			throw new IllegalStateException("discriminatorFormula does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = null;
		this.javaResourcePersistentType.removeAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula,null);
	}
	
	protected void initializeDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = getDiscriminatorFormulaResource();
		if (discriminatorFormulaResource != null) {
			this.discriminatorFormula = buildDiscriminatorFormula(discriminatorFormulaResource);
		}
	}

	protected void updateDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = getDiscriminatorFormulaResource();
		if (discriminatorFormulaResource == null) {
			if (getDiscriminatorFormula() != null) {
				setDiscriminatorFormula(null);
			}
		}
		else {
			if (getDiscriminatorFormula() == null) {
				setDiscriminatorFormula(buildDiscriminatorFormula(discriminatorFormulaResource));
			}
			else {
				getDiscriminatorFormula().update(discriminatorFormulaResource);
			}
		}
	}

	public DiscriminatorFormulaAnnotation getDiscriminatorFormulaResource() {
		return (DiscriminatorFormulaAnnotation) this.javaResourcePersistentType.getAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
	}
	
	protected JavaDiscriminatorFormula buildDiscriminatorFormula(DiscriminatorFormulaAnnotation discriminatorFormulaResource) {
		JavaDiscriminatorFormula discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this);
		discriminatorFormula.initialize(discriminatorFormulaResource);
		return discriminatorFormula;
	}

	// ********************* foreignKey **************
	
	protected void initializeForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource != null) {
			this.foreignKey = buildForeignKey(foreignKeyResource);
		}
	}
	
	protected void updateForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource == null) {
			if (getForeignKey() != null) {
				setForeignKey(null);
			}
		}
		else {
			if (getForeignKey() == null) {
				setForeignKey(buildForeignKey(foreignKeyResource));
			}
			else {
				getForeignKey().update(foreignKeyResource);
			}
		}
	}
	
	public ForeignKey addForeignKey() {
		if (getForeignKey() != null) {
			throw new IllegalStateException("foreignKey already exists"); //$NON-NLS-1$
		}
		this.foreignKey = getJpaFactory().buildForeignKey(this);
		ForeignKeyAnnotation foreignKeyResource = (ForeignKeyAnnotation) javaResourcePersistentType.addAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		this.foreignKey.initialize(foreignKeyResource);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, null, this.foreignKey);
		return this.foreignKey;
	}

	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}
	
	protected void setForeignKey(ForeignKey newForeignKey) {
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = newForeignKey;
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, newForeignKey);
	}

	public void removeForeignKey() {
		if (getForeignKey() == null) {
			throw new IllegalStateException("foreignKey does not exist, cannot be removed"); //$NON-NLS-1$
		}
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = null;
		this.javaResourcePersistentType.removeAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, null);
	}
	
	protected ForeignKey buildForeignKey(ForeignKeyAnnotation foreignKeyResource) {
		ForeignKey foreignKey = getJpaFactory().buildForeignKey(this);
		foreignKey.initialize(foreignKeyResource);
		return foreignKey;
	}
	
	protected ForeignKeyAnnotation getResourceForeignKey() {
		return (ForeignKeyAnnotation) this.javaResourcePersistentType.getAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
	}
	
	public org.eclipse.jpt.db.Table getForeignKeyDbTable() {
		return getPrimaryDbTable();
	}
	
	@Override
	public HibernateJavaGeneratorContainer getGeneratorContainer() {
		return (HibernateJavaGeneratorContainer)super.getGeneratorContainer();
	}

	// ************************* validation ***********************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateGenericGenerator(messages, reporter, astRoot);
		this.validateForeignKey(messages, astRoot);
	}
	
	protected void validateGenericGenerator(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		while (getGeneratorContainer().genericGenerators().hasNext()) {
			getGeneratorContainer().genericGenerators().next().validate(messages, reporter, astRoot);
		}	
	}
	
	protected void validateForeignKey(List<IMessage> messages, CompilationUnit astRoot) {
		org.eclipse.jpt.db.Table table = getForeignKeyDbTable();
		if (!shouldValidateAgainstDatabase() || foreignKey == null || table == null ){
			return;
		}		
		Iterator<org.eclipse.jpt.db.ForeignKey> fks = table.getForeignKeys().iterator();
		while (fks.hasNext()) {
			org.eclipse.jpt.db.ForeignKey fk = (org.eclipse.jpt.db.ForeignKey) fks.next();
			if (foreignKey.getName().equals(fk.getIdentifier())){
				return;
			}
		}
		TextRange textRange = this.getResourceForeignKey().getNameTextRange(astRoot);
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY, 
				Messages.UNRESOLVED_FOREIGN_KEY_NAME, new String[] {foreignKey.getName(), getPrimaryTableName()},
				this.foreignKey);
		message.setLineNo(textRange.getLineNumber());
		message.setOffset(textRange.getOffset());
		message.setLength(textRange.getLength());
		messages.add(message);		
	}
	
	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter,
			CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		while (getGeneratorContainer().genericGenerators().hasNext()) {
			result = getGeneratorContainer().genericGenerators().next()
				.javaCompletionProposals(pos, filter, astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	/*protected String getResourceDefaultName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.classToTableName(javaResourcePersistentType.getName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return javaResourcePersistentType.getName();
	}*/
	
	@Override
	protected JavaBaseJoinColumn.Owner buildPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}
	
	// ********** pk join column owner **********

	class HibernatePrimaryKeyJoinColumnOwner implements JavaBaseJoinColumn.Owner
	{
		public TextRange getValidationTextRange(CompilationUnit astRoot) {
			return HibernateJavaEntityImpl.this.getValidationTextRange(astRoot);
		}
		
		public String getDefaultTableName() {
			return HibernateJavaEntityImpl.this.getPrimaryTableName();
		}

		public TypeMapping getTypeMapping() {
			return HibernateJavaEntityImpl.this;
		}

		public org.eclipse.jpt.db.Table getDbTable(String tableName) {
			return HibernateJavaEntityImpl.this.getDbTable(tableName);
		}

		public org.eclipse.jpt.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateJavaEntityImpl.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		public int joinColumnsSize() {
			return HibernateJavaEntityImpl.this.primaryKeyJoinColumnsSize();
		}
		
		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateJavaEntityImpl.this.defaultPrimaryKeyJoinColumn == joinColumn;
		}		
		
		public String getDefaultColumnName() {
			if (joinColumnsSize() != 1) {
				return null;
			}
			
			Entity parentEntity = HibernateJavaEntityImpl.this.getParentEntity();
			if (parentEntity != null) {
				HibernateJpaProject hibernateJpaProject = HibernateJavaEntityImpl.this.getJpaProject();
				NamingStrategy ns = hibernateJpaProject.getNamingStrategy();
				if (hibernateJpaProject.isNamingStrategyEnabled() && ns != null) {
					try {
						String name = ns.joinKeyColumnName(parentEntity.getPrimaryKeyColumnName(),
							parentEntity.getPrimaryTableName());
						if (parentEntity.getPrimaryDbTable() != null){
							return parentEntity.getPrimaryDbTable().getDatabase().convertNameToIdentifier(name);
						}
						return name ;
					} catch (Exception e) {
						Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
								Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
						HibernateJptPlugin.logException(m.getText(), e);
					}
				}
				return parentEntity.getPrimaryKeyColumnName();
			} else {
				return getPrimaryKeyColumnName();
			}
		}

		public IMessage buildUnresolvedNameMessage(NamedColumn column, TextRange textRange) {
			throw new UnsupportedOperationException("validation not supported yet: bug 148262"); //$NON-NLS-1$
		}

		public IMessage buildUnresolvedReferencedColumnNameMessage(BaseJoinColumn column, TextRange textRange) {
			throw new UnsupportedOperationException("validation not supported yet: bug 148262"); //$NON-NLS-1$
		}

		public IMessage buildUnspecifiedNameMultipleJoinColumnsMessage(BaseJoinColumn column, TextRange textRange) {
			throw new UnsupportedOperationException("validation not supported yet: bug 148262"); //$NON-NLS-1$
		}
		
		public IMessage buildUnspecifiedReferencedColumnNameMultipleJoinColumnsMessage(BaseJoinColumn column, TextRange textRange) {
			throw new UnsupportedOperationException("validation not supported yet: bug 148262"); //$NON-NLS-1$
		}
	}

	@Override
	public String getPrimaryTableName() {
		return this.getTable().getDBTableName();
	}
	
	@Override
	public String getDefaultTableName() {
		return super.getDefaultTableName();
	}
	
	/**
	 * Convert Table to it's DB name.
	 */
	protected Iterator<String> tableNames(Iterator<Table> tables) {
		return new TransformationIterator<Table, String>(tables) {
			@Override
			protected String transform(Table t) {
				if (t instanceof HibernateTable) {
					return ((HibernateTable)t).getDBTableName();					
				} else {
					return t.getName();//What is this???
				}				
			}
		};
	}

	public JavaCacheable2_0 getCacheable() {
		return cachable;
	}

	public boolean calculateDefaultCacheable() {
		return false;
	}
	
}


