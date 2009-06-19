/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.utils;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.stringsubstitution.StringVariableLabelProvider;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
@SuppressWarnings("restriction")
public class DialogSelectionHelper extends org.hibernate.eclipse.console.utils.xpl.DialogSelectionHelper {

	
	/**
	 * Realize a Java Project selection dialog and return the first selected project,
	 * or null if there was none.
	 */
	public static IJavaProject chooseJavaProject(Shell shell, IJavaProject initialSelection, String title, String description) {
		IJavaProject[] projects;
		try {
			projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot() ).getJavaProjects();
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().log(e.getStatus() );
			projects= new IJavaProject[0];
		}
		
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(shell, labelProvider);
		dialog.setTitle(title);
		dialog.setMessage(description); 
		dialog.setElements(projects);
		
		IJavaProject javaProject = initialSelection;
		if (javaProject != null) {
			dialog.setInitialSelections(new Object[] { javaProject });
		}
		if (dialog.open() == Window.OK) {			
			return (IJavaProject) dialog.getFirstResult();
		}			
		return initialSelection;		
	}

	/**
	 * Realize a Persistence Unit selection dialog and return the first selected persistence unit,
	 * or null if there was none.
	 */
	public static String choosePersistenceUnit(Shell shell, String initialSelection, String title, String description,
			IJavaProject javaProject) {
		String[] availablePersistenceUnit = ProjectUtils.availablePersistenceUnits(javaProject);
		ILabelProvider labelProvider= new StringVariableLabelProvider();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(shell, labelProvider);
		dialog.setTitle(title);
		dialog.setMessage(description);
		dialog.setElements(availablePersistenceUnit);
		
		String persistenceUnit = initialSelection;
		if (persistenceUnit != null) {
			dialog.setInitialSelections(new Object[] { persistenceUnit });
		}
		if (dialog.open() == Window.OK) {
			return (String) dialog.getFirstResult();
		}
		return initialSelection;
	}

	static public String chooseImplementation(String supertype, String initialSelection, String title, Shell shell) {
		SelectionDialog dialog= null;
		try {
			final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
			// TODO: limit to a certain implementor
			dialog=
				JavaUI.createTypeDialog(
					shell,
					PlatformUI.getWorkbench().getProgressService(),
					scope,
					IJavaElementSearchConstants.CONSIDER_CLASSES,
					false, supertype);
		} catch (JavaModelException jme) {
			return null;
		}

		dialog.setTitle(title); 
		dialog.setMessage(title);
		
		if (dialog.open() == IDialogConstants.CANCEL_ID)
			return null;

		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= (IType) types[0];
			return type.getFullyQualifiedName('.');
		}
		return null;
	}

}
