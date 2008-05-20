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

import org.jboss.tools.common.model.impl.OrderedObjectImpl;

public class HibernateElementImpl extends OrderedObjectImpl {
    private static final long serialVersionUID = 220538596631608391L;
	
	public String getPresentationString() {
		String presentationAttr = getModelEntity().getProperty("presentationAttribute");
		return ((presentationAttr != null) ? getAttributeValue(presentationAttr) : "") +
			"[" + getAttributeValue("element type") + "]";
	}
	
	public String name() {
		return "" + getPathPart();
	}
	
	public String getPathPart() {
		return "element";
	}
	
}
