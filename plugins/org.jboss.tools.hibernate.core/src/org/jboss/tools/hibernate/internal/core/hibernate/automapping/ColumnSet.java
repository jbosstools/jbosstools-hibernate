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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.internal.core.DatabaseTableColumnSetHelper;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 14.07.2005
 * 
 */
public class ColumnSet implements IDatabaseTableColumnSet {

    private List<IDatabaseColumn> columns = new ArrayList<IDatabaseColumn>();
    
    public ColumnSet(Iterator columnIterator) {
        if (columnIterator != null) {
            while (columnIterator.hasNext()) {
                Object o = columnIterator.next();
                if (o instanceof IDatabaseColumn) {
                    IDatabaseColumn column = (IDatabaseColumn) o;
                    columns.add(column);
                }
            }
        }
    }
    
    public ColumnSet(IDatabaseColumn column)
    {
        super();
        columns.add(column);
    }
    
    /*
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getColumnIterator()
     */
    public Iterator<IDatabaseColumn> getColumnIterator() {
        return columns.iterator();
    }

    /*
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getColumnSpan()
     */
    public int getColumnSpan() {
        return columns.size();
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getTable()
     */
    public IDatabaseTable getTable() {
        if (getColumnSpan() == 0)
            return null;
        
        return ((IDatabaseColumn)columns.get(0)).getOwnerTable();
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getName()
     */
    public String getName() {
        if (getColumnSpan() == 0)
            return null;
        
        return ((IDatabaseColumn)columns.get(0)).getName();
    }

    // added by Nick 15.07.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#includes(org.jboss.tools.hibernate.core.IDatabaseTableColumnSet)
     */
    public boolean includes(IDatabaseTableColumnSet otherSet) {
        return DatabaseTableColumnSetHelper.includes(this,otherSet);
    }
    // by Nick

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#containsMappedColumns()
     */
    public boolean containsMappedColumns() {
        return DatabaseTableColumnSetHelper.containsMappedColumns(getColumnIterator());
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#intersects(org.jboss.tools.hibernate.core.IDatabaseTableColumnSet)
     */
    public boolean intersects(IDatabaseTableColumnSet otherSet) {
        return DatabaseTableColumnSetHelper.intersects(this,otherSet);
    }

    /*
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getOrderedColumnIterator()
     */
    public Iterator<IDatabaseColumn> getOrderedColumnIterator() {
        return getColumnIterator();
    }

    /*
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#equals(org.jboss.tools.hibernate.core.IDatabaseTableColumnSet)
     */
    public boolean equals(IDatabaseTableColumnSet otherSet) {
        return DatabaseTableColumnSetHelper.equals(this,otherSet);
    }
    
}
