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
package org.jboss.tools.hibernate.view.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.hibernate.cfg.Configuration;


/**
 * @author Konstantin Mishin
 *
 */
public class ObjectEditorInput implements IEditorInput{
	
	protected Object fObject;
	protected Configuration configuration;

	public ObjectEditorInput(Object object) {
		fObject = object;
	}

	public ObjectEditorInput(Configuration configuration, Object object) {
		fObject = object;
		this.configuration = configuration;
	}

	public Object getObject() {
		return fObject;
	}


	public boolean exists() {
		return false;
	}


	public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
	}


	public String getName() {
		return "";
	}


	public IPersistableElement getPersistable() {
		return null;
	}


	public String getToolTipText() {
		return "";
	}


	public Object getAdapter(Class adapter) {
		return null;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

}
