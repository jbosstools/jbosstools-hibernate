/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

@SuppressWarnings("restriction")
public class NewHibernateMappingElementsSelectionPage extends WizardPage {
	
	// sizing constants
	private static final int SIZING_SELECTION_PANE_WIDTH = 320;
	
	private static final int SIZING_SELECTION_PANE_HEIGHT = 300;

	// the current selection
	private IStructuredSelection fCurrentSelection;

	private TreeViewer fViewer;

	private boolean fAllowMultiple = true;

	public NewHibernateMappingElementsSelectionPage(IStructuredSelection selection) {
		super("", "", null);	//$NON-NLS-1$ //$NON-NLS-2$
		fCurrentSelection = selection;
	}

	public void createControl(Composite parent) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = SIZING_SELECTION_PANE_WIDTH;
		gd.heightHint = SIZING_SELECTION_PANE_HEIGHT;
		parent.setLayoutData(gd);
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		
		createTreeViewer(composite);
		fViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(composite);
	}
	
	public IStructuredSelection getSelection(){
		return fCurrentSelection;
	}
	

	public void setAllowMultiple(boolean isAllowMultiple){
		fAllowMultiple = isAllowMultiple;
	}

	protected TreeViewer createTreeViewer(Composite composite) {
		int style = SWT.BORDER | SWT.V_SCROLL | (fAllowMultiple ?  SWT.MULTI : SWT.SINGLE);
		fViewer = new TreeViewer(composite, style);
		fViewer.setContentProvider(new StandardJavaElementContentProvider());
		fViewer.setLabelProvider(new JavaElementLabelProvider());
		fViewer.setFilters(getFilters());
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				fCurrentSelection = (IStructuredSelection) event.getSelection();
				updateStatus();
			}
		});
		fViewer.setInput(getInput());
		fViewer.setSelection(fCurrentSelection, true);
		return fViewer;
	}

	protected ViewerFilter[] getFilters(){
		return new ViewerFilter[] { new ViewerFilter() {			

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof JarPackageFragmentRoot) {
					return false;
				} else if (element instanceof ICompilationUnit) {
					return true;
				} else if (element instanceof IParent) {
					return hasCompilationUnits((IParent)element);
				}  else {
					return false;
				}
			}
			
			public boolean hasCompilationUnits(IParent parent){
				IJavaElement[] elements;
				try {
					elements = parent.getChildren();
					for (int i = 0; i < elements.length; i++) {
						if (elements[i].getElementType() == IJavaElement.COMPILATION_UNIT){
							return true;
						} else if (elements[i] instanceof IParent
								&& !(elements[i] instanceof JarPackageFragmentRoot)){
							if (hasCompilationUnits((IParent)elements[i])) {
								return true;
							}
						}
					}
				} catch (JavaModelException e) {
					return false;
				}
				return false;
			}

		} };
	}

	protected Object getInput(){
		return JavaCore.create( ResourcesPlugin.getWorkspace().getRoot() );
	}

	protected void updateStatus() {
		setPageComplete((fCurrentSelection != null && !fCurrentSelection.isEmpty()));
		if (isPageComplete()){
			setMessage(null);
		} else {
			setMessage(HibernateConsoleMessages.NewHibernateMappingElementsSelectionPage_select);
		}
	}
	
}
