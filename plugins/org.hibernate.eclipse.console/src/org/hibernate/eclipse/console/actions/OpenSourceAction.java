/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.actions;

import java.io.FileNotFoundException;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author Dmitry Geraskov
 */

@SuppressWarnings("restriction")
public class OpenSourceAction extends SelectionListenerAction {

	public static final String OPENSOURCE_ACTIONID = "actionid.opensource"; //$NON-NLS-1$

	private String imageFilePath = "icons/images/java.gif"; //$NON-NLS-1$

	public OpenSourceAction() {
		super(HibernateConsoleMessages.OpenSourceAction_open_source_file);
		setToolTipText(HibernateConsoleMessages.OpenSourceAction_open_source_file);
		setEnabled( true );
		setImageDescriptor(HibernateConsolePlugin.getImageDescriptor(imageFilePath));
		setId(OPENSOURCE_ACTIONID);
	}

	public void run() {
		IStructuredSelection sel = getStructuredSelection();
		if (!(sel instanceof TreeSelection)) {
			return;
		}
		TreePath[] paths = ((TreeSelection)sel).getPaths();
		for (int i = 0; i < paths.length; i++) {
			TreePath path = paths[i];
			Object lastSegment = path.getLastSegment();
	    	PersistentClass persClass = getPersistentClass(lastSegment);
			ConsoleConfiguration consoleConfig = (ConsoleConfiguration)(path.getSegment(0));

			String fullyQualifiedName = null;
			if (lastSegment instanceof Property){
				Object prevSegment = path.getParentPath().getLastSegment();
				if (prevSegment instanceof Property
						&& ((Property)prevSegment).isComposite()){
					fullyQualifiedName =((Component)((Property) prevSegment).getValue()).getComponentClassName();
				}
			}
			if (fullyQualifiedName == null && persClass != null){
				fullyQualifiedName = persClass.getClassName();
			}

			try {
				run(consoleConfig, lastSegment, fullyQualifiedName);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenSourceAction_cannot_find_source_file, e);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenSourceAction_cannot_open_source_file, e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenSourceAction_cannot_find_source_file, e);
			}
		}
	}

	/**
	 * @param consoleConfig
	 * @param selection
	 * @param fullyQualifiedName
	 * @throws JavaModelException
	 * @throws PartInitException
	 * @throws FileNotFoundException
	 */
	public static IEditorPart run(ConsoleConfiguration consoleConfig, Object selection, 
			String fullyQualifiedName) throws JavaModelException, PartInitException, FileNotFoundException {
		if (fullyQualifiedName == null) return null;
		IJavaProject[] projs = ProjectUtils.findJavaProjects(consoleConfig);
		String remainder = null;
		IType type = null;
		IJavaProject proj = null;
		if (fullyQualifiedName.indexOf("$") > 0) { //$NON-NLS-1$
			remainder = fullyQualifiedName.substring(fullyQualifiedName.indexOf("$") + 1); //$NON-NLS-1$
			fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("$")); //$NON-NLS-1$
			for (int i = 0; i < projs.length && type == null; i++) {
				proj = projs[i];
				type = proj.findType(fullyQualifiedName);
			}
			while ( remainder.indexOf("$") > 0 ){ //$NON-NLS-1$
				String subtype = remainder.substring(0, fullyQualifiedName.indexOf("$")); //$NON-NLS-1$
				type = type.getType(subtype);
				remainder = remainder.substring(fullyQualifiedName.indexOf("$") + 1); //$NON-NLS-1$
			}
			type = type.getType(remainder);
		} else {
			for (int i = 0; i < projs.length && type == null; i++) {
				proj = projs[i];
				type = proj.findType(fullyQualifiedName);
			}
		}
		IJavaElement jElement = null;
		if (selection instanceof Property){
			final String selectionName =((Property)selection).getName(); 
			final IType typeSave = type;
			while (true) {
				jElement = type.getField(selectionName);
				if (jElement != null && jElement.exists()) {
					break;
				}
				String parentClassName = ProjectUtils.getParentTypename(proj, type.getFullyQualifiedName());
				if (parentClassName == null) {
					break;
				}
				type = proj.findType(parentClassName);
				for (int i = 0; i < projs.length && type == null; i++) {
					proj = projs[i];
					type = proj.findType(fullyQualifiedName);
				}
				if (type == null) {
					break;
				}
			};
			// do not find element - restore type
			if (jElement == null || !jElement.exists()) {
				type = typeSave;
			}
		}
		if (jElement == null) {
			jElement = type;
		}
		IEditorPart editorPart = JavaUI.openInEditor(type);
		if (editorPart instanceof JavaEditor) {
			JavaEditor jEditor = (JavaEditor)editorPart;
			selectionToEditor(jElement, jEditor);
		}
		if (editorPart == null) {
			String out = NLS.bind(HibernateConsoleMessages.OpenSourceAction_source_file_for_class_not_found, fullyQualifiedName);
			throw new FileNotFoundException(out);
		}
		return editorPart;

	}

	private PersistentClass getPersistentClass(Object selection){
    	if (selection instanceof Property){
    		return ((Property)selection).getPersistentClass();
		} else if (selection instanceof PersistentClass){
			return (PersistentClass)selection;
		} else {
			return null;
		}
	}

	private static void selectionToEditor(IJavaElement jElement, JavaEditor jEditor) {
		if (jEditor != null) {
			jEditor.setSelection(jElement);
		}
	}

	/*
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return super.getImageDescriptor();
	}
}
