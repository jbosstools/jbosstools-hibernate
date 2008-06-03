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

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.hibernate.xml.HibernateXMLPlugin;

public class RegularObject2Impl extends OrderedObjectImpl {
	private static final long serialVersionUID = 1L;
	String attribute = null;
	
	public String getAttribute() {
		if(attribute != null) return attribute;
		XAttribute[] as = getModelEntity().getAttributes();
		for (int i = 0; i < as.length; i++) {
			if("true".equals(as[i].getProperty("id"))) {
				attribute = as[i].getName();
				return attribute;
			}
		}
		HibernateXMLPlugin.log("No id attribute set for entity " + getModelEntity().getName());
		attribute = as[0].getName();
		return attribute;
	}
	
	public String name() {
		return "" + getAttributeValue(getAttribute());
	}

}
