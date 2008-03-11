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
package org.jboss.tools.hibernate.internal.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 22.07.2005
 * 
 */
public class DatabaseTableColumnSetHelper {
    public static boolean containsMappedColumns(Iterator columns)
    {
        boolean result = false;
        
        if (columns != null)
        while (columns.hasNext() && !result)
        {
            Object obj = columns.next();
            if (obj instanceof IDatabaseColumn) {
                IDatabaseColumn column = (IDatabaseColumn) obj;
                
                if (column.getPersistentValueMapping() != null)
                    result = true;
            }
        }
    
        return result;
    }
    
    private static Set<IDatabaseColumn> getColumnSet(IDatabaseTableColumnSet set) {
        Set<IDatabaseColumn> result = new HashSet<IDatabaseColumn>();
        Iterator<IDatabaseColumn> columns = set.getColumnIterator();
        while (columns.hasNext()) {
            result.add(columns.next());
        }

        return result;
    }
    
    public static boolean intersects(IDatabaseTableColumnSet set1, IDatabaseTableColumnSet set2)
    {
        boolean areIntersecting = false;
        if (set1 != null && set2 != null && (set1.getTable() == set2.getTable()))
        {
            Set<IDatabaseColumn> c_set1, c_set2;
            c_set1 = getColumnSet(set1);
            c_set2 = getColumnSet(set2);
            c_set1.retainAll(c_set2);
            areIntersecting = (c_set1.size() >= 1);
        }
        return areIntersecting;
    }


    public static boolean includes(IDatabaseTableColumnSet leadSet, IDatabaseTableColumnSet otherSet) {
        boolean includes = false;
        if (leadSet != null && otherSet != null && (leadSet.getTable() == otherSet.getTable())) {
            Set<IDatabaseColumn> c_set1, c_set2;
            c_set1 = getColumnSet(leadSet);
            c_set2 = getColumnSet(otherSet);
            includes = c_set1.containsAll(c_set2);
        }
        return includes;
    }
    
    public static boolean equals(IDatabaseTableColumnSet set1, IDatabaseTableColumnSet set2)
    {
        if (set1.getColumnSpan() != set2.getColumnSpan())
        {
            return false;
        }
        
        Iterator set1Columns = set1.getOrderedColumnIterator();
        Iterator set2Columns = set2.getOrderedColumnIterator();
        
        while (set1Columns.hasNext() && set2Columns.hasNext())
        {
            if (!set1Columns.next().equals(set2Columns.next()))
                return false;
        }
        
        return true;
   }
    
}
