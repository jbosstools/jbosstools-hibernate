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
package org.jboss.tools.hibernate.internal.core.data;


import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;

/**
 * @author alex
 *
 * A primary key constraint
 */
public class PrimaryKey extends Constraint implements IDatabaseTablePrimaryKey {
	private static final long serialVersionUID = 1L;

	// added by Nick 21.09.2005
    public IDatabaseColumn removeColumn(String columnName) {
        IDatabaseColumn column = super.removeColumn(columnName);
    
        if (getColumnSpan() == 0 && getTable() != null)
        {
            getTable().setPrimaryKey(null);
        }
        return column;
    }
    // by Nick
    
    public void setTable(IDatabaseTable table) {
		super.setTable(table);
		if(table!=null) setName("pk_"+table.getName());
	}
}
