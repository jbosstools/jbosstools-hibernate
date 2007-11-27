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
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;


/**
 * @author alex
 *
 * Abstract ORM element implementation
 */
public abstract class AbstractOrmElement implements IOrmElement {

	private String name = "";
	
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmElement#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
		
	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	//akuzmin 15/03/2005	
	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getQualifiedName(TreeItem item) {
		// add 02.12.2005
		return StringUtils.parentItemName(item, this.getName());		
	}

}
