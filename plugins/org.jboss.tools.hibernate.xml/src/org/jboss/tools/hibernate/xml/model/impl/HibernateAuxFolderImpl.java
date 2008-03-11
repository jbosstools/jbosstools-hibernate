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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.common.model.impl.XModelImpl;

public class HibernateAuxFolderImpl extends OrderedObjectImpl {
    private static final long serialVersionUID = 5190269651082395100L;

	public boolean addChild(XModelObject object) {
		boolean b = super.addChild(object);
		if(b && isActive() && children.size() == 1) {
			notifyParent();
		}
		return b;
	}

	public void removeChild(XModelObject child) {
		super.removeChild(child);
		if(isActive() && children.size() == 0) {
			notifyParent();
		}
	}
	
	private void notifyParent() {
		((XModelImpl)getModel()).fireStructureChanged(getParent());
	}

}
