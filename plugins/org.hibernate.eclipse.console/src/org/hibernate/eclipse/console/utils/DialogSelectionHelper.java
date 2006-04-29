/*
 * Created on 2004-10-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hibernate.eclipse.console.utils;

import org.eclipse.core.resources.ResourcesPlugin;
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
		return null;		
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
		} else {
			return null;
		}
	}

}
