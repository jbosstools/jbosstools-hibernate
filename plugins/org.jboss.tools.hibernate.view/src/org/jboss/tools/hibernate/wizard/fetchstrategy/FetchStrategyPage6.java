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
package org.jboss.tools.hibernate.wizard.fetchstrategy;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;


/**
 * @author kaa - akuzmin@exadel.com
 * Jul 7, 2005
 */
public class FetchStrategyPage6 extends WizardPage {
	public static final String BUNDLE_NAME = "fetchstrategy"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FetchStrategyPage6.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private List list;
	private Button LazyButton;
	private Button EagerButton;
	private ArrayList proplist;
	private Button ProxyButton;
	private Button ImmediateButton;
   
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 4;
		container.setLayout(layout);
		list = new List(container,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
		list.setBackground(new Color(null,255,255,255));
		GridData data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 4;
        int listHeight = list.getItemHeight() * 12;
        Rectangle trim = list.computeTrim(0, 0, 0, listHeight);
        data.heightHint = trim.height;
		list.setLayoutData(data);
		list.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				EagerButton.setEnabled(true);
				LazyButton.setEnabled(true);
				ProxyButton.setEnabled(true);
				// #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-379
				for(int i=0;i<list.getSelectionCount();i++)
					if (((IPropertyMapping) proplist.get(list.getSelectionIndices()[i])).getValue()
							instanceof ICollectionMapping) {
						ProxyButton.setEnabled(false);						
						break;
					}
				// #added#
				ImmediateButton.setEnabled(true);
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);
		
		ImmediateButton= new Button(container, SWT.PUSH);
		ImmediateButton.setText(BUNDLE.getString("FetchStrategyWizard.selectbutton"));
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=5;
		ImmediateButton.setLayoutData(data);
		ImmediateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				doImmediate();	
			}
		});
		
		LazyButton= new Button(container, SWT.PUSH);
		LazyButton.setText(BUNDLE.getString("FetchStrategyWizard.lazybutton"));
		//data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		GridData d=setButtonLayoutData( LazyButton);
		d.horizontalIndent=5;
		LazyButton.setLayoutData(d);
		//LazyButton.setLayoutData(data);
		LazyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				doLazy();	
			}
		});
		EagerButton= new Button(container, SWT.PUSH);
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=5;
		EagerButton.setText(BUNDLE.getString("FetchStrategyWizard.eagerbutton"));
		EagerButton.setLayoutData(data);
		EagerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doEager();	
			}

		});	
		ProxyButton= new Button(container, SWT.PUSH);
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=5;
		ProxyButton.setText(BUNDLE.getString("FetchStrategyWizard.proxybutton"));
		ProxyButton.setLayoutData(data);
		ProxyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doProxy();	
			}

		});	
	    
		setControl(container);
		refreshList();
	}

	protected void doImmediate() {
		if (list.getSelectionCount()>0)
		{
			for(int i=0;i<list.getSelectionCount();i++)
				((FetchStrategyWizard)getWizard()).doSelect((IPropertyMapping) proplist.get(list.getSelectionIndices()[i]));
			refreshList();	
		}
	}

	/**
	 * force to Proxy
	 */	
	protected void doProxy() {
		if (list.getSelectionCount()>0)
		{
			for(int i=0;i<list.getSelectionCount();i++)
				((FetchStrategyWizard)getWizard()).doProxy((IPropertyMapping) proplist.get(list.getSelectionIndices()[i]));
			refreshList();	
		}
	}

	/**
	 * force to Eager
	 */
	protected void doEager() {
		if (list.getSelectionCount()>0)
		{
			for(int i=0;i<list.getSelectionCount();i++)
				((FetchStrategyWizard)getWizard()).doEager((IPropertyMapping) proplist.get(list.getSelectionIndices()[i]));
			refreshList();	
		}
	}
	/**
	 * force to Lazy
	 */

	protected void doLazy() {
		if (list.getSelectionCount()>0)
		{
			for(int i=0;i<list.getSelectionCount();i++)
				((FetchStrategyWizard)getWizard()).doLazy((IPropertyMapping) proplist.get(list.getSelectionIndices()[i]));
			refreshList();	
		}
	}

	public FetchStrategyPage6() {
		super("wizardPage");
		setTitle(BUNDLE.getString("FetchStrategyWizard.title"));
		setDescription(BUNDLE.getString("FetchStrategyPage6.description"));
	}

	/**
	 * refresh list of Immediate fetching associations
	 */
	public void refreshList()
	{
		list.removeAll();
		proplist=((FetchStrategyWizard)getWizard()).findAssociations(this);
		if (proplist!=null)
		{
		for(int i=0;i<proplist.size();i++)
		list.add(((IPropertyMapping) proplist.get(i)).getPersistentField().getOwnerClass().getName()+"."+
				((IPropertyMapping) proplist.get(i)).getName()+":"+
				((IPropertyMapping) proplist.get(i)).getPersistentField().getType());
		}
		EagerButton.setEnabled(false);
		LazyButton.setEnabled(false);
		ProxyButton.setEnabled(false);
		ImmediateButton.setEnabled(false);
	}

}
