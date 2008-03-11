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
package org.jboss.tools.hibernate.wizard.fieldmapping;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.dialog.EditKeyDialog;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.JoinMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;


/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldMappingWizardPage4 extends WizardPage {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage4.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private String persistentClassName;
	private IPersistentField field;	
    private List list1;
    private List list2;
    private List list3;
	private Button RemoveMappingButton;
	private Button AddMappingButton;
	private Button EditJoinMappingButton;
	private boolean dirty=false;	
	public class EditJoinDlg extends ListDialog{//New dialog to Simple ID property
		private CombinedBeanPropertySourceBase bp;
		private PropertySheetPage page;
		private JoinMapping jm;
 	    public EditJoinDlg(Shell parent,JoinMapping jm) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("FieldMappingWizardPage4.editjointitle"));
 			this.jm=jm;
 		}
 	    
 	    public boolean getDirty()
 	    {
 	      return bp.isDirty();
 	    }
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			root.setLayout(layout);
 			// #changed# by Konstantin Mishin on 21.09.2005 fixed for ESORM-40
 			page=new PropertySheetPageWithDescription();
 			// #changed#
 			
 			page.createControl(root);
 			//akuzmin 29.07.2005
 	        MenuManager menuMgr = new MenuManager("#PopupMenu");
 	        Menu menu = menuMgr.createContextMenu(page.getControl());
 			page.getControl().setMenu(menu);
 			//
 			
 		    GridData data = new GridData(SWT.FILL, SWT.FILL, true,true, 2, 1);//new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
 			data.widthHint = 570;
 			data.heightHint=300;		    
 			page.getControl().setLayoutData(data);
 			bp = (CombinedBeanPropertySourceBase) (jm.getPropertySource());
 			page.selectionChanged(null, new StructuredSelection(bp));
 			
 	      return root;
 	    }
 
 	    protected void okPressed() {
			setReturnCode(OK); 	    	
			this.close();
 	}	 	
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
	    TabFolder folder = new TabFolder(container, SWT.NONE);
	    GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.verticalSpan = 4;		
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		folder.setLayoutData(data);
	    TabItem item1 = new TabItem(folder, SWT.NONE);
        item1.setText(BUNDLE.getString("FieldMappingWizardPage4.item1"));
		list1 = new List(folder,SWT.BORDER);
		list1.setBackground(new Color(null,255,255,255));
		list1.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				RemoveMappingButton.setEnabled(true);
				EditJoinMappingButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);
		
        item1.setControl(list1);
	    TabItem item2 = new TabItem(folder, SWT.NONE);
        item2.setText(BUNDLE.getString("FieldMappingWizardPage4.item2"));
		list2 = new List(folder,SWT.BORDER);
		list2.setBackground(new Color(null,255,255,255));
		list2.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				RemoveMappingButton.setEnabled(true);
				EditJoinMappingButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);
		
		item2.setControl(list2);
	    TabItem item3 = new TabItem(folder, SWT.NONE);
        item3.setText(BUNDLE.getString("FieldMappingWizardPage4.item3"));
		list3 = new List(folder,SWT.BORDER);
		list3.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				RemoveMappingButton.setEnabled(true);
				EditJoinMappingButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);
		
        item3.setControl(list3);

		Label label1 = new Label(container, SWT.NULL);
		label1.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		label1.setLayoutData(data);
		
		Label label2 = new Label(container, SWT.NULL);
		label2.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label2.setLayoutData(data);
		
		AddMappingButton= new Button(container, SWT.PUSH);
		AddMappingButton.setText(BUNDLE.getString("FieldMappingWizardPage4.addbutton"));
		AddMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddMaping();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    AddMappingButton.setLayoutData(data);

		Label label3 = new Label(container, SWT.NULL);
		label3.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label3.setLayoutData(data);
		
		RemoveMappingButton= new Button(container, SWT.PUSH);
		RemoveMappingButton.setText(BUNDLE.getString("FieldMappingWizardPage4.removebutton"));
		RemoveMappingButton.setEnabled(false);
		RemoveMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("FieldMappingWizardPage4.deldlgtitle"), BUNDLE.getString("FieldMappingWizardPage4.deldlgdescr")))
			 	{
				RemoveMaping();
			    FormList();
			 	}
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		RemoveMappingButton.setLayoutData(data);
		
		Label label4 = new Label(container, SWT.NULL);
		label4.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label4.setLayoutData(data);
		
		EditJoinMappingButton= new Button(container, SWT.PUSH);
		EditJoinMappingButton.setText(BUNDLE.getString("FieldMappingWizardPage4.joinbutton"));
		EditJoinMappingButton.setEnabled(false);		
		EditJoinMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    showJoinProperties();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		EditJoinMappingButton.setLayoutData(data);

		
		FormList();
		setControl(container);		
	}
	
	public void showJoinProperties() {
		boolean delmapping=false;
		Iterator iter =((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).getJoinIterator();			
		while ( iter.hasNext() ) {
			IJoinMapping jm =(IJoinMapping)iter.next();
			 Iterator propiterator=jm.getPropertyIterator();
			  while ( propiterator.hasNext() ) {
				  if (((PropertyMapping)propiterator.next()).getPersistentField().getName().equals(field.getName()))//find nessesarry join
				  {
						Dialog dlg=new EditJoinDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),(JoinMapping) jm);
					 	dlg.open();
					 	if (((EditJoinDlg)dlg).getDirty())
					 		setDirty(true);
					  delmapping=true;
					  break;
				  }
			  }
			  if (delmapping) break;
		}
	}

	public FieldMappingWizardPage4(IMapping mod, String persistentClassName, IPersistentField field){
		super("FieldMappingWizard");
		setTitle(BUNDLE.getString("FieldMappingWizardPage4.title"));
		setDescription(BUNDLE.getString("FieldMappingWizardPage4.description"));
		this.persistentClassName=persistentClassName;
		this.field=field;
		this.mod = mod;

	}	

	public void FormList()
	{
		boolean nesmapping=false;
		list1.removeAll();
		list2.removeAll();
		list3.removeAll();
		Iterator iter =((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).getJoinIterator();			
		while ( iter.hasNext() ) {
			JoinMapping jm =(JoinMapping)iter.next();
			 Iterator propiterator=jm.getPropertyIterator();
			  while ( propiterator.hasNext() ) {
				  PropertyMapping pm=(PropertyMapping)propiterator.next();
				  if ((pm!=null) &&(pm.getPersistentField()!=null) && (pm.getPersistentField().getName().equals(field.getName())))//find nessesarry join
				  {
					  list1.add(jm.getTable().getName());
					  Iterator keyiter=jm.getKey().getColumnIterator();
					  while ( keyiter.hasNext() ) {
						  list2.add(((Column)keyiter.next()).getName());
					  }
					  nesmapping=true;					  
					  break;
				  }
			  }
			  if (nesmapping)
			  {
				  propiterator=jm.getPropertyIterator();
				  while ( propiterator.hasNext() ) {
					  PropertyMapping pm =(PropertyMapping)propiterator.next();
					  list3.add(jm.getTable().getName()+" -> "+pm.getPersistentField().getName());
				  }
				  nesmapping=false;

			  }
				  
		}
		RemoveMappingButton.setEnabled(false);
		EditJoinMappingButton.setEnabled(false);

	}
	
	private void RemoveMaping()
	{
						  IJoinMapping jm =findJoinMaping();
						  if (jm!=null)
						  {
							  ((ClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).removeJoin(jm);
							  ((FieldMappingWizard)this.getWizard()).buildNonJoinMapping();
						  }
	}

	
	private IJoinMapping findJoinMaping()
	{
			Iterator iter =((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).getJoinIterator();			
			while ( iter.hasNext() ) {
				IJoinMapping jm =(IJoinMapping)iter.next();
				 Iterator propiterator=jm.getPropertyIterator();
				  while ( propiterator.hasNext() ) {
					  if (((PropertyMapping)propiterator.next()).getPersistentField().getName().equals(field.getName()))//find nessesarry join
					  {
						  return jm; 
					  }
				  }
			}
			
			return null;
	
	}
	
	private void AddMaping()
	{
		SimpleValueMapping key=null;
		try {
			key = ((FieldMappingWizard)getWizard()).buildJoinMappingKey();
		} catch (CoreException e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
		}
		Dialog dlg=new EditKeyDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),mod,mod.findClass(persistentClassName).getPersistentClassMapping().getDatabaseTable(),key);
//TODO akuzmin: add maximum key columns number here		
//		((EditKeyDialog)dlg).setMaxColumnnumber(2);
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
		{
			RemoveMaping();
			IDatabaseTable table=((EditKeyDialog)dlg).getTable();
			String[] columnName=((EditKeyDialog)dlg).getColumns();//get selected key

			((FieldMappingWizard)this.getWizard()).buildJoinMapping(table,columnName,key);
			FormList();
		}
	}

	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}
