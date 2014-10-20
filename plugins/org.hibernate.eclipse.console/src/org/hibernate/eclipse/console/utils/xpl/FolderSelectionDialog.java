package org.hibernate.eclipse.console.utils.xpl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class FolderSelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button newFolderButton;
	private IContainer selectedContainer;

	public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		setComparator(new ResourceComparator(ResourceComparator.NAME));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite result= (Composite)super.createDialogArea(parent);

		getTreeViewer().addSelectionChangedListener(this);

		Button button = new Button(result, SWT.PUSH);
		button.setText(HibernateConsoleMessages.FolderSelectionDialog_button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newFolderButtonPressed();
			}
		});
		button.setFont(parent.getFont());
		newFolderButton= button;

		applyDialogFont(result);

		return result;
	}

	private void updateNewFolderButtonState() {
		IStructuredSelection selection= (IStructuredSelection) getTreeViewer().getSelection();
		selectedContainer= null;
		if (selection.size() == 1) {
			Object first= selection.getFirstElement();
			if (first instanceof IContainer) {
				selectedContainer= (IContainer) first;
			}
		}
		newFolderButton.setEnabled(selectedContainer != null);
	}

	protected void newFolderButtonPressed() {
		NewFolderDialog dialog= new NewFolderDialog(getShell(), selectedContainer);
		if (dialog.open() == Window.OK) {
			TreeViewer treeViewer= getTreeViewer();
			treeViewer.refresh(selectedContainer);
			Object createdFolder= dialog.getResult()[0];
			treeViewer.reveal(createdFolder);
			treeViewer.setSelection(new StructuredSelection(createdFolder));
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		updateNewFolderButtonState();
	}



}
