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
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;

public class HibConfigSimplePropertyImpl extends RegularObject2Impl {
	private static final long serialVersionUID = 1L;

	public HibConfigSimplePropertyImpl() {
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) throws XModelException {
		if(getParent() == null) return;
		if(name.equals(XMLStoreConstants.ATTR_VALUE)) {
			XAttribute attr = ComplexAttrUtil.findComplexAttr(getParent(), getAttributeValue(XMLStoreConstants.ATTR_NAME));
			if(attr != null) {
				XModelEntity entity = attr.getModelEntity();
				XModelObject c = getParent().getChildByPath(entity.getAttribute(XMLStoreConstants.ATTR_NAME).getDefaultValue());
				if(c != null) {
					c.setAttributeValue(attr.getName(), newValue);
				}
			}
		} else if(name.equals(XMLStoreConstants.ATTR_NAME)) {
			XAttribute attr = ComplexAttrUtil.findComplexAttr(getParent(), oldValue);
			if(attr != null) {
				XModelEntity entity = attr.getModelEntity();
				XModelObject c = getParent().getChildByPath(entity.getAttribute(XMLStoreConstants.ATTR_NAME).getDefaultValue());
				if(c != null) {
					c.setAttributeValue(attr.getName(), "");
				}
			}
			attr = ComplexAttrUtil.findComplexAttr(getParent(), newValue);
			if(attr != null) {
				XModelEntity entity = attr.getModelEntity();
				XModelObject c = getParent().getChildByPath(entity.getAttribute(XMLStoreConstants.ATTR_NAME).getDefaultValue());
				if(c != null) {
					c.setAttributeValue(attr.getName(), newValue);
				}
			}
		}
	}
}
