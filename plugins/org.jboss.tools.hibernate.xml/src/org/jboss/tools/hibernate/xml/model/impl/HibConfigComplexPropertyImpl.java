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
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;

public class HibConfigComplexPropertyImpl extends RegularObject2Impl {
	private static final long serialVersionUID = 1L;
	public static String H_PROPERTY = "h_property"; //$NON-NLS-1$
	public static String ENT_PROPERTY = "HibConfig3Property"; //$NON-NLS-1$
	static String ATTR_VALUE = "value"; //$NON-NLS-1$
	boolean lock = false;

	public HibConfigComplexPropertyImpl() {
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) throws XModelException {
		if(getParent() == null) return;
		XAttribute a = getModelEntity().getAttribute(name);
		if(a == null) return;
		String propertyName = a.getProperty(H_PROPERTY);
		if(propertyName == null || propertyName.length() == 0) return;
		if(lock) return;
		lock = true;
		try {
		XModelObject o = getParent().getChildByPath(propertyName);
		if(o == null) {
			if(newValue != null && newValue.length() > 0) {
				XModelObject c = getModel().createModelObject(ENT_PROPERTY, null);
				c.setAttributeValue(ATTR_VALUE, newValue);
				c.setAttributeValue(XMLStoreConstants.ATTR_NAME, propertyName);
				DefaultCreateHandler.addCreatedObject(getParent(), c, -1);
			}
		} else {
			if(newValue == null || newValue.length() == 0) {
				DefaultRemoveHandler.removeFromParent(o);
			} else {
				getModel().editObjectAttribute(o, ATTR_VALUE, newValue);
			}
		}
		} finally {
			lock = false;
		}
	}
}
