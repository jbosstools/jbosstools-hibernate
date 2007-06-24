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
package org.jboss.tools.hibernate.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModel;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 19, 2005
 */
public class ModelCheckedTreeSelectionDialog extends CheckedTreeSelectionDialog {
	
	private boolean alsoEnableButtons = false;
	
	public ModelCheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		alsoEnableButtons = false;		
	}
	
	public ModelCheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider, boolean alsoEnableButtons) {
		super(parent, labelProvider, contentProvider);
		this.alsoEnableButtons = alsoEnableButtons;		
	}	

	protected void okPressed() {
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.CheckedTreeSelectionDialog#create()
	 */
	public void create() {
		super.create();
		if (!alsoEnableButtons) getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.ListSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Control newcontrl = super.createDialogArea(parent);
		getTreeViewer().expandAll();
		getTreeViewer().collapseAll();
		getTreeViewer().addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
					if (event.getElement() instanceof TreeModel)
					{
						TreeModel elem=(TreeModel) event.getElement();
						checkElement(elem,event.getChecked());
					}
					updateOKStatus();
					if (getTreeViewer().getCheckedElements().length==0 && !alsoEnableButtons)
						getButton(IDialogConstants.OK_ID)
						.setEnabled(false);	
					else
						getButton(IDialogConstants.OK_ID)
						.setEnabled(true);
						
				}

			});

		getButton(IDialogConstants.SELECT_ALL_ID)
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (getTreeViewer().getCheckedElements().length == 0 && !alsoEnableButtons)
							getButton(IDialogConstants.OK_ID)
									.setEnabled(false);
						else
							getButton(IDialogConstants.OK_ID)
									.setEnabled(true);

					}
				});

		getButton(IDialogConstants.DESELECT_ALL_ID)
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (getTreeViewer().getCheckedElements().length == 0 && !alsoEnableButtons)
							getButton(IDialogConstants.OK_ID)
									.setEnabled(false);
						else
							getButton(IDialogConstants.OK_ID)
									.setEnabled(true);

					}
				});
		if (getTreeViewer().getTree().getItemCount()==0 && !alsoEnableButtons)
		{
			getButton(IDialogConstants.DESELECT_ALL_ID).setEnabled(false);
			getButton(IDialogConstants.SELECT_ALL_ID).setEnabled(false);
		}
		
		return newcontrl;
	}

	public void setAllSelections() {
		if ((getTreeViewer()!=null)&&(getTreeViewer().getInput() instanceof TreeModel))
		{
		Object[] selectedElements=((TreeModel)getTreeViewer().getInput()).getChildren();
		for (int i=0;i<selectedElements.length;i++)
			if (selectedElements[i] instanceof TreeModel)
			{
				TreeModel elem=(TreeModel) selectedElements[i];
				getTreeViewer().setChecked(elem,true);
				checkElement(elem,true);
			}
		updateOKStatus();
		if (getTreeViewer().getCheckedElements().length==0 && !alsoEnableButtons)
			getButton(IDialogConstants.OK_ID)
			.setEnabled(false);	
		else
			getButton(IDialogConstants.OK_ID)
			.setEnabled(true);
		}
	}
	
	private boolean isAllElemsSelected(CheckboxTreeViewer treeViewer, TreeModel parent,boolean status) {
		Object[] childs = parent.getChildren();
		for(int i=0;i<childs.length;i++)
			if (treeViewer.getChecked(childs[i])!=status)
				return false;
		return true;
	}

	private void setElemState(CheckboxTreeViewer treeViewer, TreeModel elemshema, boolean checked) {
		Object[] childs = elemshema.getChildren();
		for(int i=0;i<childs.length;i++)
			treeViewer.setChecked(childs[i],checked);
	}
	
	private void checkElement(TreeModel elem,boolean state)
	{
		if (elem.getChildren().length!=0)//schemas
		{
			if (getTreeViewer().getGrayed(elem))
				getTreeViewer().setGrayChecked(elem,false);
			setElemState(getTreeViewer(),elem,state);
		}
		else//tables
		{
			if (!state)
			{
				if (isAllElemsSelected(getTreeViewer(),elem.getParent(),false))
				{
					getTreeViewer().setGrayChecked(elem.getParent(),false);
					getTreeViewer().setChecked(elem.getParent(),state);
				}
				else
					getTreeViewer().setGrayChecked(elem.getParent(),true);	
			}
			else
			{
				if (!isAllElemsSelected(getTreeViewer(),elem.getParent(),true))
					getTreeViewer().setGrayChecked(elem.getParent(),true);
				else getTreeViewer().setGrayChecked(elem.getParent(),false);
				getTreeViewer().setChecked(elem.getParent(),state);
			}
		}
		
	}
	
}
