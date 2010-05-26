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

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJoinColumn;
import org.eclipse.jpt.core.context.java.JavaJoinColumnEnabledRelationshipReference;
import org.eclipse.jpt.core.internal.context.java.GenericJavaJoinColumnJoiningStrategy;
import org.eclipse.jpt.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.core.internal.validation.JpaValidationMessages;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateJoinColumn;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaJoinColumnJoiningStrategy extends
		GenericJavaJoinColumnJoiningStrategy {

	public HibernateJavaJoinColumnJoiningStrategy(
			JavaJoinColumnEnabledRelationshipReference parent) {
		super(parent);
	}

	protected void validateJoinColumnName(JavaJoinColumn joinColumn, List<IMessage> messages, CompilationUnit astRoot) {
		if ( ! joinColumn.isResolved() && joinColumn.getDbTable() != null) {
			if (((HibernateJoinColumn)joinColumn).getDBColumnName() != null) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.JOIN_COLUMN_UNRESOLVED_NAME,
						new String[] {((HibernateJoinColumn)joinColumn).getDBColumnName()}, 
						joinColumn,
						joinColumn.getNameTextRange(astRoot)
					)
				);
			}
			else if (joinColumn.getOwner().joinColumnsSize() > 1) {
				messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.JOIN_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
							joinColumn,
							joinColumn.getNameTextRange(astRoot)
						)
					);
			}
			//If the name is null and there is only one join-column, one of these validation messages will apply
			// 1. target entity does not have a primary key
			// 2. target entity is not specified
			// 3. target entity is not an entity
		}
	}
	
	protected void validationJoinColumnReferencedColumnName(JavaJoinColumn joinColumn, List<IMessage> messages, CompilationUnit astRoot) {
		if ( ! joinColumn.isReferencedColumnResolved() && joinColumn.getReferencedColumnDbTable() != null) {
			if (((HibernateJoinColumn)joinColumn).getReferencedDBColumnName() != null) {
				messages.add(
					DefaultJpaValidationMessages.buildMessage(
						IMessage.HIGH_SEVERITY,
						JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME,
						new String[] {((HibernateJoinColumn)joinColumn).getReferencedDBColumnName(), ((HibernateJavaJoinColumn)joinColumn).getDBColumnName()}, 
						joinColumn,
						joinColumn.getReferencedColumnNameTextRange(astRoot)
					)
				);
			}
			else if (joinColumn.getOwner().joinColumnsSize() > 1) {
				messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.HIGH_SEVERITY,
							JpaValidationMessages.JOIN_COLUMN_REFERENCED_COLUMN_UNRESOLVED_NAME_MULTIPLE_JOIN_COLUMNS,
							joinColumn,
							joinColumn.getReferencedColumnNameTextRange(astRoot)
						)
					);
			}
			//If the referenced column name is null and there is only one join-column, one of these validation messages will apply
			// 1. target entity does not have a primary key
			// 2. target entity is not specified
			// 3. target entity is not an entity			
		}
	}

}
