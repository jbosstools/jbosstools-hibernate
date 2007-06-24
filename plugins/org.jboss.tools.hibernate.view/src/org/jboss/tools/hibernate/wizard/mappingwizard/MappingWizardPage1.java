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


import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;



/**
 * @author kaa
 * Select persistent classes page
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage1 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
    //private Button PersistentClassesButton;
    private List list;
	private IMapping mod;
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 4;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("MappingWizardPage1.label"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label.setLayoutData(data);

		list = new List(container,SWT.BORDER|SWT.V_SCROLL);
		list.setBackground(new Color(null,255,255,255));
		list.addSelectionListener(new SelectionListener()
				{

					public void widgetSelected(SelectionEvent e) {
						setNext();	
					}

					public void widgetDefaultSelected(SelectionEvent e) {

						
					}
			}
		);
	    data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 3;
        int listHeight = list.getItemHeight() * 12;
        Rectangle trim = list.computeTrim(0, 0, 0, listHeight);
        data.heightHint = trim.height;
		list.setLayoutData(data);
		GetClasses();		
		setControl(container);		
	}
	public MappingWizardPage1(IMapping mod){//,SpecialWizardSupport support, int id) {
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage1.description")+"\n"+BUNDLE.getString("MappingWizardPage1.descriptionpage"));
	  	this.mod=mod;
	}

	public void GetClasses()
	{
		ArrayList pclist=new ArrayList();
		list.removeAll();
   	    IPersistentClass[] PertsistentClassesobj=mod.getPertsistentClasses();
	 	for (int i = 0; i < PertsistentClassesobj.length; i++)
	 	{
	 		pclist.add(PertsistentClassesobj[i].getName());
	 	}
	 	Collections.sort(pclist);
	 	for (int i = 0; i < pclist.size(); i++)
	 	{
	 		list.add(pclist.get(i).toString());
	 	}
	 	// #added# by Konstantin Mishin on 30.12.2005 fixed for ESORM-360
	 	list.setSelection(0);
	 	// #added#
	}
	
	public String getSelectedClass()
	{
		if (list.getSelection().length>0)
		return list.getSelection()[0];
		else return null;
	}

	public boolean canFlipToNextPage() {
		if (list.getSelection().length>0)
			return true;
		else
		return false;
	}

	public void setNext()
	{
		this.getWizard().getContainer().updateButtons();
	}
	
	
}
