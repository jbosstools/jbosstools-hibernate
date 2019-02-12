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

import org.jboss.tools.hibernate.xml.Messages;

public class DatabaseObjectImpl extends RegularObject2Impl {
	private static final long serialVersionUID = 1L;
	
	public DatabaseObjectImpl() {}
	
	public String getPresentationString() {
		if("Hibernate3DatabaseObjectDef".equals(getModelEntity().getName())) { //$NON-NLS-1$
			return super.getPresentationString();
		}
		String s = getAttributeValue("drop"); //$NON-NLS-1$
		if(s == null || s.length() == 0) return Messages.DatabaseObjectImpl_GenericName;
		s = s.trim();
		if(s.toLowerCase().startsWith("drop")) { //$NON-NLS-1$
			s = s.substring(4).trim();
		}
		if(s != null && s.length() > 23) s = s.substring(0, 20) + "..."; //$NON-NLS-1$
		return s;
	}
	
	public String getAttributeValue(String name) {
		if("presentation".equals(name)) { //$NON-NLS-1$
			return getPresentationString();
		} else {
			return super.getAttributeValue(name);
		}
	}

}
