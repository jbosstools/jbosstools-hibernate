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
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;

public class ComplexAttrUtil {

	public static XAttribute findComplexAttr(XModelObject folder, String hProperty) {
		XChild[] cs = folder.getModelEntity().getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].getMaxCount() == 1 && cs[i].isRequired()) {
				String n = cs[i].getName();
				XModelEntity e = folder.getModelEntity().getMetaModel().getEntity(n);
				if(e == null) continue;
				XAttribute[] as = e.getAttributes();
				for (int j = 0; j < as.length; j++) {
					if(hProperty.equals(as[j].getProperty(HibConfigComplexPropertyImpl.H_PROPERTY))) {
						return as[j];
					}
				}
			}
		}
		return null;
	}

}
