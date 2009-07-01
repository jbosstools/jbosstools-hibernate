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

public class HibConfigCacheImpl extends RegularObject2Impl {
	private static final long serialVersionUID = 1L;
	
	public String getAttributeValue(String name) {
		if("item".equals(name)) { //$NON-NLS-1$
			String[] as = {"class", "collection"}; //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < as.length; i++) {
				String s = getAttributeValue(as[i]);
				if(s != null && s.length() > 0) return as[i] + "=" + s; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}
		return super.getAttributeValue(name);
	}

	public String setAttributeValue(String name, String value) {
		if("item".equals(name)) { //$NON-NLS-1$
			return getAttributeValue(name);
		}
		return super.setAttributeValue(name, value);
	}

}
