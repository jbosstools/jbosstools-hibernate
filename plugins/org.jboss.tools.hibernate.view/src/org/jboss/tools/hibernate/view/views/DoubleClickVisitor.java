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
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.viewers.TreeViewer;
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


public class DoubleClickVisitor implements IOrmModelVisitor {

	public Object visitOrmProject(IOrmProject project, Object argument) {
		return null;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		/* del tau 16.02.2005
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = actionExplorerVisitor.mappingWizardAction;
			action.run();
		}
		*/
		return null;		
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		// #added# by Konstantin Mishin on 2005/08/08				
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.columnPropertyDialogAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		// #added# 
		return null;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return null;
	}

	public Object visitPackage(IPackage pakage, Object argument) {
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.mappingWizardAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
//			IAction action = actionExplorerVisitor.hibernateConnectionWizardAction; 20050620 yan
			IAction action = ViewsAction.openMappingAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		// 20050620 <yan>
//		IResource resource = storage.getResource();
//		if (resource != null && resource instanceof IFile) {
//			IWorkbenchPage page = ViewPlugin.getPage();
//			try {
//				//IDE.openEditor(page, (IFile) resource, "org.jboss.tools.hibernate.editor.L4thFileLauncher" );
//				IDE.openEditor(page, (IFile) resource);
//			} catch (PartInitException e) {
//				ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),"Error","Error at opening a file " + resource.getName());
//			}
//		}
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.openMappingStorageAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		// </yan>

		return null;
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		// 200506020 <yan>
//		ICompilationUnit sourceCode = clazz.getSourceCode();
//		if (sourceCode != null) {
//			IWorkbenchPage page = ViewPlugin.getPage();
//			IResource resource = sourceCode.getResource();
//			if (resource instanceof IFile){
//				try {
//					IDE.openEditor(page, (IFile) resource);
//				} catch (PartInitException e) {
//					ExceptionHandler.log(e, null);
//				}				
//			}
//		}
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.openMappingStorageAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		// </yan>

		//showMessage("Double-click detected", (TreeViewer) argument);
		return null;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		// edit 09.06.2005
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.fieldMappingWizardAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		return null;
	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		// 20050620 <yan>
		if (mapping!=null) return visitPersistentClass(mapping.getPersistentClass(),argument);
		// </yan>
		return null;
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}

/*
	private void showMessage(String message, TreeViewer viewer) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Tree View",
			message);
	}
*/

	// tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		// 20050727 <yan>
		if (argument instanceof ActionExplorerVisitor) {
			ActionExplorerVisitor actionExplorerVisitor = (ActionExplorerVisitor) argument;
			IAction action = ViewsAction.editNamedQueryAction.setViewer(actionExplorerVisitor.getViewer());
			action.run();
		}
		//</yan>
		return null;
	}
}
