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
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;

public class HibernateMapImpl extends OrderedObject2Impl {
	private static final long serialVersionUID = 1L;
	
	public String getAttributeValue(String name) {
		if("index".equals(name)) { //$NON-NLS-1$
			XModelObject c = getChildByPath("index"); //$NON-NLS-1$
			return c == null ? "index" : c.getAttributeValue("element type"); //$NON-NLS-1$ //$NON-NLS-2$
		} else if("element".equals(name)) { //$NON-NLS-1$
			XModelObject c = getChildByPath("element"); //$NON-NLS-1$
			return c == null ? "element" : c.getAttributeValue("element type"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.getAttributeValue(name);
	}

	public String setAttributeValue(String name, String value) {
		if("index".equals(name)) { //$NON-NLS-1$
			return value;
		} else if("element".equals(name)) { //$NON-NLS-1$
			return value;
		}
		return super.setAttributeValue(name, value);
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) throws XModelException {
		if(newValue == null || newValue.equals(oldValue)) return;
		if("index".equals(name)) { //$NON-NLS-1$
			String actionName = 
				"index".equals(newValue) ? "AddIndex" : //$NON-NLS-1$ //$NON-NLS-2$
				"list-index".equals(newValue) ? "AddListIndex" :					 //$NON-NLS-1$ //$NON-NLS-2$
				"map-key".equals(newValue) ? "AddMapKey" : //$NON-NLS-1$ //$NON-NLS-2$
				"composite-map-key".equals(newValue) ? "AddCompositeMapKey" : //$NON-NLS-1$ //$NON-NLS-2$
				"map-key-many-to-many".equals(newValue) ? "AddMapKeyManyToMany" : //$NON-NLS-1$ //$NON-NLS-2$
				"composite-index".equals(newValue) ? "AddCompositeIndex" : //$NON-NLS-1$ //$NON-NLS-2$
				"index-many-to-any".equals(newValue) ? "AddIndexManyToAny" : //$NON-NLS-1$ //$NON-NLS-2$
				"index-many-to-many".equals(newValue) ? "AddIndexManyToMany" : //$NON-NLS-1$ //$NON-NLS-2$
				null;
			String actionPath = (actionName == null) ? null : "CreateActions.IndexActions." + actionName; //$NON-NLS-1$
			if(actionPath != null && XActionInvoker.getAction(actionPath, this) != null) {
				XActionInvoker.invoke(actionPath, this, new Properties());
			}
		} else if("element".equals(name)) { //$NON-NLS-1$
			String actionName = 
				"element".equals(newValue) ? "AddElement" : //$NON-NLS-1$ //$NON-NLS-2$
				"one-to-many".equals(newValue) ? "AddOneToMany" : //$NON-NLS-1$ //$NON-NLS-2$
				"many-to-many".equals(newValue) ? "AddManyToMany" : //$NON-NLS-1$ //$NON-NLS-2$
				"composite-element".equals(newValue) ? "AddCompositeElement" : //$NON-NLS-1$ //$NON-NLS-2$
				"many-to-any".equals(newValue) ? "AddManyToAny" : //$NON-NLS-1$ //$NON-NLS-2$
				null;
			String actionPath = (actionName == null) ? null : "CreateActions." + actionName; //$NON-NLS-1$
			if(actionPath != null && XActionInvoker.getAction(actionPath, this) != null) {
				XActionInvoker.invoke(actionPath, this, new Properties());
			}
		}
	}

}
