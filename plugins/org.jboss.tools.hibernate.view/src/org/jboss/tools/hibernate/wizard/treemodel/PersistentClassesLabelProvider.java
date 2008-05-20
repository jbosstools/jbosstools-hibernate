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

import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 18, 2005
 */
//edit tau 04.04.2006
public class PersistentClassesLabelProvider extends TreeModelLabelProvider {
	private ResourceBundle BUNDLE = ViewPlugin.BUNDLE_IMAGE;
	private ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle(PersistentClassesLabelProvider.class.getPackage().getName() + "." + BUNDLE_NAME);	
	public static final String BUNDLE_NAME = "treemodel"; 
	
	public String getText(Object element) {
		if (element instanceof TreeModel) {
			if (((TreeModel) element).getParent().getParent()==null)
				{
				if (((TreeModel) element).getName() == null || ((TreeModel) element).getName().trim().length() == 0 )
					return BUNDLE_TEXT.getString("TreeModel.DefaultPackageName"); 
				}
    		return ((TreeModel) element).getName();
    		}
		else 
		{
			RuntimeException myException = getRuntimeException(element);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE_TEXT.getString("TreeModel.MessageDialogTitle"), null);
			throw myException;
		}
	}

	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		
		if (element instanceof TreeModel) {
			if (((TreeModel) element).getChildren().length!=0)
				descriptor =ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Package"));
			else
				descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentClass"));
		} 		
		else 
		{
			RuntimeException myException = getRuntimeException(element);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE_TEXT.getString("TreeModel.MessageDialogTitle"), null);
			throw myException;
		}

		return getImage(descriptor);
	}
	

}
