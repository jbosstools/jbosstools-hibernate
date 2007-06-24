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
package org.jboss.tools.hibernate.wizard.mappingwizard;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.DiscriminatorPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.AddColumnDialog;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;



/**
 * @author kaa
 * Discriminator mapping page
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage5 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage5.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private String SelectedPC;
	private PropertySheetPage page;
	private Button AddMappingButton;
	private Button RemoveMappingButton;
	private Button SelectMappingButton;
	private BeanPropertySourceBase bp;
	private boolean dirty=false;
//	private Text list;
	public class AddDlg extends ListDialog{
	    private Combo ClassList;
 	    public AddDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage5.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			root.setLayout(layout);
 			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage5.combo"));
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label1.setLayoutData(data);
 			
 			
 			ClassList = new Combo(root, SWT.READ_ONLY);
 			Iterator iter = (mod.findClass(SelectedPC).getPersistentClassMapping().getDatabaseTable().getColumnIterator());
 			Column curcol=null;
 			while ( iter.hasNext() ) {
 				curcol=(Column)iter.next();
 				if (IsReadyDescriminator(curcol.getName()))
 				ClassList.add(curcol.getName());
 			}
 			
 			if (ClassList.getItemCount()>0)
 				ClassList.setText(ClassList.getItem(0));

 			ClassList.setLayout(layout);
 			
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.widthHint = 220;
 			data.heightHint = 26;
 			ClassList.setLayoutData(data);
 			
 			
 	      return root;
 	    }
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			if (ClassList.getItemCount()==0)
				this.getButton(IDialogConstants.OK_ID).setEnabled(false);			
		}
 	    
 	    protected void okPressed() {
 	    	if (ClassList.indexOf(ClassList.getText())>=0)
 	    	{
			RemoveMaping();				
 			((HibernateAutoMapping)mod.getAutoMappingService()).createDiscriminatorMapping((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping()),(HibernateConfiguration)mod.getConfiguration(),ClassList.getText());
 	    	}
        this.close();
 	}	 	
	}
//----------------------------------
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
		
		Group gr = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 2;
		gr.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 5);
	    groupData.heightHint=450;
	    groupData.widthHint=350;
	    gr.setLayoutData(groupData);
		
    	page=new PropertySheetPage();
		page.createControl(gr);
		page.getControl().setSize(442,400);
		//akuzmin 29.07.2005
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu menu = menuMgr.createContextMenu(page.getControl());
		page.getControl().setMenu(menu);
		//
		
//testing=================================================================	
//		page.getControl().addControlListener(new ControlAdapter() {
//            public void controlResized(ControlEvent e) {
//                //updateColumnSizes();
//            }
//            });
//
//		//TableTree tableTree=(TableTree)
//		page.getControl().getData();
//		//TableTreeItem [] treeA =tableTree.getItems();
//		//treeA[0]..setData("",o);
//testing=================================================================================		
		GridData data =new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL); 
		data.verticalSpan = 3;
		data.horizontalSpan=2;
		//data.horizontalIndent=9;
		page.getControl().setLayoutData(data);

		AddMappingButton= new Button(container, SWT.PUSH);
		GridData data1= new GridData(GridData.HORIZONTAL_ALIGN_END |GridData.VERTICAL_ALIGN_BEGINNING);
		data1.verticalIndent=8;
		data1.horizontalIndent=5;
		AddMappingButton.setLayoutData(data1);
		AddMappingButton.setText(BUNDLE.getString("MappingWizardPage5.addbutton"));//Create discriminator column...
		
		AddMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				AddMaping();
			}
		});

		SelectMappingButton= new Button(container, SWT.PUSH);
	    data1=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
	    data1.horizontalIndent=5;
		SelectMappingButton.setLayoutData(data1);
		SelectMappingButton.setText(BUNDLE.getString("MappingWizardPage5.selectbutton"));
		SelectMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				SetNewDescriminator();
			}
		});
		
	    
	    RemoveMappingButton= new Button(container, SWT.PUSH);
		data1=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data1.horizontalIndent=5;
		RemoveMappingButton.setLayoutData(data1);
	    RemoveMappingButton.setText(BUNDLE.getString("MappingWizardPage5.removebutton"));
	    RemoveMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				RemoveMaping();
			}
		});
	
		setControl(container);		
		
	}
	public MappingWizardPage5(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage5.description")+"\n"+BUNDLE.getString("MappingWizardPage5.descriptionpage"));
	  	this.mod=mod;
	}

	public void SetSelectedPC(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
	}

	public void FormList()
	{
//		list.setText("");		
	    IPersistentClass PertsistentClassesobj=mod.findClass(SelectedPC);
	    if (((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getDiscriminator()!=null)
		{
			page.createControl(this.getShell().getParent());
			page.getControl().setEnabled(true);
		bp = new BeanPropertySourceBase((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping()));
		bp.setPropertyDescriptors(DiscriminatorPropertyDescriptorsHolder.getInstance());
		page.selectionChanged(null, new StructuredSelection(bp));
		RemoveMappingButton.setEnabled(true);
		SelectMappingButton.setEnabled(false);
		AddMappingButton.setEnabled(false);
//		Iterator iter = ((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getDiscriminator().getColumnIterator();
//
//		Column curcol=null;
//		while ( iter.hasNext() ) {
//			curcol=(Column)iter.next();
//			list.setText(curcol.getName());
//		}
		
		}
		else
		{
			page.createControl(this.getShell().getParent());
			page.getControl().setEnabled(false);
			RemoveMappingButton.setEnabled(false);
			SelectMappingButton.setEnabled(true);
			AddMappingButton.setEnabled(true);			
		}
	}
	private void AddMaping()//add column and make it as discriminator
	{
		Dialog dlg=new AddColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
			RemoveMaping();
 			((HibernateAutoMapping)mod.getAutoMappingService()).createDiscriminatorMapping((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping()),(HibernateConfiguration)mod.getConfiguration(),((AddColumnDialog)dlg).getColumnName());
			FormList();
	 	}
	}
	
	private void RemoveMaping()
	{
//		if (!list.equals(""))
//		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage5.deldlgtitle"), BUNDLE.getString("MappingWizardPage5.deldlgdescr")))
//	 	{		
		((Table)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable()).removeColumn(((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDiscriminatorColumnName());
		((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setDiscriminator(null);
		setDirty(true);
		FormList();
//	 	}
	
	}

	private void SetNewDescriminator() {
		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	 	dlg.open();
	 	if (dlg.getReturnCode()==0)
		this.getWizard().getContainer().updateButtons();
	}

	private boolean IsReadyDescriminator(String Colimnname) {
		return (((MappingWizard)this.getWizard()).IsFieldColumn(Colimnname)); 
	}
	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		if (bp!=null)
			return dirty||bp.isDirty();
		else 
			return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}
