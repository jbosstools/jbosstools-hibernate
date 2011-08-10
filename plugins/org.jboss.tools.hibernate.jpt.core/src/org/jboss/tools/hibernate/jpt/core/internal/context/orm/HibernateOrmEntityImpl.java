/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context.orm;

import java.util.ListIterator;

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.NotNullFilter;
import org.eclipse.jpt.common.utility.internal.iterables.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterables.TransformationIterable;
import org.eclipse.jpt.common.utility.internal.iterators.EmptyListIterator;
import org.eclipse.jpt.jpa.core.context.BaseJoinColumn;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.core.context.ReadOnlyTable;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.jpa.core.internal.context.orm.AbstractOrmEntity;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.orm.NullOrmCacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.CacheableHolder2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.orm.OrmCacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.PersistenceUnit2_0;
import org.eclipse.jpt.jpa.core.resource.orm.XmlEntity;
import org.eclipse.jpt.jpa.core.resource.orm.v2_0.XmlCacheable_2_0;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmEntityImpl extends AbstractOrmEntity<XmlEntity>
implements HibernateOrmEntity {

	protected OrmCacheable2_0 cacheable;

	public HibernateOrmEntityImpl(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		super(parent, resourceMapping);
		this.cacheable = this.buildCacheable();
	}

	// ********** synchronize/update **********

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.cacheable.synchronizeWithResourceModel();
	}

	@Override
	public void update() {
		super.update();
		this.cacheable.update();
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	// ********** cacheable **********

	@Override
	public OrmCacheable2_0 getCacheable() {
		return this.cacheable;
	}

	protected OrmCacheable2_0 buildCacheable() {
		return this.isJpa2_0Compatible() ?
				this.getContextNodeFactory2_0().buildOrmCacheable(this) :
				new NullOrmCacheable2_0(this);
	}

	@Override
	public boolean calculateDefaultCacheable() {
		CacheableHolder2_0 javaEntity = (CacheableHolder2_0) this.getJavaTypeMappingForDefaults();
		if (javaEntity != null) {
			return javaEntity.getCacheable().isCacheable();
		}

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
	public XmlCacheable_2_0 getXmlCacheable() {
		return this.getXmlTypeMapping();
	}

	@Override
	public String getPrimaryTableName() {
		return this.getTable().getDBTableName();
	}

	protected boolean tableNameIsValid(String tableName) {
		return this.tableIsUndefined || CollectionTools.contains(this.getAllAssociatedDBTableNames(), tableName);
	}
	
	public Iterable<String> getAllAssociatedDBTableNames() {
		return this.convertToDBNames(this.getAllAssociatedTables());
	}

	/**
	 * strip out <code>null</code> names
	 */
	protected Iterable<String> convertToDBNames(Iterable<ReadOnlyTable> tables) {
		return new FilteringIterable<String>(this.convertToDBNames_(tables), NotNullFilter.<String>instance());
	}

	/**
	 * Convert Table to it's DB name.
	 */
	protected Iterable<String> convertToDBNames_(Iterable<ReadOnlyTable> tables) {
		return new TransformationIterable<ReadOnlyTable, String>(tables) {
			@Override
			protected String transform(ReadOnlyTable t) {
				if (t instanceof HibernateTable) {
					return ((HibernateTable)t).getDBTableName();
				} else {
					return t.getName();//What is this???
				}
			}
		};
	}


	@Override
	protected PrimaryKeyJoinColumnOwner buildPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}

	// ********** pk join column owner **********
//do we need this?
	class HibernatePrimaryKeyJoinColumnOwner extends PrimaryKeyJoinColumnOwner
	{
		@Override
		public TypeMapping getTypeMapping() {
			return HibernateOrmEntityImpl.this;
		}

		public org.eclipse.jpt.jpa.db.Table getDbTable(String tableName) {
			return HibernateOrmEntityImpl.this.resolveDbTable(tableName);
		}

		@Override
		public org.eclipse.jpt.jpa.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateOrmEntityImpl.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		@Override
		public int joinColumnsSize() {
			return HibernateOrmEntityImpl.this.primaryKeyJoinColumnsSize();
		}

		@Override
		public String getDefaultColumnName() {
			if (joinColumnsSize() != 1) {
				return null;
			}
			Entity parentEntity = HibernateOrmEntityImpl.this.getParentEntity();
			String colName = (parentEntity == null)
			? getPrimaryKeyColumnName() : parentEntity.getPrimaryKeyColumnName();
			NamingStrategy ns = HibernateOrmEntityImpl.this.getJpaProject().getNamingStrategy();
			if (getJpaProject().isNamingStrategyEnabled() && ns != null){
				try {
					String name = ns.joinKeyColumnName(colName,	(parentEntity == null)
							? getTable().getName() : parentEntity.getPrimaryTableName());
					return name;
				} catch (Exception e) {
					IMessage m = HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,
							Messages.NAMING_STRATEGY_EXCEPTION,null);
					HibernateJptPlugin.logException(m.getText(), e);
				}
			}
			return colName;
		}

		@Override
		public String getDefaultTableName() {
			//FIXME: use NamingStrategy here
			return HibernateOrmEntityImpl.this.getPrimaryTableName();
		}

		@Override
		public TextRange getValidationTextRange() {
			return null;
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
	public HibernateOrmTable getTable() {
		return (HibernateOrmTable) super.getTable();
	}

	//******** TODO **********

	public void removeDiscriminatorFormula() {
		// TODO Auto-generated method stub
	}

	public GenericGenerator addGenericGenerator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public ListIterator<GenericGenerator> genericGenerators() {
		return EmptyListIterator.<GenericGenerator>instance();
	}

	public int genericGeneratorsSize() {
		return 0;
	}

	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		// TODO Auto-generated method stub
	}

	public void removeGenericGenerator(int index) {
		// TODO Auto-generated method stub
	}

	public void removeGenericGenerator(GenericGenerator generator) {
		// TODO Auto-generated method stub
	}

	public HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public HibernateNamedQuery addHibernateNamedQuery(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public ListIterator<HibernateNamedNativeQuery> hibernateNamedNativeQueries() {
		return EmptyListIterator.<HibernateNamedNativeQuery>instance();
	}

	public int hibernateNamedNativeQueriesSize() {
		return 0;
	}

	public ListIterator<HibernateNamedQuery> hibernateNamedQueries() {
		return EmptyListIterator.<HibernateNamedQuery>instance();
	}

	public int hibernateNamedQueriesSize() {
		// TODO Auto-generated method stub
		return 0;
	}


	public void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex) {
		// TODO Auto-generated method stub
	}

	public void moveHibernateNamedQuery(int targetIndex, int sourceIndex) {
		// TODO Auto-generated method stub
	}

	public void removeHibernateNamedNativeQuery(int index) {
		// TODO Auto-generated method stub
	}

	public void removeHibernateNamedNativeQuery(
			HibernateNamedNativeQuery namedNativeQuery) {
		// TODO Auto-generated method stub
	}

	public void removeHibernateNamedQuery(int index) {
		// TODO Auto-generated method stub
	}

	public void removeHibernateNamedQuery(HibernateNamedQuery namedQuery) {
		// TODO Auto-generated method stub
	}
}
