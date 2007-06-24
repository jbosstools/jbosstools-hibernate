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
package org.jboss.tools.hibernate.xml.model;

import java.util.Properties;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XFilteredTreeConstraint;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;

public class FileHibernateFilteredTreeConstraint implements XFilteredTreeConstraint {

	public void update(XModel model) {		
	}

	public boolean isHidingAllChildren(XModelObject object) {
		return false;
	}
	
	static Properties checkedEntities = new Properties();
	
	boolean checkEntity(XModelEntity entity) {
		String res = checkedEntities.getProperty(entity.getName());
		if(res != null) return "true".equals(res);
		XChild[] cs = entity.getChildren();
		for (int i = 0; i < cs.length; i++) {
			String n = cs[i].getName();
			if(n.startsWith("Hibernate") && n.endsWith("Folder")) {
				checkedEntities.setProperty(n, "true");
				return true;
			}
		}
		checkedEntities.setProperty(entity.getName(), "false");
		return false;		
	}
	
	public boolean isHidingSomeChildren(XModelObject object) {
			///if(true) return false;
		return checkEntity(object.getModelEntity());
	}

	public boolean accepts(XModelObject object) {
		String entity = object.getModelEntity().getName();
//		if("OrmDiagram".equals(entity)) return false;
		if(entity.startsWith("Hibernate") && entity.endsWith("Folder") && object.getChildren().length == 0) return false;
		return true;
	}

}
