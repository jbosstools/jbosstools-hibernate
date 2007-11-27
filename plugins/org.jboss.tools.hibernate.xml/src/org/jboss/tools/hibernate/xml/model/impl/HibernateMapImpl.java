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

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;

public class HibernateMapImpl extends OrderedObject2Impl {
	private static final long serialVersionUID = 1L;
	
	public String getAttributeValue(String name) {
		if("index".equals(name)) {
			XModelObject c = getChildByPath("index");
			return c == null ? "index" : c.getAttributeValue("element type");
		} else if("element".equals(name)) {
			XModelObject c = getChildByPath("element");
			return c == null ? "element" : c.getAttributeValue("element type");
		}
		return super.getAttributeValue(name);
	}

	public String setAttributeValue(String name, String value) {
		if("index".equals(name)) {
			return value;
		} else if("element".equals(name)) {
			return value;
		}
		return super.setAttributeValue(name, value);
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) {
		if(newValue == null || newValue.equals(oldValue)) return;
		if("index".equals(name)) {
			String actionName = 
				"index".equals(newValue) ? "AddIndex" :
				"list-index".equals(newValue) ? "AddListIndex" :					
				"map-key".equals(newValue) ? "AddMapKey" :
				"composite-map-key".equals(newValue) ? "AddCompositeMapKey" :
				"map-key-many-to-many".equals(newValue) ? "AddMapKeyManyToMany" :
				"composite-index".equals(newValue) ? "AddCompositeIndex" :
				"index-many-to-any".equals(newValue) ? "AddIndexManyToAny" :
				"index-many-to-many".equals(newValue) ? "AddIndexManyToMany" :
				null;
			String actionPath = (actionName == null) ? null : "CreateActions.IndexActions." + actionName;
			if(actionPath != null && XActionInvoker.getAction(actionPath, this) != null) {
				XActionInvoker.invoke(actionPath, this, new Properties());
			}
		} else if("element".equals(name)) {
			String actionName = 
				"element".equals(newValue) ? "AddElement" :
				"one-to-many".equals(newValue) ? "AddOneToMany" :
				"many-to-many".equals(newValue) ? "AddManyToMany" :
				"composite-element".equals(newValue) ? "AddCompositeElement" :
				"many-to-any".equals(newValue) ? "AddManyToAny" :
				null;
			String actionPath = (actionName == null) ? null : "CreateActions." + actionName;
			if(actionPath != null && XActionInvoker.getAction(actionPath, this) != null) {
				XActionInvoker.invoke(actionPath, this, new Properties());
			}
		}
	}

}
