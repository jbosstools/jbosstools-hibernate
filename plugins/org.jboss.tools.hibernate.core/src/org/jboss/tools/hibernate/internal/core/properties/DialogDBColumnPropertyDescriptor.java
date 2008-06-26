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
package org.jboss.tools.hibernate.internal.core.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Sep 25, 2005
 */
public class DialogDBColumnPropertyDescriptor extends
AutoChangeblePropertyDescriptor {

	protected IDatabaseTable db;	
	public DialogDBColumnPropertyDescriptor(Object id, String displayName,String[] dependentProperties,IDatabaseTable db) {
		super(id, displayName,dependentProperties);
		this.db=db;
	}

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new DialogDBColumnCellEditor(parent,db);
        return editor; 
    }

	public void autochangeValues(Object changedValue) {
		if((db!=null)&&((changedValue instanceof IDatabaseTable)||(changedValue instanceof String))){
			if (changedValue instanceof String) db=db.getSchema().getProjectMapping().findTable(StringUtils.hibernateEscapeName((String)changedValue));//added 27/9
			else
			db=(IDatabaseTable)changedValue;	
		}		
	}

	
}
