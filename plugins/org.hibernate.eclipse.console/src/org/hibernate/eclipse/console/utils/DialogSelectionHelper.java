/*
 * Created on 2004-10-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hibernate.eclipse.console.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.hibernate.eclipse.console.FileFilter;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
public class DialogSelectionHelper {

	/**
	 * 
	 * Shows the UI to select new JAR or ZIP archive entries located in the workspace.
	 * The dialog returns the selected entries or <code>null</code> if the dialog has
	 * been cancelled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialSelection The path of the element (container or archive) to initially select or <code>null</code> to not select an entry. 
	 * @param usedEntries An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @param fileExtensions An array of file extensions.
	 * @param allowMultiple allow multiple selections.
	 * @param acceptedTypes TODO
	 * @return Returns the new classpath container entry paths or <code>null</code> if the dialog has
	 * been cancelled by the user.
	 * 
	 * Inspired by BuildPathDialogAccess.chooseJAREntries from jdt.ui.wizards 
	 */
	public static IPath[] chooseFileEntries(Shell shell, IPath initialSelection, IPath[] usedEntries, String title, String description, String[] fileExtensions, boolean allowMultiple, boolean allowDirectories ) {
		if (usedEntries == null) {
			throw new IllegalArgumentException("used entries must be not-null");
		}
			
		
		Class[] acceptedClasses;
		if(!allowDirectories) {
			acceptedClasses = new Class[] { IFile.class };
		} else {
			acceptedClasses = new Class[] { IFile.class, IFolder.class };
		}
		
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true);
		ArrayList usedFiles= new ArrayList(usedEntries.length);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < usedEntries.length; i++) {
			IResource resource= root.findMember(usedEntries[i]);
			if (resource instanceof IFile) {
				usedFiles.add(resource);
			}
		}
		IResource focus= initialSelection != null ? root.findMember(initialSelection) : null;
		
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setValidator(validator);
		dialog.setAllowMultiple(allowMultiple);
		dialog.setTitle(title);
		dialog.setMessage(description);
		dialog.addFilter(new FileFilter(fileExtensions, usedFiles, true, allowDirectories));
		dialog.setInput(root);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
		dialog.setInitialSelection(focus);

		if (dialog.open() == Window.OK) {
			Object[] elements= dialog.getResult();
			IPath[] res= new IPath[elements.length];
			for (int i= 0; i < res.length; i++) {
				IResource elem= (IResource)elements[i];
				res[i]= elem.getFullPath();
			}
			return res;
		}
		return null;
	}

	/**
	 * Realize a Java Project selection dialog and return the first selected project,
	 * or null if there was none.
	 */
	public static IJavaProject chooseJavaProject(Shell shell, IJavaProject initialSelection, String title, String description) {
		IJavaProject[] projects;
		try {
			projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		} catch (JavaModelException e) {
			HibernateConsolePlugin.log(e.getStatus());
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
	
}
