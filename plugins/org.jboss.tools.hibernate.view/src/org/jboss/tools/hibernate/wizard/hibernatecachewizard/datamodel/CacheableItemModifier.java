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
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel;


import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.ui.CachedRegionsPage;


public class CacheableItemModifier implements ICellModifier 
{
	private 	Viewer 				theViewer 		= null;
	private 	CachedRegionsPage	Callback		= null;
	
	public CacheableItemModifier(Viewer viewer)
	{
		theViewer = viewer;
	}
	public void setCallback(CachedRegionsPage callback)
	{
		Callback = callback;
	}
	public boolean canModify(Object element, String property) 
	{
		boolean sign = true;
		TreeDataModel tdm = (TreeDataModel)element;
		if(tdm == null || tdm.getParentName() == null)
		{		return false;		}
		if(!tdm.getParentName().equals(ICacheable.bundle.getString("HibernateCacheWizard.ROOT_ELEMENT_NAME")))
		{// do not modify item names
			sign = false;
		}
		return sign;
	}

	public Object getValue(Object element, String property) 
	{
		Callback.setEnabledMoveToButton(false);
		TreeDataModel tdm = (TreeDataModel)element;
		return (Object)tdm.getName();
	}

	public void modify(Object element, String property, Object value) 
	{
		String newname = (String)value;
 		if( (newname.length() < 1) || (newname.length() > 150)) return; //check bounds;
		TreeDataModel tdm 		= (TreeDataModel)((TreeItem)element).getData();
		if(!tdm.getName().equals(value))
			Callback.updateTreeStructure((String)value, tdm.getName());
		Callback.setEnabledMoveToButton(true);
	}
}
