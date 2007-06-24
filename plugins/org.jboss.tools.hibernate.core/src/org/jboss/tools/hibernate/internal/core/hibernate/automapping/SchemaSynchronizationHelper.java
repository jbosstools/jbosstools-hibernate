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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 01.08.2005
 * 
 */
public class SchemaSynchronizationHelper {

    // added by Nick 29.07.2005
    private Map<ColumnTrack,IDatabaseTableForeignKey> columnTracks = new HashMap<ColumnTrack,IDatabaseTableForeignKey>();
    // by Nick

    public void addColumnSetCorrespondence(IDatabaseTableColumnSet columns, IDatabaseTableForeignKey fKey) {
        columnTracks.put(new ColumnTrack(columns),fKey);
    }
    
    public boolean isPairedKey(IHibernateKeyMapping mapping) {
        ColumnTrack track = new ColumnTrack(new ColumnSet(mapping.getColumnIterator()));
        return (columnTracks.containsKey(track));        
    }
    
    public IDatabaseTableForeignKey processFKeyMapping(IHibernateKeyMapping mapping, IPersistentClassMapping referencedClass) {
        ColumnSet staleColumns = new ColumnSet(mapping.getColumnIterator());

        if(mapping.getTable() == null || referencedClass == null)
            return null;
        
        IDatabaseTableForeignKey fk = mapping.getTable().getForeignKey(mapping.getForeignKeyName());
        if(fk == null || fk.getReferencedTable() == null || 
                fk.getReferencedTable() != referencedClass.getDatabaseTable())
        {
            fk = searchForeignKey(mapping, referencedClass.getDatabaseTable());
            if(fk == null)
            {// the fk was not found.
                HibernateAutoMapping.clearLegacyColumnMapping(mapping,null,true);
                return null;
            }
            mapping.setForeignKeyName(fk.getName());
        }

        this.addColumnSetCorrespondence(staleColumns,fk);
        
        ColumnSet columnSet = new ColumnSet(mapping.getColumnIterator());
        
        if (columnSet.equals(fk))
            return fk;
        
        HibernateAutoMapping.clearLegacyColumnMapping(mapping,null,false);
        
        Iterator fkcolumns = fk.getColumnIterator();

        while(fkcolumns.hasNext())
        {
            IDatabaseColumn clm = (IDatabaseColumn)fkcolumns.next();
            if (mapping instanceof ISimpleValueMapping) {
                ((ISimpleValueMapping) mapping).addColumn(clm);
            }
        }

        return fk;
    }
    
    /*
     * Columns search for the foreign key.  
     * return foreign key or null.
     */
    private IDatabaseTableForeignKey searchForeignKey(IHibernateKeyMapping mapping, IDatabaseTable table)
    {
        IDatabaseTableForeignKey    result          = null;

        ColumnTrack track = new ColumnTrack(new ColumnSet(mapping.getColumnIterator()));
        if (columnTracks.containsKey(track)) {
            result = (IDatabaseTableForeignKey) columnTracks.get(track);
        } else {
            Map<Double,IDatabaseTableForeignKey> suitablefkeys = new HashMap<Double,IDatabaseTableForeignKey>();
            Iterator fkeys = mapping.getTable().getForeignKeyIterator();
            while(fkeys.hasNext())
            {
                IDatabaseTableForeignKey fk = (IDatabaseTableForeignKey)fkeys.next();
                if(fk.getReferencedTable() != null && fk.getReferencedTable() == table)
                {
                    collectSuitableFKeyColumns(fk, mapping, suitablefkeys);
                }
            }
            
            if(suitablefkeys.size() > 0)
            {
                Object min = Collections.min(suitablefkeys.keySet()/*,listSizeComparator()*/);
                result = (IDatabaseTableForeignKey)suitablefkeys.get(min);
            }
        }
        
        return result;
    }
    
    static public <T> Iterator<T> getIteratorCopy(Iterator<T> itr) {
        ArrayList<T> elements = new ArrayList<T>();
        while(itr.hasNext()) {       
            elements.add(itr.next());        
        }
        
        return elements.iterator();
        
    }

    /*
     * fk - processing foreign key
     * columns - mappping columns
     */
    private static void collectSuitableFKeyColumns(IDatabaseTableForeignKey fk, IHibernateKeyMapping mapping, Map<Double,IDatabaseTableForeignKey> suitablefkeys)
    {
        Iterator fkcolumns= fk.getColumnIterator();
        
        boolean freeKey = true;
        
        int cols_0 = 0;
        
        while(fkcolumns.hasNext() && freeKey) {
            IDatabaseColumn fkcolumn    = (IDatabaseColumn )fkcolumns.next();
            freeKey = (fkcolumn.getPersistentValueMapping() == null || fkcolumn.getPersistentValueMapping() == mapping);

            if (fkcolumn.getPersistentValueMapping() == null)
            {
                cols_0++;
            }
            
//          IDatabaseColumn mapcolumn   = (IDatabaseColumn)columns.next();
//          if(mapcolumn.getName().equals(fkcolumn.getName()))
//          {       suitablecolumns.add(fkcolumn);          }
        }

        double metrics = 0;
        
        metrics = Math.abs( fk.getColumnSpan() - mapping.getColumnSpan() ) + cols_0;
        suitablefkeys.put(new Double(metrics),fk);      
    }

}

//added by Nick 29.07.2005
class ColumnTrack {
    private ArrayList<String> columnNames = new ArrayList<String>();
    private IDatabaseTable table;
    
    ColumnTrack(IDatabaseTableColumnSet columnSet) {
        Iterator<IDatabaseColumn> columns = columnSet.getOrderedColumnIterator();
        while (columns.hasNext()) {
            IDatabaseColumn column = columns.next();
            columnNames.add(column.getName());
        }        
        this.table = columnSet.getTable();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ColumnTrack) {
            ColumnTrack colTrack = (ColumnTrack) obj;
            boolean equals = (colTrack.table == this.table) && columnNames.equals(colTrack.columnNames);
            return equals;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        
        if (table != null) {
            hash = 31*table.hashCode() + columnNames.hashCode();
        } else {
            hash = columnNames.hashCode();
        }
        
        return hash;
    }
    
}
