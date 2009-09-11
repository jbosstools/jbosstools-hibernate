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
package org.jboss.tools.hibernate.xml.model.handlers;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;
import org.jboss.tools.hibernate.xml.model.impl.ComplexAttrUtil;

public class DeletePropertyHandler extends DefaultRemoveHandler {

	public DeletePropertyHandler() {}

    public void executeHandler(XModelObject object, java.util.Properties p) throws XModelException {
    	String name = object.getAttributeValue(XMLStoreConstants.ATTR_NAME);
    	XAttribute attr = ComplexAttrUtil.findComplexAttr(object.getParent(), name);
    	if(attr != null) {
			XModelEntity entity = attr.getModelEntity();
			XModelObject c = object.getParent().getChildByPath(entity.getAttribute(XMLStoreConstants.ATTR_NAME).getDefaultValue());
			if(c != null) {
				c.getModel().editObjectAttribute(c, attr.getName(), "");
			}
    	} else {
    		super.executeHandler(object, p);
    	}
    }

}
