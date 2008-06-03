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


public class AddSubclassHandler extends DefaultCreateHandler {
	static String[] entities = 
		{"Hibernate3Join", 
		 "Hibernate3Subclass", 
		 "Hibernate3JoinedSubclass", 
		 "Hibernate3UnionSubclass"};
	static int[][] matrix = new int[][]{
		{1,1,0,0},
		{1,1,0,0},
		{0,0,1,0},
		{0,0,0,1}
	};

	int getIndex(String entity) {
		for (int i= 0; i < entities.length; i++) {
			if(entities[i].equals(entity)) return i;
		}
		return -1;
	}

    public boolean isEnabled(XModelObject object) {
    	if(!super.isEnabled(object)) return true;
    	String entity = action.getProperty("entity");
    	if(entity == null) return true;
    	XModelObject[] os = object.getChildren();
    	if(os.length == 0) return true;
    	for (int i = 0; i < os.length; i++) {
    		String entity2 = os[i].getModelEntity().getName();
    		if(!compatible(entity, entity2)) return false;
    	}
    	return true;
    }
    
    boolean compatible(String entity1, String entity2) {
    	int index1 = getIndex(entity1);
    	int index2 = getIndex(entity2);
    	return (index1 < 0 || index2 < 0) ? false : matrix[index1][index2] == 1;
    }

}
