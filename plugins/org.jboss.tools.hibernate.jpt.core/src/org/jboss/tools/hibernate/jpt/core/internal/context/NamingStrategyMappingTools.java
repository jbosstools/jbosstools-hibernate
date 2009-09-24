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

package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.Column;
import org.eclipse.jpt.core.context.ColumnMapping;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.JoinColumn;
import org.eclipse.jpt.core.context.PersistentAttribute;
import org.eclipse.jpt.core.context.RelationshipMapping;
import org.eclipse.jpt.core.internal.context.MappingTools;
import org.eclipse.jpt.db.Table;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class NamingStrategyMappingTools extends MappingTools {
	
	public static String buildJoinTableDefaultName(RelationshipMapping relationshipMapping) {
		if (relationshipMapping.getJpaProject().getDataSource().connectionProfileIsActive()) {
			return buildDbJoinTableDefaultName(relationshipMapping);
		}
		// continue with a "best effort":
		String owningTableName = relationshipMapping.getTypeMapping().getPrimaryTableName();
		if (owningTableName == null) {
			return null;
		}
		Entity targetEntity = relationshipMapping.getResolvedTargetEntity();
		if (targetEntity == null) {
			return null;
		}
		String targetTableName = targetEntity.getPrimaryTableName();
		if (targetTableName == null) {
			return null;
		}

		HibernateJpaProject hibernateJpaProject = (HibernateJpaProject)targetEntity.getJpaProject();
		NamingStrategy ns = hibernateJpaProject.getNamingStrategy();		
		if (ns != null && hibernateJpaProject.isNamingStrategyEnabled()){
			try{
				String name = ns.collectionTableName(
						relationshipMapping.getEntity().getPersistentType().getName(),//+
						relationshipMapping.getEntity().getTable().getName(),//+
						targetEntity.getPersistentType().getName(),//+
						targetEntity.getTable().getName(),//+
						relationshipMapping.getName()//+
						);
				/*Table primaryTable = relationshipMapping.getTypeMapping().getPrimaryDbTable();			
				return primaryTable != null ? primaryTable.getDatabase().convertNameToIdentifier(name)
						: name;*/
				return name;
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return owningTableName + '_' + targetTableName;
	}
	
	protected static String buildDbJoinTableDefaultName(RelationshipMapping relationshipMapping) {
		Table owningTable = relationshipMapping.getTypeMapping().getPrimaryDbTable();
		if (owningTable == null) {
			return null;
		}
		Entity targetEntity = relationshipMapping.getResolvedTargetEntity();
		if (targetEntity == null) {
			return null;
		}
		Table targetTable = targetEntity.getPrimaryDbTable();
		if (targetTable == null) {
			return null;
		}
		
		HibernateJpaProject hibernateJpaProject = (HibernateJpaProject)targetEntity.getJpaProject();
		NamingStrategy ns = hibernateJpaProject.getNamingStrategy();
		if (ns != null && hibernateJpaProject.isNamingStrategyEnabled()){
			try {
				String name = ns.collectionTableName(
						relationshipMapping.getEntity().getPersistentType().getName(),//+
						relationshipMapping.getEntity().getTable().getName(),//+
						targetEntity.getPersistentType().getName(),//+
						targetEntity.getTable().getName(),//+
						relationshipMapping.getName()//+
						);
				//return owningTable.getDatabase().convertNameToIdentifier(name);
				return name;
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		String name = owningTable.getName() + '_' + targetTable.getName();
		//return owningTable.getDatabase().convertNameToIdentifier(name);
		return name;
	}
	
	public static String buildJoinColumnDefaultName(HibernateJoinColumn joinColumn) {		
		JoinColumn.Owner owner = joinColumn.getOwner();
		RelationshipMapping relationshipMapping = owner.getRelationshipMapping();
		if (relationshipMapping == null) {
			return null;
		}
		if (owner.joinColumnsSize() != 1) {
			return null;
		}
		String prefix = owner.getAttributeName();
		Entity targetEntity = owner.getTargetEntity();
		if (targetEntity == null) {
			return null;
		}
		String targetEntityName = targetEntity.getName();
		// not sure which of these is correct...
		// (the spec implies that the referenced column is always the
		// primary key column of the target entity)
		// Column targetColumn = joinColumn.getTargetPrimaryKeyDbColumn();
		String targetColumnName = joinColumn.getReferencedColumnName();
		if (targetColumnName == null) {
			return null;
		}
		HibernateJpaProject hibernateJpaProject = (HibernateJpaProject)targetEntity.getJpaProject();
		NamingStrategy ns = hibernateJpaProject.getNamingStrategy();
		if (ns != null && hibernateJpaProject.isNamingStrategyEnabled()){
			String logicalTargetColumnName = null;
			String name = null;
			try {
				if (joinColumn.getSpecifiedName() != null){
					name = ns.columnName(joinColumn.getSpecifiedName());
				} else {
					PersistentAttribute pattr = joinColumn.getReferencedPersistentAttribute();
					if (pattr != null && pattr.getMapping() instanceof ColumnMapping){
						Column column = ((ColumnMapping)pattr.getMapping()).getColumn();
						if (column != null){
							logicalTargetColumnName = ns.logicalColumnName(column.getSpecifiedName(), pattr.getName());
						} else {
							logicalTargetColumnName = ns.logicalColumnName(null, pattr.getName());
						}
					}
					
					name = ns.foreignKeyColumnName(prefix,
									targetEntity.getPersistentType().getName(),//+
									targetEntity.getTable().getName(),//+
									logicalTargetColumnName);//+
				}
				return name;
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		if (prefix == null) {
			prefix = targetEntityName;
		}
		String name = prefix + '_' + targetColumnName;
		// not sure which of these is correct...
		// converting the name to an identifier will result in the identifier
		// being delimited nearly every time (at least on non-Sybase/MS
		// databases); but that probably is not the intent of the spec...
		// return targetColumn.getDatabase().convertNameToIdentifier(name);
		return name;
	}
	

}
