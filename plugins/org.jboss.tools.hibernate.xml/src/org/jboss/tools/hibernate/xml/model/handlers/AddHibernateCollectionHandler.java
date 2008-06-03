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

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;


public class AddHibernateCollectionHandler extends DefaultCreateHandler {

	protected XModelObject modifyCreatedObject(XModelObject o) {
		XModelObject c = o.getChildByPath("index");
		if(c == null) {
			c = o.getModel().createModelObject("Hibernate3Index", null);
			o.addChild(c);
		}
		c = o.getChildByPath("element");
		if(c == null) {
			c = o.getModel().createModelObject("Hibernate3Element", null);
			o.addChild(c);
		}
		return o;
	}


}
