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
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.common.utility.internal.iterators.TransformationIterator;
import org.eclipse.jpt.jpa.core.context.BaseJoinColumn;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.Table;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaEntity;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.NullJavaCacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.CacheableHolder2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaCacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.PersistenceUnit2_0;
import org.eclipse.jpt.jpa.core.resource.java.EntityAnnotation;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateJavaEntityImpl extends AbstractJavaEntity
implements HibernateJavaEntity {

	protected final HibernateJavaTypeDefContainer typeDefContainer;

	protected JavaDiscriminatorFormula discriminatorFormula;

	protected final JavaCacheable2_0 cacheable;

	protected ForeignKey foreignKey;

	public HibernateJavaEntityImpl(JavaPersistentType parent, EntityAnnotation mappingAnnotation) {
		super(parent, mappingAnnotation);
		this.discriminatorFormula = this.buildDiscriminatorFormula();
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(parent);
		this.cacheable = buildJavaCachable();
	}

	protected JavaCacheable2_0 buildJavaCachable() {
		return this.isJpa2_0Compatible() ?
				this.getJpaFactory2_0().buildJavaCacheable(this) :
				new NullJavaCacheable2_0(this);
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.cacheable.synchronizeWithResourceModel();
		this.typeDefContainer.synchronizeWithResourceModel();
		this.discriminatorFormula.synchronizeWithResourceModel();
		this.initializeForeignKey();
	}

	@Override
	public void update() {
		super.update();
		this.cacheable.update();
		this.typeDefContainer.update();
		this.discriminatorFormula.update();
		this.updateForeignKey();
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) this.getJpaPlatform().getJpaFactory();
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public HibernateJavaTypeDefContainer getTypeDefContainer() {
		return this.typeDefContainer;
	}

/*	protected static final String[] SUPPORTING_ANNOTATION_NAMES_ARRAY2 = new String[] {
		Hibernate.GENERIC_GENERATOR,
		Hibernate.GENERIC_GENERATORS,
		Hibernate.TYPE_DEF,
		Hibernate.TYPE_DEFS,
		Hibernate.NAMED_QUERY,
		Hibernate.NAMED_QUERIES,
		Hibernate.NAMED_NATIVE_QUERY,
		Hibernate.NAMED_NATIVE_QUERIES,
		Hibernate.DISCRIMINATOR_FORMULA,
		Hibernate.FOREIGN_KEY,
	};

	protected static final Iterable<String> SUPPORTING_ANNOTATION_NAMES2 = new ArrayIterable<String>(SUPPORTING_ANNOTATION_NAMES_ARRAY2);


	@SuppressWarnings("unchecked")
	@Override
	public Iterable<String> getSupportingAnnotationNames() {
		return new CompositeIterable<String>(
				SUPPORTING_ANNOTATION_NAMES2,
				super.getSupportingAnnotationNames());
	}*/

	@Override
	public HibernateJavaTable getTable() {
		return (HibernateJavaTable) super.getTable();
	}

	// ********************* DiscriminatorFormula **************
	@Override
	public JavaDiscriminatorFormula getDiscriminatorFormula() {
		return this.discriminatorFormula;
	}


	protected JavaDiscriminatorFormula buildDiscriminatorFormula() {
		return this.getJpaFactory().buildJavaDiscriminatorFormula(this, this.buildDiscriminatorFormulaOwner());
	}

	protected JavaDiscriminatorFormula.Owner buildDiscriminatorFormulaOwner() {
		return new DiscriminatorFormulaOwner();
	}

	protected void setDiscriminatorFormula(JavaDiscriminatorFormula newDiscriminatorFormula) {
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = newDiscriminatorFormula;
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula, newDiscriminatorFormula);
	}

	@Override
	public JavaDiscriminatorFormula addDiscriminatorFormula() {
		if (getDiscriminatorFormula() != null) {
			throw new IllegalStateException("discriminatorFormula already exists"); //$NON-NLS-1$
		}
		this.discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this, buildDiscriminatorFormulaOwner());
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = (DiscriminatorFormulaAnnotation) this.getResourcePersistentType().addAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		this.discriminatorFormula.initialize(discriminatorFormulaResource);
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, null, this.discriminatorFormula);
		return this.discriminatorFormula;
	}

	@Override
	public void removeDiscriminatorFormula() {
		if (getDiscriminatorFormula() == null) {
			throw new IllegalStateException("discriminatorFormula does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = null;
		this.getResourcePersistentType().removeAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
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
		return (DiscriminatorFormulaAnnotation) this.getResourcePersistentType().getAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
	}

	protected JavaDiscriminatorFormula buildDiscriminatorFormula(DiscriminatorFormulaAnnotation discriminatorFormulaResource) {
		JavaDiscriminatorFormula discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this, buildDiscriminatorFormulaOwner());
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

	@Override
	public ForeignKey addForeignKey() {
		if (getForeignKey() != null) {
			throw new IllegalStateException("foreignKey already exists"); //$NON-NLS-1$
		}
		this.foreignKey = getJpaFactory().buildForeignKey(this);
		ForeignKeyAnnotation foreignKeyResource = (ForeignKeyAnnotation) this.getResourcePersistentType().addAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		this.foreignKey.initialize(foreignKeyResource);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, null, this.foreignKey);
		return this.foreignKey;
	}

	@Override
	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}

	protected void setForeignKey(ForeignKey newForeignKey) {
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = newForeignKey;
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, newForeignKey);
	}

	@Override
	public void removeForeignKey() {
		if (getForeignKey() == null) {
			throw new IllegalStateException("foreignKey does not exist, cannot be removed"); //$NON-NLS-1$
		}
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = null;
		this.getResourcePersistentType().removeAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, null);
	}

	protected ForeignKey buildForeignKey(ForeignKeyAnnotation foreignKeyResource) {
		ForeignKey foreignKey = getJpaFactory().buildForeignKey(this);
		foreignKey.initialize(foreignKeyResource);
		return foreignKey;
	}

	protected ForeignKeyAnnotation getResourceForeignKey() {
		return (ForeignKeyAnnotation) this.getResourcePersistentType().getAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
	}

	@Override
	public org.eclipse.jpt.jpa.db.Table getForeignKeyDbTable() {
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
		getTypeDefContainer().validate(messages, reporter, astRoot);
		this.validateForeignKey(messages, astRoot);
	}

	protected void validateForeignKey(List<IMessage> messages, CompilationUnit astRoot) {
		org.eclipse.jpt.jpa.db.Table table = getForeignKeyDbTable();
		if (!validatesAgainstDatabase() || this.foreignKey == null || table == null ){
			return;
		}
		Iterator<org.eclipse.jpt.jpa.db.ForeignKey> fks = table.getForeignKeys().iterator();
		while (fks.hasNext()) {
			org.eclipse.jpt.jpa.db.ForeignKey fk = fks.next();
			if (this.foreignKey.getName().equals(fk.getIdentifier())){
				return;
			}
		}
		TextRange textRange = this.getResourceForeignKey().getNameTextRange(astRoot);
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY,
				Messages.UNRESOLVED_FOREIGN_KEY_NAME, new String[] {this.foreignKey.getName(), getPrimaryTableName()},
				this.foreignKey);
		message.setLineNo(textRange.getLineNumber());
		message.setOffset(textRange.getOffset());
		message.setLength(textRange.getLength());
		messages.add(message);
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
	protected PrimaryKeyJoinColumnOwner buildPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}

	// ********** pk join column owner **********

	//not sure need this any more
	class HibernatePrimaryKeyJoinColumnOwner extends PrimaryKeyJoinColumnOwner
	{
		@Override
		public TextRange getValidationTextRange(CompilationUnit astRoot) {
			return HibernateJavaEntityImpl.this.getValidationTextRange(astRoot);
		}

		@Override
		public String getDefaultTableName() {
			return HibernateJavaEntityImpl.this.getPrimaryTableName();
		}

		@Override
		public TypeMapping getTypeMapping() {
			return HibernateJavaEntityImpl.this;
		}

		public org.eclipse.jpt.jpa.db.Table getDbTable(String tableName) {
			return HibernateJavaEntityImpl.this.resolveDbTable(tableName);
		}

		@Override
		public org.eclipse.jpt.jpa.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateJavaEntityImpl.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		@Override
		public int joinColumnsSize() {
			return HibernateJavaEntityImpl.this.primaryKeyJoinColumnsSize();
		}

		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateJavaEntityImpl.this.defaultPrimaryKeyJoinColumn == joinColumn;
		}

		@Override
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

	// ********** cacheable **********

	@Override
	public JavaCacheable2_0 getCacheable() {
		return this.cacheable;
	}

	protected JavaCacheable2_0 buildCacheable() {
		return this.isJpa2_0Compatible() ?
				this.getJpaFactory2_0().buildJavaCacheable(this) :
				new NullJavaCacheable2_0(this);
	}

	@Override
	public boolean calculateDefaultCacheable() {
		Cacheable2_0 parentCacheable = this.getParentCacheable();
		return (parentCacheable != null) ?
				parentCacheable.isCacheable() :
				((PersistenceUnit2_0) this.getPersistenceUnit()).calculateDefaultCacheable();
	}

	protected Cacheable2_0 getParentCacheable() {
		CacheableHolder2_0 parentEntity = (CacheableHolder2_0) this.getParentEntity();
		return (parentEntity == null) ? null : parentEntity.getCacheable();
	}

	@Override
	public Iterator<String> javaCompletionProposals(int pos,
			Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		result = this.getTypeDefContainer().javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		return null;
	}

	// ********** JavaDiscriminatorColumn.Owner implementation **********

	protected class DiscriminatorFormulaOwner
		implements JavaDiscriminatorFormula.Owner
	{

		@Override
		public TypeMapping getTypeMapping() {
			return HibernateJavaEntityImpl.this;
		}

		@Override
		public String getDefaultTableName() {
			return HibernateJavaEntityImpl.this.getPrimaryTableName();
		}

		@Override
		public org.eclipse.jpt.jpa.db.Table resolveDbTable(String tableName) {
			return HibernateJavaEntityImpl.this.resolveDbTable(tableName);
		}


	}

}


