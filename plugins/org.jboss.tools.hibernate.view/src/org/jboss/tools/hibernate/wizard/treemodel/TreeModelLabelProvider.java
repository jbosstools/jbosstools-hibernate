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
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;


/**
 * @author tau
 * 04.04.2006
 */
abstract public class TreeModelLabelProvider extends LabelProvider {
	private Map imageCache = new HashMap(4);
	private ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle(HibernateJarsLabelProvider.class.getPackage().getName() + "." + BUNDLE_NAME);	
	public static final String BUNDLE_NAME = "treemodel"; 
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	abstract public String getText(Object element);
//	{
//		if (element instanceof TreeModel) {
//			// if (((TreeModel) element).getParent().getParent()==null){
//			// if (((TreeModel) element).getName() == null || ((TreeModel)
//			// element).getName().trim().length() == 0 )
//			// return BUNDLE_TEXT.getString("TreeModel.DefaultPackageName");
//			// }
//			return ((TreeModel) element).getName();
//		} else {
//			RuntimeException myException = getRuntimeException(element);
//			ExceptionHandler.displayMessageDialog(myException, ViewPlugin
//					.getActiveWorkbenchShell(), BUNDLE_TEXT
//					.getString("TreeModel.HibernateJarsTitle"), null);
//			throw myException;
//		}
//	}

	abstract  public Image getImage(Object element);
//	{
//		ImageDescriptor descriptor = null;
//		
//		if (element instanceof TreeModel) {
//			if (((TreeModel) element).getChildren().length!=0)
//				descriptor =ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Package"));
//			else
//				descriptor = ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentClass"));
//		} 		
//		else 
//		{
//			RuntimeException myException = getRuntimeException(element);
//			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE_TEXT.getString("TreeModel.MessageDialogTitle"), null);
//			throw myException;
//		}
//
//
//		// obtain the cached image corresponding to the descriptor
//		Image image = (Image) imageCache.get(descriptor);
//		if (image == null) {
//			image = descriptor.createImage();
//			imageCache.put(descriptor, image);
//		}
//		return image;
//	}

	public RuntimeException getRuntimeException(Object element) {
		RuntimeException myException = null;
		if (element != null && element.getClass() != null) {
			myException = new NestableRuntimeException(BUNDLE_TEXT.getString("TreeModel.UnknownType") + ": "
							+ element.getClass().getName());
		} else {
			myException = new NestableRuntimeException(BUNDLE_TEXT.getString("TreeModel.UnknownType") + ": " + element);
		}
		return myException;
	}
	
	public Image getImage(ImageDescriptor descriptor) {
		if (descriptor == null) return null;
		Image image = (Image) imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

}
