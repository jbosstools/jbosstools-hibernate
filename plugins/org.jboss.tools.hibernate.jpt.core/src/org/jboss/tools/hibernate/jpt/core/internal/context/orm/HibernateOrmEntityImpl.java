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

import org.eclipse.jpt.core.context.BaseJoinColumn;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.TypeMapping;
import org.eclipse.jpt.core.context.orm.OrmBaseJoinColumn;
import org.eclipse.jpt.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.core.internal.context.orm.AbstractOrmEntity;
import org.eclipse.jpt.core.resource.orm.XmlEntity;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.utility.internal.iterators.EmptyListIterator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.DiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmEntityImpl extends AbstractOrmEntity
implements HibernateOrmEntity {

	public HibernateOrmEntityImpl(OrmPersistentType parent,
			XmlEntity resourceMapping) {
		super(parent, resourceMapping);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	
/*
   @Override
	protected String buildDefaultName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (!isMetadataComplete()) {
			JavaEntity javaEntity = getJavaEntity();
			if (javaEntity != null) {
				return javaEntity.getName();
			}
		}
		String className = getClass_();
		if (className != null) {
			try {
				String shortCalssName = ClassTools.shortNameForClassNamed(className);
				return ns == null ? shortCalssName : ns.classToTableName(shortCalssName);
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return null;
	}
	
	
	@Override
	public String getPrimaryTableName() {
		return this.getTable().getDBTableName();
	}*/

	/**
	 * Convert Table to it's DB name.
	 */
/*	@Override
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
	}*/
	
	@Override
	protected OrmBaseJoinColumn.Owner createPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}
	
	// ********** pk join column owner **********

	class HibernatePrimaryKeyJoinColumnOwner implements OrmBaseJoinColumn.Owner
	{
		public TypeMapping getTypeMapping() {
			return HibernateOrmEntityImpl.this;
		}

		public org.eclipse.jpt.db.Table getDbTable(String tableName) {
			return HibernateOrmEntityImpl.this.getDbTable(tableName);
		}

		public org.eclipse.jpt.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateOrmEntityImpl.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		public int joinColumnsSize() {
			return HibernateOrmEntityImpl.this.primaryKeyJoinColumnsSize();
		}
		
		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateOrmEntityImpl.this.defaultPrimaryKeyJoinColumns.contains(joinColumn);
		}
		
		public String getDefaultColumnName() {
			if (joinColumnsSize() != 1) {
				return null;
			}
			Entity parentEntity = HibernateOrmEntityImpl.this.getParentEntity();
			NamingStrategy ns = HibernateOrmEntityImpl.this.getJpaProject().getNamingStrategy();
			if (getJpaProject().isNamingStrategyEnabled() && ns != null){				
				try {
					String name = ns.joinKeyColumnName(parentEntity.getPrimaryKeyColumnName(),
							parentEntity.getPrimaryTableName());
					return name;
				} catch (Exception e) {
					Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
							Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
					HibernateJptPlugin.logException(m.getText(), e);
				}
			}
			return parentEntity.getPrimaryKeyColumnName();
		}
		
		public TextRange getValidationTextRange() {
			return null;
		}
	}
	
	@Override
	public HibernateOrmTable getTable() {
		return (HibernateOrmTable) super.getTable();
	}
	
	//******** TODO **********

	public DiscriminatorFormula addDiscriminatorFormula() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiscriminatorFormula getDiscriminatorFormula() {
		// TODO Auto-generated method stub
		return null;
	}

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
