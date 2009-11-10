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

import java.util.StringTokenizer;

import org.jboss.tools.common.model.impl.OrderedObjectImpl;

public class HibernateElementImpl extends OrderedObjectImpl {
    private static final long serialVersionUID = 220538596631608391L;
	
	public String getPresentationString() {
		String suff = "[" + getAttributeValue("element type") + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String presentationAttr = getModelEntity().getProperty("presentationAttribute"); //$NON-NLS-1$
		if(presentationAttr != null) {
			StringTokenizer st = new StringTokenizer(presentationAttr, "|"); //$NON-NLS-1$
			while(st.hasMoreTokens()) {
				String att = st.nextToken();
				String v = getAttributeValue(att);
				if(v != null && v.length() > 0) return v + suff;
			}
		}
		return suff;
	}
	
	public String name() {
		return "" + getPathPart(); //$NON-NLS-1$
	}
	
	public String getPathPart() {
		return "element"; //$NON-NLS-1$
	}
	
}
