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

import java.util.ResourceBundle;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;



/**
 * @author kaa
 *
 * Persistent classes properties page
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage2 extends WizardPage{
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage2.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private String SelectedPC;
	private IMapping mod;
	private PropertySheetPage page;
	private BeanPropertySourceBase bp;
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		Group gr = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		//Grlayout.numColumns = 3;
		gr.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    groupData.heightHint=450;
	    groupData.widthHint=619;
	    gr.setLayoutData(groupData);
		container.setLayout(layout);
		page=new PropertySheetPageWithDescription();
		page.createControl(gr);
		
		
	    GridData data = new GridData(SWT.FILL, SWT.FILL, true,true, 1, 1);//new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		page.getControl().setSize(600,350);
		page.getControl().setLayoutData(data);
		FormList();//fill all data
		page.getControl().getSize();
		page.getControl().addMouseListener(
				new MouseAdapter(){

			public void mouseDown(MouseEvent e) {
				if ((bp!=null)&&(bp.isRefresh())){
					FormList();
					bp.setRefresh(false);
				}
			}

});
		
		setControl(container);		
	}
	
	public MappingWizardPage2(IMapping mod,String SelectedPersistentClass){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage2.description")+"\n"+BUNDLE.getString("MappingWizardPage2.descriptionpage"));
	  	this.mod=mod;
		this.SelectedPC=SelectedPersistentClass;
	}

	public void FormList()
	{
		if (SelectedPC!=null)
		{
  	    IPersistentClass PertsistentClassesobj=mod.findClass(SelectedPC);
  	    if (PertsistentClassesobj==null)
  	    {
  	    	
  	    }
  	    else
		if (PertsistentClassesobj.getPersistentClassMapping()!=null)
		{
		 bp = (BeanPropertySourceBase) ((ClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getPropertySource();
		 page.selectionChanged(null, new StructuredSelection(bp));
		}
		}
	}

	public void SetSelectedPC(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
	}

	public boolean canFlipToNextPage() {
		IPersistentClass pc=mod.findClass(SelectedPC);
		if (pc.getPersistentClassMapping()!=null)
			return pc.getPersistentClassMapping().getDatabaseTable()!=null;
		else return false;
	}
	
	public boolean getDirty()
	{
		return bp.isDirty();
	}
}
