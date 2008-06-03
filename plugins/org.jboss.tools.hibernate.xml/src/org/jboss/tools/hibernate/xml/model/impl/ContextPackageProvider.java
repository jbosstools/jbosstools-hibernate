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
package org.jboss.tools.hibernate.xml.model.impl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.AccessibleJava.IContextPackageProvider;

public class ContextPackageProvider implements IContextPackageProvider {
	XModelObject object = null;

	public String getContextPackage() {
		while(object != null && object.getFileType() != XModelObject.FILE) object = object.getParent();
		if(object == null) return null;
		String s = object.getAttributeValue("package");
		if(s != null && s.length() == 0) return null;
		return s;
	}

	public void setObject(XModelObject object) {
		this.object = object;
	}

}
