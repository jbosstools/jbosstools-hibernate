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

package org.hibernate.eclipse.jdt.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.dialogs.PackageSelectionDialog;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.hibernate.eclipse.console.wizards.UpDownListComposite;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class AddRemoveTableComposite extends UpDownListComposite {

	public AddRemoveTableComposite(Composite parent, int style) {
		super(parent, style, "", //$NON-NLS-1$
				false, new JavaElementLabelProvider(),
				ArrayContentProvider.getInstance());
		setUpDownVisible(false);
	}
	
	@Override
	protected String[] getAddButtonLabels() {
		return new String[] { JdtUiMessages.AddRemoveTableComposite_add_class,
							  JdtUiMessages.AddRemoveTableComposite_add_package};
	}
	
	protected void createColumns(Table table) {
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setWidth(350);
	}
	
	protected Object[] handleAdd(int i) {
		
			SelectionDialog dialog= null;
			try {
				IJavaProject[] projects = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProjects();
				
				int includeMask = IJavaSearchScope.SOURCES | IJavaSearchScope.REFERENCED_PROJECTS;
				IJavaSearchScope scope = SearchEngine.createJavaSearchScope(projects, includeMask);
				
				if (i == 0){
					dialog=
					JavaUI.createTypeDialog(
						getShell(),
						PlatformUI.getWorkbench().getProgressService(),
						scope,
						IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES,
						true);
					dialog.setTitle(JdtUiMessages.AddRemoveTableComposite_java_types_title); 
					dialog.setMessage(JdtUiMessages.AddRemoveTableComposite_java_select_types);
				} else if (i == 1){
					dialog = new JavaPackageSelectionDialog(getShell(), scope);
					dialog.setTitle(JdtUiMessages.AddRemoveTableComposite_java_packages_title); 
					dialog.setMessage(JdtUiMessages.AddRemoveTableComposite_java_select_packages);
				} else {
					return null;
				}					

				if (dialog.open() == IDialogConstants.CANCEL_ID)
					return null;

				return dialog.getResult();
			} catch (JavaModelException jme) {
				return null;
			}
	}

}

@SuppressWarnings("restriction")
class JavaPackageSelectionDialog extends PackageSelectionDialog {
	
	public JavaPackageSelectionDialog(Shell parent, IJavaSearchScope scope) {
		super(parent,
				PlatformUI.getWorkbench().getProgressService(),
				PackageSelectionDialog.F_HIDE_EMPTY_INNER,
				scope);
		setIgnoreCase(false);
		setMultipleSelection(true);
	}
	
	@Override
	public void setElements(Object[] elements) {
		List<IPackageFragment> javaPackages = new ArrayList<IPackageFragment>();
		for (Object element : elements) {
			if (element instanceof IPackageFragment) {
				IPackageFragment pkg = (IPackageFragment) element;
				try {
					if (pkg.containsJavaResources()) javaPackages.add(pkg);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}				
			}
		}
		super.setElements(javaPackages.toArray());
	}
	
}

