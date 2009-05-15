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
import org.eclipse.swt.widgets.Tree;

public class NewHibernateMappingElementsSelectionPage extends WizardPage {

	// the current selection
	private IStructuredSelection fCurrentSelection;

	private TreeViewer fViewer;

	private boolean fAllowMultiple = true;

	private int fWidth = 50;

	private int fHeight = 18;

	public NewHibernateMappingElementsSelectionPage(IStructuredSelection selection) {
		super("", "", null);
		fCurrentSelection = selection;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		createTreeViewer(composite);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(fWidth);
		data.heightHint = convertHeightInCharsToPixels(fHeight);

		Tree treeWidget = fViewer.getTree();
		treeWidget.setLayoutData(data);
		setControl(composite);
	}

	protected TreeViewer createTreeViewer(Composite composite) {
		int style = SWT.BORDER | (fAllowMultiple ?  SWT.MULTI : SWT.SINGLE);
		fViewer = new TreeViewer(new Tree(composite, style));
		fViewer.setContentProvider(new StandardJavaElementContentProvider());
		fViewer.setLabelProvider(new JavaElementLabelProvider());
		fViewer.setFilters(getFilters());
		fViewer.addSelectionChangedListener(getSelectionChangedListener());
		fViewer.setInput(getInput());
		fViewer.setSelection(fCurrentSelection, true);
		return fViewer;
	}

	protected ViewerFilter[] getFilters(){
		return new ViewerFilter[] { new ViewerFilter() {

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

		} };
	}

	protected Object getInput(){
		return JavaCore.create( ResourcesPlugin.getWorkspace().getRoot() );
	}

	public IStructuredSelection getSelection(){
		return fCurrentSelection;
	}

	protected ISelectionChangedListener getSelectionChangedListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				fCurrentSelection = (IStructuredSelection) event.getSelection();
				updateStatus();
			}
		};
	}

	public void setAllowMultiple(boolean isAllowMultiple){
		fAllowMultiple = isAllowMultiple;
	}

	protected void updateStatus() {
		// TODO Auto-generated method stub
	}
}
