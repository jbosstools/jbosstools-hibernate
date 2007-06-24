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

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.meta.action.impl.handlers.PasteHandler;
import org.jboss.tools.common.model.XModelBuffer;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ServiceDialog;

public class PasteUniqueHandler extends PasteHandler {

	protected void execute(XModelObject parent, int sourceIndex, boolean isDrop, Properties p) throws Exception {
		XModelBuffer buffer = parent.getModel().getModelBuffer();
		boolean isParent = isParent(parent, buffer.copy(sourceIndex));
		if(isParent) {
			if(!prepaste(parent, sourceIndex)) return;
			if(!isDrop)
			  paste(parent, sourceIndex, p);
			else 
			  pasteOnDrop(parent, sourceIndex, p);
		} else {
			drop(parent, buffer.source(sourceIndex), p);
		}
	}
	
	private boolean prepaste(XModelObject parent, int sourceIndex) {
		XModelObject source = parent.getModel().getModelBuffer().source(sourceIndex);
		String entity = source.getModelEntity().getName();
		XChild c = parent.getModelEntity().getChild(entity);
		if(c == null || c.getMaxCount() > 1) return true;  
		XModelObject co = parent.getChildByPath(source.getPathPart());
		if(co == null) return true;
		String message = "Replace existing element ";
		String n = "<" + co.getModelEntity().getXMLSubPath() + ">";
		message += n;
		ServiceDialog d = parent.getModel().getService();
		int q = d.showDialog("Paste", message, new String[]{"OK", "Cancel"}, null, ServiceDialog.QUESTION);
		if(q != 0) return false;
		DefaultRemoveHandler.removeFromParent(co);
		return true;
	}

}
