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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 30.05.2005
 * 
 */
public class SharedColumnsCopier {

    private Map<IPersistentValueMapping,IPersistentValueMapping> values = new HashMap<IPersistentValueMapping,IPersistentValueMapping>();
    private Map<IPersistentValueMapping,IPostCopyOperation> postCopies = new HashMap<IPersistentValueMapping,IPostCopyOperation>();

    interface IPostCopyOperation {
        void process(SimpleValueMapping svm);
    }
    
    static class BindToPKPostCopy implements IPostCopyOperation {
        IDatabaseTablePrimaryKey pKey;
        
        BindToPKPostCopy(IDatabaseTablePrimaryKey pKey) {
            this.pKey = pKey;
        }

        public void process(SimpleValueMapping svm) {
            if (pKey == null || svm == null)
                return ;
            
            Iterator itr = svm.getColumnIterator();
            while (itr.hasNext()) {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                pKey.addColumn(column);
            }
        }
        
    }
    
    public void addSharingValueMappings(IPersistentValueMapping value, IPersistentValueMapping otherValue)
    {
        if (value != null && otherValue != null &&
                value instanceof SimpleValueMapping && otherValue instanceof SimpleValueMapping)
        {
            values.put(value,otherValue);
            values.put(otherValue,value);
        }
    }

    //can be added to run after sharing copy was done
    //binds many-to-many columns to PK
    public void addPostCopyOperation(IPersistentValueMapping value, IPostCopyOperation operation)
    {
        postCopies.put(value,operation);
    }
    
    public void process(IPersistentValueMapping value) {
        if (values.containsKey(value) && value.getColumnSpan() != 0) {
            SimpleValueMapping source = (SimpleValueMapping)value;
            SimpleValueMapping destination = (SimpleValueMapping)values.get(source);

            values.remove(source);
            values.remove(destination);
            
            Iterator columns = source.getColumnIterator();
            if (columns != null && destination != null) {
                while (columns.hasNext()) {
                    IDatabaseColumn column = (IDatabaseColumn)columns.next();
                    IPersistentValueMapping columnValue = column.getPersistentValueMapping();
                    destination.addColumn(column);
                    column.setPersistentValueMapping(columnValue);
                }
            }
            destination.setForeignKeyName(source.getForeignKeyName());
        
            IPostCopyOperation operation = (IPostCopyOperation) postCopies.get(destination);
            if (operation != null)
                operation.process(destination);
        }
    }

}
