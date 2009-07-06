/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.hibernate.console.ConsoleConfiguration;


public class ObjectEditorInput implements IEditorInput{
	
	protected Object fObject;
	protected ConsoleConfiguration configuration;

	public ObjectEditorInput(ConsoleConfiguration configuration, Object object) {
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
		return ""; //$NON-NLS-1$
	}


	public IPersistableElement getPersistable() {
		return null;
	}


	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}


	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public ConsoleConfiguration getConfiguration() {
		return configuration;
	}

	public boolean equals(Object obj) {
		return (obj instanceof ObjectEditorInput && ((ObjectEditorInput)obj).fObject == fObject);
	}

	public int hashCode() {
		return fObject.hashCode();
	}
}
