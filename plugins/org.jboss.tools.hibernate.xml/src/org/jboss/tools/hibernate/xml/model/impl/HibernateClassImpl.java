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

public class HibernateClassImpl extends OrderedObject2Impl {
    private static final long serialVersionUID = 3382417320218706443L;
    
    public String get(String s) {
    	if("details".equals(s)) {
    		String elementType = getAttributeValue("element type");
    		if("join".equals(elementType)) {
    			return "join " + "table=" + getAttributeValue("table");
    		} else {
    			return elementType + " name=" + getAttributeValue("name");
    		}
    	}
    	return super.get(s);
    }

}
