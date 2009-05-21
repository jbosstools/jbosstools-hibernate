/*******************************************************************************
 * Copyright (c) 2000, 2005, 2006 IBM Corporation, JBoss Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Max Rydahl Andersen, JBoss Inc - More flexible file & directory choosing.
 *******************************************************************************/
package org.hibernate.eclipse.console.utils.xpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.hibernate.eclipse.console.FileFilter;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class DialogSelectionHelper {

	/**
	 *
	 * Shows the UI to select new JAR or ZIP archive entries located in the workspace.
	 * The dialog returns the selected entries or <code>null</code> if the dialog has
	 * been cancelled. The dialog does not apply any changes.
	 * @param shell The parent shell for the dialog.
	 * @param initialSelection The path of the element (container or archive) to initially select or <code>null</code> to not select an entry.
	 * @param usedEntries An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @param fileExtensions An array of file extensions.
	 * @param allowMultiple allow multiple selections.
	 * @param allowFiles TODO
	 * @param acceptedTypes TODO
	 *
	 * @return Returns the new classpath container entry paths or <code>null</code> if the dialog has
	 * been cancelled by the user.
	 *
	 * Inspired by BuildPathDialogAccess.chooseJAREntries from jdt.ui.wizards
	 */
	public static IPath[] chooseFileEntries(Shell shell, IPath initialSelection, IPath[] usedEntries, String title, String description, String[] fileExtensions, boolean allowMultiple, boolean allowDirectories, boolean allowFiles ) {
		if (usedEntries == null) {
			throw new IllegalArgumentException(HibernateConsoleMessages.DialogSelectionHelper_used_entries_must_be_notnull);
		}

		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		if(allowDirectories) {
			clazzes.add(IFolder.class);
			clazzes.add(IProject.class);
		}
		if(allowFiles) {
			clazzes.add(IFile.class);
		}
		Class<?>[] acceptedClasses = clazzes.toArray(new Class[clazzes.size()]);

		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true);
		List<IResource> usedFiles= new ArrayList<IResource>(usedEntries.length);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < usedEntries.length; i++) {
			IResource resource= root.findMember(usedEntries[i]);
			if (resource instanceof IFile) {
				usedFiles.add(resource);
			}
		}
		IResource focus= initialSelection != null ? root.findMember(initialSelection) : null;

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider() );
		dialog.setValidator(validator);
		dialog.setAllowMultiple(allowMultiple);
		dialog.setTitle(title);
		dialog.setMessage(description);
		dialog.addFilter(new FileFilter(fileExtensions, usedFiles, true, allowDirectories) );
		dialog.setInput(root);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME) );
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

	public static IPath[] chooseFolderEntries(Shell shell, IPath initialSelection, String title, String description, boolean allowMultiple) {
		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		clazzes.add(IFolder.class);
		clazzes.add(IProject.class);

		Class<?>[] acceptedClasses = clazzes.toArray(new Class[clazzes.size()]);

		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IResource focus= initialSelection != null ? root.findMember(initialSelection) : null;

		ElementTreeSelectionDialog dialog= null;
		dialog = new FolderSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider() );
		//	dialog = new FileFolderSelectionDialog(shell, allowMultiple, allowDirectories ? IResource.FOLDER : IResource.FILE );

		dialog.setValidator(validator);
		dialog.setAllowMultiple(allowMultiple);
		dialog.setTitle(title);
		dialog.setMessage(description);
		dialog.setInput(root);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME) );
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

}
