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

public class DatabaseObjectImpl extends RegularObject2Impl {
	private static final long serialVersionUID = 1L;
	
	public DatabaseObjectImpl() {}
	
	public String getPresentationString() {
		if("Hibernate3DatabaseObjectDef".equals(getModelEntity().getName())) {
			return super.getPresentationString();
		}
		String s = getAttributeValue("drop");
		if(s == null || s.length() == 0) return "database object";
		s = s.trim();
		if(s.toLowerCase().startsWith("drop")) {
			s = s.substring(4).trim();
		}
		if(s != null && s.length() > 23) s = s.substring(0, 20) + "...";
		return s;
	}
	
	public String getAttributeValue(String name) {
		if("presentation".equals(name)) {
			return getPresentationString();
		} else {
			return super.getAttributeValue(name);
		}
	}

}
