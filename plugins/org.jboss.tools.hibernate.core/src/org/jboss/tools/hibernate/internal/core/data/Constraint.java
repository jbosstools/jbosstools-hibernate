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
import java.util.List;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.internal.core.DatabaseTableColumnSetHelper;

/**
 * @author alex
 *
 * A relational constraint.
 */
public class Constraint extends DataObject implements IDatabaseConstraint {
	private static final long serialVersionUID = 1L;
	private final List<IDatabaseColumn> columns = new ArrayList<IDatabaseColumn>();
	private IDatabaseTable table;
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseConstraint#getColumnIterator()
	 */
	public Iterator<IDatabaseColumn> getColumnIterator() {
		return columns.iterator();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseConstraint#getColumnSpan()
	 */
	public int getColumnSpan() {
		return columns.size();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseConstraint#getTable()
	 */
	public IDatabaseTable getTable() {
		return table;
	}

	/**
	 * @param table The table to set.
	 */
	public void setTable(IDatabaseTable table) {
		this.table = table;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitDatabaseConstraint(this,argument);
	}
	public void addColumn(IDatabaseColumn column) {
		if ( !columns.contains(column) ) columns.add(column);
		/*column.setValue(this);
		column.setTypeIndex( columns.size()-1 );*/
	}

	public boolean containsColumn(String columnName) { //by Nick 22.04.2005
		boolean found = false;
		Iterator itr = columns.iterator();
		if (itr != null)
			while (itr.hasNext() && !found)
			{
				IDatabaseColumn column = (IDatabaseColumn)itr.next();
				if (column.getName().equals(columnName))
					found = true;
			}
		return found;
	}
	public IDatabaseColumn removeColumn(String columnName) {
		for(int i=0;i<columns.size();++i){
			IDatabaseColumn column = (IDatabaseColumn)columns.get(i);
			if (column.getName().equals(columnName)) {
				return (IDatabaseColumn)columns.remove(i);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseConstraint#clear()
	 */
	public void clear() {
		columns.clear();
	}
	
    // added by Nick 16.06.2005
	public boolean equals(Object obj)
    {
	    if (obj instanceof IDatabaseConstraint)
        {
	        if (obj == null)
                return false;
            
            if (obj.getClass() == this.getClass())
            {
                IDatabaseConstraint objConstraint = (IDatabaseConstraint) obj;
                boolean sameTable = false;
                boolean sameName = false;
                
                if (objConstraint.getTable() != null &&
                        objConstraint.getTable().equals(this.getTable()))
                {
                    sameTable = true;
                }
                
                if (sameTable)
                {
                    if (objConstraint.getName() != null &&
                            objConstraint.getName().equals(this.getName()))
                        sameName = true;
                }
                
                return sameName && sameTable;
            }
            else
                return false;
        }
        else
            return false;
    }
    // by Nick

    // added by Nick 04.07.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseConstraint#isUnique()
     */
    public boolean isUnique() {
//        boolean isUnique = true;
//        Iterator itr = getColumnIterator();
//        while (isUnique && itr.hasNext())
//        {
//            IDatabaseColumn column = (IDatabaseColumn) itr.next();
//            if (!column.isUnique())
//                isUnique = false;
//        }
//        
//        return isUnique;

        // changed by Nick 12.09.2005
        if (getColumnSpan() == 1)
        {
            IDatabaseColumn column = ((IDatabaseColumn) columns.get(0));
            if (column.isUnique())
                return true;
        }

        Iterator idxItr = getTable().getIndexIterator();
        
        while (idxItr.hasNext())
        {
            Index idx = (Index) idxItr.next();
            if (this.equals(idx) && idx.isUnique())
            {
                return true;
            }
        }
        
        Iterator ukItr = getTable().getUniqueKeyIterator();
        while (ukItr.hasNext())
        {
            IDatabaseTableColumnSet uk = (IDatabaseTableColumnSet) idxItr.next();
            if (this.equals(uk))
            {
                return true;
            }
        }
        
        return false;
        //by Nick
    }
    // by Nick

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
    
    // added by Nick 28.07.2005
    public int getColumnIndex(IDatabaseColumn column)
    {
        return columns.indexOf(column);
    }
    // by Nick

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#getOrderedColumnIterator()
     */
    public Iterator<IDatabaseColumn> getOrderedColumnIterator() {
        return getColumnIterator();
    }

    // added by Nick 09.09.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTableColumnSet#equals(org.jboss.tools.hibernate.core.IDatabaseTableColumnSet)
     */
    public boolean equals(IDatabaseTableColumnSet otherSet) {
        return DatabaseTableColumnSetHelper.equals(this,otherSet);
    }
    // by Nick
}
