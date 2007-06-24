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
package org.jboss.tools.hibernate.wizard.treemodel;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 15, 2005
 */
public class TablesLabelProvider extends LabelProvider {
	private Map imageCache = new HashMap(4);
	private ResourceBundle BUNDLE = ViewPlugin.BUNDLE_IMAGE;	
	private ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle(TablesLabelProvider.class.getPackage().getName() + "." + BUNDLE_NAME);	
	public static final String BUNDLE_NAME = "treemodel"; 
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof TablesTreeModel) {
    		return ((TablesTreeModel) element).getName();
    		}
		else 
		{
			RuntimeException myException;
			if (element != null && element.getClass() != null )
				myException = new NestableRuntimeException("Unknown type of element in tree of type: " + element.getClass().getName());
			else myException = new NestableRuntimeException("Unknown type of element in tree of type: " + element);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE_TEXT.getString("TreeModel.MessageDialogTitle"), null);
			throw myException;
		}
	}

	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		
		if (element instanceof TablesTreeModel) {
			if (((TablesTreeModel) element).getChildren().length!=0)
				descriptor =ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseSchema"));
			else
				if (((TablesTreeModel) element).isIsview())
					descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseView"));
				else
					descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseTable"));
		} 		
		else 
		{
			RuntimeException myException;
			if (element != null && element.getClass() != null )
				myException = new NestableRuntimeException("Unknown type of element in tree of type: " + element.getClass().getName());
			else myException = new NestableRuntimeException("Unknown type of element in tree of type: " + element);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE_TEXT.getString("TreeModel.MessageDialogTitle"), null);
			throw myException;
		}


		// obtain the cached image corresponding to the descriptor
		Image image = (Image) imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
	

}
