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
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.AbstractValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;

/**
 * @author kaa - akuzmin@exadel.com
 *	12.06.2005
 */

public class FieldMappingSimpleEditor extends Composite {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingComponentEditor.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private PropertySheetPage page;
	private IPersistentValueMapping mapping;
	private IPropertyMapping prop;
	private boolean isCatch=true;
	private CombinedBeanPropertySourceBase bp;
	private BeanPropertySourceBase bps;
	
	public FieldMappingSimpleEditor(Composite parent,IMapping mod,String persistentClassName) {
			super(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1,false);
			setLayout(layout);
			// #changed# by Konstantin Mishin on 16.09.2005 fixed for ESORM-40
			//page=new PropertySheetPage();
			page=new PropertySheetPageWithDescription();
			// #changed#
			page.createControl(this);
			
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
			data.widthHint = 550;
		    data.heightHint=300;
			data.grabExcessVerticalSpace=true;
			page.getControl().setSize(540,680);//were 1000 500
			page.getControl().addMouseListener(
					new MouseAdapter(){

				public void mouseDown(MouseEvent e) {
					if (prop.getValue() instanceof CollectionMapping)
					{
						
						if ((bps.isRefresh())&&isCatch())
						{
							bps=(BeanPropertySourceBase) ((AbstractValueMapping)mapping).getPropertySource();
							page.selectionChanged(null, new StructuredSelection(bps));
						}
					}
					else
					{
						if (bp.isRefresh())
						{
							bp=(CombinedBeanPropertySourceBase) prop.getPropertySource(mapping);
							page.selectionChanged(null, new StructuredSelection(bp));
						}
					}
				}});			
			page.getControl().setLayoutData(data);
		
	}
	
	public void FormList(IPersistentValueMapping mapping,IPropertyMapping prop)
	{
		this.mapping=mapping;
		this.prop=prop;
		if (prop.getValue() instanceof CollectionMapping)
		{
			bps=(BeanPropertySourceBase) ((AbstractValueMapping)mapping).getPropertySource();
			page.selectionChanged(null, new StructuredSelection(bps));
		}
		else
		{
			bp=(CombinedBeanPropertySourceBase) prop.getPropertySource(mapping);		
			page.selectionChanged(null, new StructuredSelection(bp));
		}
	}

	/**
	 * @return Returns the page.
	 */
	public PropertySheetPage getPage() {
		return page;
	}

	/**
	 * @return Returns the bps.
	 */
	public BeanPropertySourceBase getBps() {
		return bps;
	}

	/**
	 * @param isCatch The isCatch to set.
	 */
	public void setCatch(boolean isCatch) {
		this.isCatch = isCatch;
	}

	/**
	 * @return Returns the isCatch.
	 */
	public boolean isCatch() {
		return isCatch;
	}

	public boolean isDirty() {
		if (bps!=null)
			return bps.isDirty();
		else 
			if (bp!=null)
				return bp.isDirty();
			else return false;
	}


}
