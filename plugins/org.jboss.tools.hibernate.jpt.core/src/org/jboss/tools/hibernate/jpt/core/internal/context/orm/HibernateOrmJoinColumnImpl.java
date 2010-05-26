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

import java.util.Iterator;

import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.PersistentAttribute;
import org.eclipse.jpt.core.context.RelationshipMapping;
import org.eclipse.jpt.core.context.XmlContextNode;
import org.eclipse.jpt.core.context.orm.OrmJoinColumn;
import org.eclipse.jpt.core.internal.context.orm.GenericOrmJoinColumn;
import org.eclipse.jpt.core.resource.orm.XmlJoinColumn;
import org.eclipse.jpt.db.Column;
import org.eclipse.jpt.db.Table;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinColumnImpl extends GenericOrmJoinColumn implements
		HibernateOrmJoinColumn {

	public HibernateOrmJoinColumnImpl(XmlContextNode parent, OrmJoinColumn.Owner owner,
			XmlJoinColumn resourceJoinColumn) {
		super(parent, owner, resourceJoinColumn);
	}
	
	@Override
	protected String getOwnerDefaultColumnName() {
		return NamingStrategyMappingTools.buildJoinColumnDefaultName(this);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	

	@Override
	public Column getDbColumn() {
		Table table = this.getDbTable();
		return (table == null) ? null : table.getColumnForIdentifier(this.getDBColumnName());
	}

	public String getDBColumnName() {
		return getSpecifiedDBColumnName() != null ? getSpecifiedDBColumnName()
				: getDefaultDBColumnName();
	}

	public String getDefaultDBColumnName() {
		return getDefaultName();
	}
	
	public String getSpecifiedDBColumnName() {
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(getSpecifiedName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedName();
	}
	
	@Override
	public Column getReferencedDbColumn() {
		Table table = this.getReferencedColumnDbTable();
		return (table == null) ? null : table.getColumnForIdentifier(this.getReferencedDBColumnName());
	}

	public String getReferencedDBColumnName() {
		return getReferencedSpecifiedDBColumnName() != null ? getReferencedSpecifiedDBColumnName()
				: getReferencedDefaultDBColumnName();
	}

	public String getReferencedDefaultDBColumnName() {
		return defaultReferencedColumnName;
	}

	public String getReferencedSpecifiedDBColumnName() {
		if (specifiedReferencedColumnName == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(specifiedReferencedColumnName);
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return specifiedReferencedColumnName;
	}
	
	public PersistentAttribute getReferencedPersistentAttribute() {
		RelationshipMapping relationshipMapping = this.getOwner().getRelationshipMapping();
		if (relationshipMapping == null) {
			return null;
		}
		if (this.getOwner().joinColumnsSize() != 1) {
			return null;
		}
		Entity targetEntity = this.getOwner().getTargetEntity();
		if (targetEntity == null) {
			return null;
		}
		PersistentAttribute pAttr = null;
		Iterator<PersistentAttribute> attributes = targetEntity.getPersistentType().allAttributes();
		for (Iterator<PersistentAttribute> stream = attributes; stream.hasNext();) {
			PersistentAttribute attribute = stream.next();
			String name = attribute.getPrimaryKeyColumnName();
			if (name != null) {
				if (pAttr == null){
					pAttr = attribute;
				} else {
					return null;
				}
			}			
		}
		return pAttr;
	}

}
