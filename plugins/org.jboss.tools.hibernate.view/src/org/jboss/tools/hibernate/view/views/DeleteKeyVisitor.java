/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.view.views;

import org.eclipse.jface.action.IAction;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;


/**
 * @author Konstantin Mishin
 *
 */
public class DeleteKeyVisitor  implements IOrmModelVisitor {

	public Object visitOrmProject(IOrmProject project, Object argument) {
		return null;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		return null;		
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		return null;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return null;
	}

	public Object visitPackage(IPackage pakage, Object argument) {
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.removeClassesAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.removeConfigAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		return null;
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.removeClassAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		return null;
	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		// #added# by Konstantin Mishin on 18.11.2005 fixed for ESORM-352
		if (mapping!=null)
			return visitPersistentClass(mapping.getPersistentClass(),argument);
		// #added#
		return null;
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}
	
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		// #added# by Konstantin Mishin on 18.11.2005 fixed for ESORM-352
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.removeNamedQueryAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		// #added#
		return null;
	}
}
