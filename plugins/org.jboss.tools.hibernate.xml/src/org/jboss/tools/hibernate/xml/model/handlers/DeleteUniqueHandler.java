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

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.undo.XTransactionUndo;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class DeleteUniqueHandler extends DefaultRemoveHandler {

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if(!isEnabled(object)) return;
		String transactionName = "delete " + DefaultCreateHandler.title(object, false);
		executeInTransaction(object, p, transactionName, XTransactionUndo.REMOVE);
	}

	protected void transaction(XModelObject object, Properties p) throws Exception {
		XModelObject parent = object.getParent();
		removeFromParent(object);
		String entity = action.getProperty("entity");
		if(entity == null) entity = object.getModelEntity().getName();
		XModelObject c = XModelObjectLoaderUtil.createValidObject(object.getModel(), entity);
		DefaultCreateHandler.addCreatedObject(parent, c, p);
	}

}
