/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.JoinColumn;
import org.eclipse.jpt.core.context.RelationshipMapping;
import org.eclipse.jpt.core.context.RelationshipReference;
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
public class NamingStrategyMappingTools {
	
	public static String buildJoinTableDefaultName(RelationshipReference relationshipReference) {
		if (relationshipReference.getJpaProject().getDataSource().connectionProfileIsActive()) {
			return buildDbJoinTableDefaultName(relationshipReference);
		}
		
		RelationshipMapping relationshipMapping = relationshipReference.getRelationshipMapping();
		if (relationshipMapping == null) {
			return null;
		}
		Entity ownerEntity = (Entity) relationshipReference.getTypeMapping();
		org.eclipse.jpt.core.context.Table ownerTable = ownerEntity.getTable();
		if (ownerTable == null) {
			return null;
		}
		
		Entity targetEntity = relationshipMapping.getResolvedTargetEntity();
		if (targetEntity == null) {
			return null;
		}
		
		org.eclipse.jpt.core.context.Table targetTable = targetEntity.getTable();
		if (targetTable == null) {
			return null;
		}
		
		NamingStrategy ns = getJpaProject(relationshipReference).getNamingStrategy();
		if (getJpaProject(relationshipReference).isNamingStrategyEnabled() && ns != null){
			/*
			 * By testing generated DDL I have found for JPA console configuration:
			 * 1) first parameter of the method is always fully qualified owner entity class name
			 * 2) second and forth parameters of the method are always fully qualified target entity class name
			 * 3) third parameter of the method is name attribute of @Table annotation,
			 * 		if it is not specified, then it is *unqualified* name attribute of @Entity annotation
			 * 		if @Entity annotation not specified it is *unqualified* name of the target entity class.
			 * 4) fifth parameter is owner entity field name (even if @Column annotation set different name)
			 * 
			 */			
			try {
				String targetEntityName = targetEntity.getPersistentType().getName();
				String ownerEntityName = ownerEntity.getPersistentType().getName();
				String propName = relationshipMapping.getPersistentAttribute().getName();
				return ns.collectionTableName(ownerEntityName, targetTable.getName(),
						targetEntityName, targetTable.getName(), propName);
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return ownerTable.getName() + '_' + targetTable.getName();
	}
	
	public static HibernateJpaProject getJpaProject(RelationshipReference relationshipReference){
		return (HibernateJpaProject)relationshipReference.getJpaProject();
	}
	
	protected static String buildDbJoinTableDefaultName(RelationshipReference relationshipReference) {
		Table owningTable = relationshipReference.getTypeMapping().getPrimaryDbTable();
		if (owningTable == null) {
			return null;
		}
		RelationshipMapping relationshipMapping = relationshipReference.getRelationshipMapping();
		if (relationshipMapping == null) {
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
		String name = owningTable.getName() + '_' + targetTable.getName();
		return owningTable.getDatabase().convertNameToIdentifier(name);
	}
	
	public static String buildJoinColumnDefaultName(JoinColumn joinColumn, JoinColumn.Owner owner) {
		if (owner.joinColumnsSize() != 1) {
			return null;
		}
		String prefix = owner.getAttributeName();
		if (prefix == null) {
			Entity targetEntity = owner.getRelationshipTarget();
			if (targetEntity == null) {
				return null;
			}
			prefix = targetEntity.getName();
		}
		// not sure which of these is correct...
		// (the spec implies that the referenced column is always the
		// primary key column of the target entity)
		// Column targetColumn = joinColumn.getTargetPrimaryKeyDbColumn();
		String targetColumnName = joinColumn.getReferencedColumnName();
		if (targetColumnName == null) {
			return null;
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
