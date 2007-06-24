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

import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;

public class FolderOwnerAdopt implements XAdoptManager {

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		if(isAdoptableGrandChild(target, object)) return true;
		return false;
	}

	public void adopt(XModelObject target, XModelObject object, Properties p) {
		if(isAdoptableGrandChild(target, object)) {
			XModelObject folder = getFolderForChild(target, object);
			if(folder == null) return;
			XActionInvoker.invoke("CopyActions.Paste", folder, null);
		}
	}
	
	public boolean isAdoptableGrandChild(XModelObject target, XModelObject object) {
		return getFolderForChild(target, object) != null;
	}
	
	public XModelObject getFolderForChild(XModelObject parent, XModelObject child) {
		String entity = child.getModelEntity().getName();
		if(parent.getModelEntity().getChild(entity) != null) return null;
		XChild[] cs = parent.getModelEntity().getChildren();
		for (int i = 0; i < cs.length; i++) {
			XModelEntity ent = parent.getModel().getMetaData().getEntity(cs[i].getName());
			if(ent == null || ent.getChild(entity) == null) continue;
			XModelObject[] os = parent.getChildren(ent.getName());
			if(os.length > 0) return os[0];			
		}
		return null;
	}

}
