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

import java.util.Iterator;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;

 /**
  *
  */

public class SchemaSynchronizationVisitor extends BaseMappingVisitor 
{
    private IMapping 	projectMapping;
    private SchemaSynchronizationHelper helper;
	
	public SchemaSynchronizationVisitor(IMapping mapping, SchemaSynchronizationHelper helper)
	{		
        projectMapping = mapping;	
        this.helper = helper;
    }
	
	
	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {

	    String className = mapping.getReferencedEntityName();
        if (className != null) {
            IPersistentClass persClass = projectMapping.findClass(className);
            if (persClass == null) {
                return null;
            }

            helper.processFKeyMapping(mapping,
                    persClass.getRootClass().getPersistentClassMapping());
        }
        return mapping;
	}
	
	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
		Object retvalue = null;
		if(mapping.getValue()!=null)
		{		retvalue = mapping.getValue().accept(this, null);				}
		return retvalue;
	}

	public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
		Iterator<IDatabaseColumn> columns = SchemaSynchronizationHelper.getIteratorCopy(simple.getColumnIterator());
		if(columns == null) {
			return null;
		}
		while(columns.hasNext()) {
			IDatabaseColumn column = (IDatabaseColumn)columns.next();
            
            boolean notAKey = !(argument instanceof ICollectionMapping || argument instanceof IPersistentClassMapping);
            
			if(simple.getTable().getColumn(column.getName()) == null || (notAKey && simple.isSimpleValue() && simple.getTable() == column.getOwnerTable() && simple.getTable().isForeignKey(column.getName())))
			{// wrong column has found.
				HibernateAutoMapping.clearLegacyColumnMapping((SimpleValueMapping)simple, column, true);				
			}
		}
		return simple;
	}


    public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
        for(Iterator it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getPropertyIterator()); it.hasNext();)
        {
            IPropertyMapping ipm = (IPropertyMapping)it.next();
            ipm.accept(this, mapping);
        }
        return null;
    }


    public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument) {
        Iterator<IPropertyMapping> it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getFieldMappingIterator());
        while(it.hasNext()) {
            it.next().accept(this,mapping);
        }        
        return null;
        
    }

    public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
        Iterator<IPropertyMapping> it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getPropertyIterator());
        while(it.hasNext()) {
            it.next().accept(this,mapping);
        }
        return null;
    }

    private boolean isEmptyValue(IPersistentValueMapping value) {
        if (value instanceof ISimpleValueMapping) {
            ISimpleValueMapping element = (ISimpleValueMapping) value;
            
            if (element.getColumnSpan() == 0 && element.getFormula() == null)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) {
        if (mapping.getElement() != null)
        {
            mapping.getElement().accept(this,mapping);
        }
        if (mapping.getKey() != null)
        {
            helper.processFKeyMapping(mapping.getKey(),
                    mapping.getOwner());
        }
        
        if (isEmptyValue(mapping.getElement()) || isEmptyValue(mapping.getKey()))
            HibernateAutoMappingHelper.removeMapping(mapping.getFieldMapping());
        
        return null;
    }

}
 