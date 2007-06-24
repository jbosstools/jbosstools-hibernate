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

import java.util.ArrayList;
import java.util.Iterator;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;

/**
 * @author alex
 *
 * A foreign key constraint
 */
public class ForeignKey extends Constraint implements IDatabaseTableForeignKey {
	private static final long serialVersionUID = 1L;
	private IDatabaseTable referencedTable;
	private String referencedEntityName;
	private boolean cascadeDeleteEnabled;    
	
    // added by Nick 28.07.2005
	// contains IDatabaseColumn instances
    private ArrayList<IDatabaseColumn> columnsPKOrder = new ArrayList<IDatabaseColumn>();
    // by Nick
	
	/*
	 * @see org.jboss.tools.hibernate.core.IDatabaseTableForeignKey#getReferencedTable()
	 */
	public IDatabaseTable getReferencedTable() {
		return referencedTable;
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IDatabaseTableForeignKey#isCascadeDeleteEnabled()
	 */
	public boolean isCascadeDeleteEnabled() {
		return cascadeDeleteEnabled;
	}

	/**
	 * @return Returns the referencedEntityName.
	 */
	public String getReferencedEntityName() {
		return referencedEntityName;
	}

	/**
	 * @param referencedEntityName The referencedEntityName to set.
	 */
	public void setReferencedEntityName(String referencedEntityName) {
		this.referencedEntityName = referencedEntityName;
	}

	/**
	 * @param cascadeDeleteEnabled The cascadeDeleteEnabled to set.
	 */
	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
	}

	/**
	 * @param referencedTable The referencedTable to set.
	 */
	public void setReferencedTable(IDatabaseTable referencedTable) {
		this.referencedTable = referencedTable;
	}

    public void clear() {
        columnsPKOrder.clear();
        super.clear();
    }

    private void growOrderList(int toSize) {
        if (columnsPKOrder.size() < toSize) {
            for (int i = 0 ; (toSize - columnsPKOrder.size()) != 0 ; i++) {
                columnsPKOrder.add(null);
            }
        }
    }
    
    void setColumnPKOrder(IDatabaseColumn column, int index) {
        growOrderList(index+1);
        columnsPKOrder.set(index,column);
    }
    
    public Iterator<IDatabaseColumn> getOrderedColumnIterator() {
        Iterator columnItr = getColumnIterator();
        
        boolean allColumnsHaveOrder = (columnsPKOrder.size() == getColumnSpan());
        
        while (columnItr.hasNext() && allColumnsHaveOrder) {
            Object o = columnItr.next();
            if (!columnsPKOrder.contains(o))
                allColumnsHaveOrder = false;
        }
        
        if (allColumnsHaveOrder) {
            return columnsPKOrder.iterator();
        } else {
            return getColumnIterator();
        }
    }

}
