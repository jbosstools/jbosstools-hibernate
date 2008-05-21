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
package org.jboss.tools.hibernate.wizard.queries;

import java.util.ResourceBundle;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author yan
 *
 */
public class QueryResultLabelProvider extends LabelProvider {

	private static ResourceBundle BUNDLE_IMAGE=ViewPlugin.BUNDLE_IMAGE;
	private Image object,property,error,empty;
	
	
	public Image getImage(Object element) {
		QueryResultItem item=(QueryResultItem)element;
		if (item.getSource() instanceof Throwable) {
			if (error==null) {
				error=ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("NamedQueriesWizard.error")).createImage();
			}
			return error;
		} else if (item.isTrivial()) {
			if (item.isRoot() && item.getSource()==null) {
				if (empty==null) {
					empty=ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("NamedQueriesWizard.object_none")).createImage();
				}
				return empty;
			}
			if (property==null) {
				property=ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("NamedQueriesWizard.property")).createImage();
			}
			return property;
		} else {
			if (object==null) {
				object=ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("NamedQueriesWizard.object")).createImage();
			}
			return object;
		}
	}

	public String getText(Object element) {
		return element.toString();
	}


}
