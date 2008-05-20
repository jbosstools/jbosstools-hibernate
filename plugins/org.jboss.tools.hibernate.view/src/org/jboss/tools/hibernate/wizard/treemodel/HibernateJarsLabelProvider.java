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
 * @author tau
 * 04.04.2006
 */
public class HibernateJarsLabelProvider extends TreeModelLabelProvider {
	private ResourceBundle BUNDLE = ViewPlugin.BUNDLE_IMAGE;
	private ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle(HibernateJarsLabelProvider.class.getPackage().getName() + "." + BUNDLE_NAME);	
	public static final String BUNDLE_NAME = "treemodel"; 
	
	public String getText(Object element) {
		if (element instanceof TreeModel) {
			return ((TreeModel) element).getName();
		} else {
			RuntimeException myException = getRuntimeException(element);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin
					.getActiveWorkbenchShell(), BUNDLE_TEXT
					.getString("TreeModel.HibernateJarsTitle"), null);
			throw myException;
		}
	
	}
		

	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof TreeModel) {
			if (((TreeModel) element).getChildren().length!=0)
				descriptor =ViewPlugin.getImageDescriptor(BUNDLE.getString("TreeModelLabel.fldr_obj"));
			else {
				String nameElement = ((TreeModel) element).getName();
				if (nameElement == null) {
				} else if (nameElement.indexOf(".jar") > 0) {
					descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("TreeModelLabel.jar_obj"));
				} else if (nameElement.indexOf(".txt") > 0) {
					descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("TreeModelLabel.file_obj"));
				} else if (nameElement.indexOf(".properties") > 0) {
					descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("TreeModelLabel.file_obj"));
				}
			}
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
