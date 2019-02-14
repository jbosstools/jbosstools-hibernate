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

import org.eclipse.jpt.jpa.core.context.orm.OrmMappingJoinColumnRelationship;
import org.eclipse.jpt.jpa.core.internal.context.orm.GenericOrmMappingJoinColumnRelationshipStrategy;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmJoinColumnJoiningStrategy extends GenericOrmMappingJoinColumnRelationshipStrategy {

	public HibernateOrmJoinColumnJoiningStrategy(OrmMappingJoinColumnRelationship parent) {
		this(parent, false);
	}

	public HibernateOrmJoinColumnJoiningStrategy(OrmMappingJoinColumnRelationship parent, boolean targetForeignKey) {
		super(parent, targetForeignKey);
	}

	/*@Override
	protected void validateJoinColumnName(OrmJoinColumn joinColumn,
			List<IMessage> messages) {
		if ( ! joinColumn.isResolved() && joinColumn.getDbTable() != null) {
			if (((HibernateJoinColumn)joinColumn).getDBColumnName() != null) {
				if (getRelationshipMapping().getPersistentAttribute().isVirtual()) {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_COLUMN_UNRESOLVED_NAME,
							new String[] {getRelationshipMapping().getName(), ((HibernateJoinColumn)joinColumn).getDBColumnName()},
							joinColumn,
							joinColumn.getNameTextRange()
						)
					);
				} else {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.JOIN_COLUMN_UNRESOLVED_NAME,
							new String[] {((HibernateJoinColumn)joinColumn).getDBColumnName()},
							joinColumn,
							joinColumn.getNameTextRange()
						)
					);
				}
			}
			else if (joinColumn.getOwner().joinColumnsSize() > 1) {
				if (getRelationshipMapping().getPersistentAttribute().isVirtual()) {
					messages.add(
							DefaultJpaValidationMessages.buildMessage(
								IMessage.HIGH_SEVERITY,
								JpaValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
								new String[] {getRelationshipMapping().getName()},
								joinColumn,
								joinColumn.getNameTextRange()
							)
						);
				}
				else {
					messages.add(
							DefaultJpaValidationMessages.buildMessage(
								IMessage.HIGH_SEVERITY,
								JpaValidationMessages.JOIN_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
								joinColumn,
								joinColumn.getNameTextRange()
							)
						);
				}
			}
		}
	}

	@Override
	protected void validationJoinColumnReferencedColumnName(
			OrmJoinColumn joinColumn, List<IMessage> messages) {
		if ( ! joinColumn.isReferencedColumnResolved() && joinColumn.getReferencedColumnDbTable() != null) {
			if (((HibernateJoinColumn)joinColumn).getReferencedDBColumnName() != null) {
				if (getRelationshipMapping().getPersistentAttribute().isVirtual()) {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME,
							new String[] {getRelationshipMapping().getName(),
									((HibernateJoinColumn)joinColumn).getReferencedDBColumnName(),
									((HibernateJoinColumn)joinColumn).getDBColumnName()},
							joinColumn,
							joinColumn.getReferencedColumnNameTextRange()
						)
					);
				} else {
					messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME,
							new String[] {((HibernateJoinColumn)joinColumn).getReferencedDBColumnName(),
									((HibernateJoinColumn)joinColumn).getDBColumnName()},
							joinColumn,
							joinColumn.getReferencedColumnNameTextRange()
						)
					);
				}
			}
			else if (joinColumn.getOwner().joinColumnsSize() > 1) {
				if (getRelationshipMapping().getPersistentAttribute().isVirtual()) {
					messages.add(
							DefaultJpaValidationMessages.buildMessage(
								IMessage.HIGH_SEVERITY,
								JpaValidationMessages.VIRTUAL_ATTRIBUTE_JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
								new String[] {((HibernateJoinColumn)joinColumn).getReferencedDBColumnName(),
										((HibernateJoinColumn)joinColumn).getDBColumnName()},
								joinColumn,
								joinColumn.getReferencedColumnNameTextRange()
							)
						);
				}
				else {
					messages.add(
							DefaultJpaValidationMessages.buildMessage(
								IMessage.HIGH_SEVERITY,
								JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
								joinColumn,
								joinColumn.getReferencedColumnNameTextRange()
							)
						);
				}
			}
		}
	}*/

}
