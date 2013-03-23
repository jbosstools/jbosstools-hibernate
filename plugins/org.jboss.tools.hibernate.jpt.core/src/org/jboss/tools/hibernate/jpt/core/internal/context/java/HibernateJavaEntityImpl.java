/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.iterable.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterable.TransformationIterable;
import org.eclipse.jpt.common.utility.internal.predicate.PredicateTools;
import org.eclipse.jpt.common.utility.transformer.Transformer;
import org.eclipse.jpt.jpa.core.context.BaseJoinColumn;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.core.context.Table;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaEntity;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.NullJavaCacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.CacheableReference2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.PersistenceUnit2_0;
import org.eclipse.jpt.jpa.core.resource.java.EntityAnnotation;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateJavaEntityImpl extends AbstractJavaEntity
implements HibernateJavaEntity {

	protected final HibernateJavaTypeDefContainer typeDefContainer;

	protected JavaDiscriminatorFormula discriminatorFormula;

	protected final Cacheable2_0 cacheable;

	public HibernateJavaEntityImpl(JavaPersistentType parent, EntityAnnotation mappingAnnotation) {
		super(parent, mappingAnnotation);
		this.discriminatorFormula = this.buildDiscriminatorFormula();
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(parent, this.getJavaResourceType());
		this.cacheable = this.buildJavaCachable();
	}

	protected Cacheable2_0 buildJavaCachable() {
		return this.isJpa2_0Compatible() ?
				this.getJpaFactory2_0().buildJavaCacheable(this) :
				new NullJavaCacheable2_0(this);
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.cacheable.synchronizeWithResourceModel();
		this.typeDefContainer.synchronizeWithResourceModel();
		this.syncDiscriminatorFormula();
	}

	@Override
	public void update() {
		super.update();
		this.cacheable.update();
		this.typeDefContainer.update();
		if (discriminatorFormula != null){
			this.discriminatorFormula.update();
		}
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


	@Override
	public HibernateJavaTable getTable() {
		return (HibernateJavaTable) super.getTable();
	}

	// ********************* DiscriminatorFormula **************
	@Override
	public JavaDiscriminatorFormula getDiscriminatorFormula() {
		return this.discriminatorFormula;
	}

	@Override
	public JavaDiscriminatorFormula addDiscriminatorFormula() {
		if (getDiscriminatorFormula() != null) {
			throw new IllegalStateException("discriminatorFormula already exists"); //$NON-NLS-1$
		}
		DiscriminatorFormulaAnnotation annotation = this.buildDiscriminatorFormulaAnnotation();
		JavaDiscriminatorFormula discriminatorFormula = buildDiscriminatorFormula(annotation);
		this.setDiscriminatorFormula(discriminatorFormula);
		return discriminatorFormula;
	}
	
	protected DiscriminatorFormulaAnnotation buildDiscriminatorFormulaAnnotation() {
		return (DiscriminatorFormulaAnnotation) this.getJavaResourceType().addAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
	}
	
	@Override
	public void removeDiscriminatorFormula() {
		if (getDiscriminatorFormula() == null) {
			throw new IllegalStateException("discriminatorFormula does not exist, cannot be removed"); //$NON-NLS-1$
		}
		this.getJavaResourceType().removeAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		this.setDiscriminatorFormula(null);
	}

	protected JavaDiscriminatorFormula buildDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation annotation = this.getDiscriminatorFormulaAnnotation();
		return (annotation == null) ? null : this.buildDiscriminatorFormula(annotation);
	}

	public DiscriminatorFormulaAnnotation getDiscriminatorFormulaAnnotation() {
		return (DiscriminatorFormulaAnnotation) this.getJavaResourceType().getAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
	}

	protected JavaDiscriminatorFormula buildDiscriminatorFormula(DiscriminatorFormulaAnnotation annotation) {
		return getJpaFactory().buildJavaDiscriminatorFormula(this, annotation);
	}

	protected void syncDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation annotation = getDiscriminatorFormulaAnnotation();
		if (annotation == null) {
			if (getDiscriminatorFormula() != null) {
				setDiscriminatorFormula(null);
			}
		}
		else {
			if ((getDiscriminatorFormula() != null)
					&& (getDiscriminatorFormula().getDiscriminatorFormulaAnnotation() == annotation)) {
				this.discriminatorFormula.synchronizeWithResourceModel();
			} else {
				this.setDiscriminatorFormula(this.buildDiscriminatorFormula(annotation));
			}
		}
	}

	protected void setDiscriminatorFormula(JavaDiscriminatorFormula newDiscriminatorFormula) {
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = newDiscriminatorFormula;
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula, newDiscriminatorFormula);
	}

	@Override
	public HibernateGenericGeneratorContainer getGeneratorContainer() {
		return (HibernateGenericGeneratorContainer)super.getGeneratorContainer();
	}

	// ************************* validation ***********************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		getTypeDefContainer().validate(messages, reporter);
	}

	@Override
	protected PrimaryKeyJoinColumnParentAdapter buildPrimaryKeyJoinColumnParentAdapter() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}

	// ********** pk join column owner **********

	//not sure need this any more
	class HibernatePrimaryKeyJoinColumnOwner extends PrimaryKeyJoinColumnParentAdapter
	{
		@Override
		public TextRange getValidationTextRange() {
			return HibernateJavaEntityImpl.this.getValidationTextRange();
		}

		@Override
		public String getDefaultTableName() {
			return HibernateJavaEntityImpl.this.getPrimaryTableName();
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
		public int getJoinColumnsSize() {
			return HibernateJavaEntityImpl.this.getPrimaryKeyJoinColumnsSize();
		}

		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateJavaEntityImpl.this.defaultPrimaryKeyJoinColumn == joinColumn;
		}

		@Override
		public String getDefaultColumnName(NamedColumn column) {
			if (getJoinColumnsSize() != 1) {
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
						IMessage m =HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
								Messages.NAMING_STRATEGY_EXCEPTION, column);
						HibernateJptPlugin.logException(m.getText(), e);
					}
				}
				return parentEntity.getPrimaryKeyColumnName();
			} else {
				return getPrimaryKeyColumnName();
			}
		}
	}
	
	protected boolean tableNameIsValid(String tableName) {
		return this.tableIsUndefined || IterableTools.contains(this.getAllAssociatedDBTableNames(), tableName);
	}
	
	public Iterable<String> getAllAssociatedDBTableNames() {
		return this.convertToDBNames(this.getAllAssociatedTables());
	}

	/**
	 * strip out <code>null</code> names
	 */
	protected Iterable<String> convertToDBNames(Iterable<Table> tables) {
		return new FilteringIterable<String>(this.convertToDBNames_(tables), PredicateTools.notNullPredicate());	}

	/**
	 * Convert Table to it's DB name.
	 */
	protected Iterable<String> convertToDBNames_(Iterable<Table> tables) {
		return new TransformationIterable<Table, String>(
				tables,
				new Transformer<Table, String>() {
					@Override
					public String transform(Table t) {
						if (t instanceof HibernateTable) {
							return ((HibernateTable)t).getDBTableName();
						} else {
							return t.getName();//What is this???
						}
					}
				}
			);
	}

	@Override
	public String getPrimaryTableName() {
		return this.getTable().getDBTableName();
	}

	// ********** cacheable **********

	@Override
	public Cacheable2_0 getCacheable() {
		return this.cacheable;
	}

	protected Cacheable2_0 buildCacheable() {
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
		CacheableReference2_0 parentEntity = (CacheableReference2_0) this.getParentEntity();
		return (parentEntity == null) ? null : parentEntity.getCacheable();
	}

//	@Override
//	public Iterable<String> getJavaCompletionProposals(int pos,
//			Filter<String> filter) {
//		Iterable<String> result = super.getJavaCompletionProposals(pos, filter);
//		if (result != null) {
//			return result;
//		}
//		result = this.getTypeDefContainer().getJavaCompletionProposals(pos, filter);
//		if (result != null) {
//			return result;
//		}
//		return null;
//	}

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


