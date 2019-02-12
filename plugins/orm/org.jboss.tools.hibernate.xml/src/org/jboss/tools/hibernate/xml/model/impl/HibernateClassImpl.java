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

public class HibernateClassImpl extends HibClassImpl {
    private static final long serialVersionUID = 3382417320218706443L;
    
    public String get(String s) {
    	if("details".equals(s)) { //$NON-NLS-1$
    		String elementType = getAttributeValue("element type"); //$NON-NLS-1$
    		if("join".equals(elementType)) { //$NON-NLS-1$
    			return "join " + "table=" + getAttributeValue("table"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		} else {
    			return elementType + " name=" + getAttributeValue("name"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    	return super.get(s);
    }

}
