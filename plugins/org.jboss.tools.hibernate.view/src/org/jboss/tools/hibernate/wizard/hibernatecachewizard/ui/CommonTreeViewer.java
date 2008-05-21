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
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel.*;


/**
 * Common tree viewer of page of HibernateCacheWizard
 * 
 * @author YK
 *
 */
public class CommonTreeViewer extends TreeViewer 
{
	private TreeViewerStuffer stf = new TreeViewerStuffer();
	
	public CommonTreeViewer(Composite parent)
	{
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		setContentProvider(new CachedCommonTreeContentProvider());
		setLabelProvider(new CachedCommonTreeLableProvider());		
	}
	public TreeDataModel getItemByPoint(Point pt)
	{
		Item tdm = getItem(pt.x, pt.y);
		return (TreeDataModel)tdm.getData();
	}
	public TreeViewerStuffer getStuffer()
	{
		return ( new TreeViewerStuffer() );
	}	
	public TreeDataModel stuffViewer(TreeViewerStuffer stuffer)
	{
		return stuffer.stuff();
	}
	/**
	 * Content Provider for common tree viewer of page of HibernateCacheWizard
	 * @author YK
	 */
	protected class CachedCommonTreeContentProvider implements ITreeContentProvider
	{
	    public Object[] getChildren( Object parentElement )
	    {
			 return ( (TreeDataModel)parentElement ).getChildren();
	    }

	    public Object getParent( Object element )
	    {
	        return null;
	    }

	    public boolean hasChildren( Object element )
	    {
	        Object[] children = getChildren( element );

	        return ( children != null );
	    }

	    public Object[] getElements( Object parent )
	    {
	        return getChildren( parent );
	    }
		public void dispose(  )
		{}

		public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
		{}
	}
	
	/**
	 * Label Provider for common tree viewer of page of HibernateCacheWizard
	 * @author YK
	 */
	protected class CachedCommonTreeLableProvider extends LabelProvider
	{
		public String getText( Object element )
		{
			String name;
			name = (element != null) ? ((TreeDataModel)element).getName() : super.getText(element); 
			return name;
		}
	}


	public class TreeViewerStuffer
	{
		public TreeDataModel stuff()
		{
			return null;
		}
	}
}
