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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 27.05.2005
 * 
 */
public class ColumnMergingVisitor extends ColumnBuilderVisitor{

    static class StaleColumnsRemovingVisitor extends ColumnBuilderVisitor {
//        private HibernateMapping mapping;

        private List<SimpleValueMapping> staleColumnsContainers = new ArrayList<SimpleValueMapping>();
        private List<IDatabaseColumn> staleColumns = new ArrayList<IDatabaseColumn>();
        
        /**
         * @param mapping
         */
        public StaleColumnsRemovingVisitor(HibernateMapping mapping) {
            super(mapping);
//            this.mapping = mapping;
        }

        public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
            if (argument instanceof SimpleValueMapping) {
                SimpleValueMapping svm = (SimpleValueMapping) argument;
                
                IDatabaseTable table = column.getOwnerTable();
                if (table == null || table.getColumn(column.getName()) == null)
                {
                    staleColumnsContainers.add(svm);
                    staleColumns.add(column);
                }
            }
            return super.visitDatabaseColumn(column, argument);
        }
        
        public void removeStaleColumns()
        {
            IDatabaseColumn[] columns = (IDatabaseColumn[]) staleColumns.toArray(new IDatabaseColumn[0]);
            SimpleValueMapping[] values = (SimpleValueMapping[]) staleColumnsContainers.toArray(new SimpleValueMapping[0]);
            
            for (int i = 0; i < values.length; i++) {
                SimpleValueMapping mapping = values[i];
                mapping.removeColumn(columns[i]);
                if (mapping.getColumnSpan() == 0)
                {
                    IPersistentFieldMapping pfm = mapping.getFieldMapping();
                    HibernateAutoMappingHelper.removeMapping(pfm);
                }
            }
        }
        
    }
    
    private StaleColumnsRemovingVisitor staleColumnsRemover;
    
    IDatabaseTableColumnSet columnSet = null;
    
    Map<IPersistentField,IDatabaseTableColumnSet> fKeys = new HashMap<IPersistentField,IDatabaseTableColumnSet>();
    Map<IPersistentField,IDatabaseTable> linkTables = new HashMap<IPersistentField,IDatabaseTable>();
    
    public void setPersistentFieldColumnSet(IPersistentField field, IDatabaseTableColumnSet constraint)
    {
        fKeys.put(field,constraint);
    }

    public IDatabaseTableColumnSet getPersistentFieldColumnSet(IPersistentField field)
    {
        return (IDatabaseTableColumnSet) fKeys.get(field);
    }
    
    public void addPersistentFieldLinkTable(IPersistentField field, IDatabaseTable linkTable)
    {
        linkTables.put(field,linkTable);
    }
    
    public ColumnMergingVisitor(HibernateMapping mapping) {
        super(mapping);
        staleColumnsRemover = new StaleColumnsRemovingVisitor(mapping);
    }

    public Object visitPersistentField(IPersistentField field, Object argument) {
        columnSet = (IDatabaseTableColumnSet) fKeys.get(field);
        Object result = super.visitPersistentField(field, argument);
        columnSet = null;
        return result;
    }

    protected void bindReferenceColumnsAndFK(SimpleValueMapping svm, IPersistentClass toClass, IPersistentField field, String prefix) {
        if (columnSet != null)
        {
            if (svm.getColumnSpan() == 0)
            {
                Iterator itr = columnSet.getOrderedColumnIterator();
                while (itr.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn) itr.next();
                    svm.addColumn(column);
                }
                svm.setTable(columnSet.getTable());
                svm.setForeignKeyName(columnSet.getName());
            }
        }
    }

    protected IDatabaseColumn createAndBindColumn(ISimpleValueMapping valueMapping, String namePrefix) {

        IDatabaseColumn column = null;
        
        if (columnSet != null)
        {
            if (valueMapping instanceof SimpleValueMapping)
            {
                SimpleValueMapping svm = (SimpleValueMapping) valueMapping;
                Iterator columns = columnSet.getOrderedColumnIterator();
                while (columns.hasNext())
                {
                    column = (IDatabaseColumn) columns.next();
                    svm.addColumn(column);
                }
                svm.setTable(columnSet.getTable());
            }
        }
        return column;
    }
    
    public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
        if (simple.getColumnSpan() == 0)
        {
            createAndBindColumn(simple,safeCastToString(argument));
            sharedColumnsCopier.process(simple);
        }
        else
        {
            staleColumnsRemover.visitIOrmElementsIterator(simple.getColumnIterator(),simple);
            //delete non-existent columns
            staleColumnsRemover.removeStaleColumns();
        }
        return null;
    }

    public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
        Object result = super.visitPropertyMapping(mapping, argument);
    
        if (mapping.getPersistentValueMapping() instanceof SimpleValueMapping) {
            SimpleValueMapping svm = (SimpleValueMapping) mapping.getPersistentValueMapping();

            if (svm.getColumnSpan() == 0)
            {
                HibernateAutoMappingHelper.removeMapping(mapping);
            }
        }
        
        return result;
    }

}
