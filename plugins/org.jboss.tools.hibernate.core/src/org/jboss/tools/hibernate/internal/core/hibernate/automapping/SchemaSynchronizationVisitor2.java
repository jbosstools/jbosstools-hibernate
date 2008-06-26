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

/**
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;

public class SchemaSynchronizationVisitor2 extends BaseMappingVisitor {
//    private IMapping 	projectMapping;
    private SchemaSynchronizationHelper helper;
    private ArrayList<IDatabaseTable> tables = new ArrayList<IDatabaseTable>();
	private ArrayList<IDatabaseTable> linkTables = new ArrayList<IDatabaseTable>();
    
    private ArrayList<ICollectionMapping> manyToManyCollectionsToClear = new ArrayList<ICollectionMapping>();
    
    // added by Nick 01.08.2005
    private Map<IHibernateKeyMapping,ICollectionMapping> uniCollectionsKeysMap = new HashMap<IHibernateKeyMapping,ICollectionMapping>();
    // by Nick
    
	public SchemaSynchronizationVisitor2(IMapping 	mapping, SchemaSynchronizationHelper helper, IDatabaseTable[] tables, IDatabaseTable[] linkTables) {
//		projectMapping = mapping;
        this.helper = helper;
        for (int i = 0; i < tables.length; i++) {
            IDatabaseTable table = tables[i];
            
            this.tables.add(table);
        };
        
        for (int i = 0; i < linkTables.length; i++) {
            IDatabaseTable table = linkTables[i];
            
            this.linkTables.add(table);
        };
        
	}

	public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) {
        if (linkTables.contains(mapping.getCollectionTable()) && mapping.getElement() instanceof IManyToManyMapping)
        {
            manyToManyCollectionsToClear.add(mapping);
            return null;
        }
        
		return super.visitCollectionMapping(mapping, argument);
	}

	public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
        for(Iterator it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getPropertyIterator()); it.hasNext();)
        {
            IPropertyMapping ipm = (IPropertyMapping)it.next();
            ipm.accept(this, mapping);
        }
        return null;
	}

	public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
        Iterator it = mapping.getPropertyIterator();
        while(it.hasNext())
        {
            ((IOrmElement)it.next()).accept(this,mapping);
        }
        return null;
	}

	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
		return null;
	}

	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
//		if (linkTables.contains(mapping.getTable()))
//        {
		    //HibernateAutoMappingHelper.removeMapping(mapping.getFieldMapping());
//        }
		return super.visitManyToOneMapping(mapping, argument);
	}

	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
	    if(argument instanceof ICollectionMapping)
	    {
            ICollectionMapping collection = (ICollectionMapping) argument;
            
	        IPersistentClassMapping ihcm = null;
            
            if (mapping.getAssociatedClass() != null)
                ihcm = mapping.getAssociatedClass().getRootClass();
	        
            if(ihcm == null)
	        {// associated class has not set.
//	            IPersistentClass 	refclass 		= null;
//	            String 				refclassname 	= mapping.getReferencedEntityName();
//	            if(refclassname != null)
//	            {
//	            refclass = projectMapping.
//	            findClass((refclassname == null) ? inDepthSearchReferencedEntityName(argument): refclassname);
//	            }
//	            if(refclass == null)
//	            {		clearOneToManyMapping(mapping, argument);			}
//	            
//	            ihcm = refclass.getPersistentClassMapping();
	            return null;
	        }
	        
	        IDatabaseTable refTable = ihcm.getDatabaseTable();
	        if (!tables.contains(refTable))
	            return null;
	        
	        IHibernateKeyMapping key = collection.getKey();
	        
	        boolean biDirectional = helper.isPairedKey(key);
	        
	        IDatabaseTableForeignKey fKey = helper.processFKeyMapping(key,collection.getOwner());
	        
	        if(fKey == null) {       
	            clearOneToManyMapping(mapping, argument);           
	        } else {
	            collection.setInverse(biDirectional);
	        }
	        
	        if (!collection.isInverse()) {
	            uniCollectionsKeysMap.put(key,collection);
	        }
	    }
		
		// check columns;
//		while(keycolumns.hasNext())
//		{
//			IDatabaseColumn keycolumn  = (IDatabaseColumn)keycolumns.next();
//			IDatabaseColumn tempcolumn = reftable.getColumn((Column)keycolumn); 
//			if(tempcolumn == null || tempcolumn.getPersistentValueMapping() == null)
//			{// the column had not found or the column w/o pvmapping
//				clearOneToManyMapping(mapping, argument);
//			}
//		}

        
        return null;
	}
	
/*
	private String inDepthSearchReferencedEntityName(Object argument) {
		String 				refname = null;
		ICollectionMapping 	mapping = null;
		if(argument instanceof ICollectionMapping) {mapping = (ICollectionMapping)argument;}
		if(mapping == null) return refname;
		refname = ((CollectionMapping)mapping).getCollectionElementClassName(); 
		return refname;
	}
*/

	private void clearOneToManyMapping(IOneToManyMapping mapping, Object argument)
	{
		mapping.setAssociatedClass(null);
		mapping.setReferencedEntityName(null);
		mapping.setTable(null);
		
		if(argument instanceof ICollectionMapping)
		{	
            ICollectionMapping collection = (ICollectionMapping) argument;
            HibernateAutoMappingHelper.removeMapping(collection.getFieldMapping());
            if (collection.getKey() != null)
            {
                HibernateAutoMapping.clearLegacyColumnMapping(collection.getKey(),null,false);
            }
            if (collection.getElement() instanceof SimpleValueMapping)
            {
                HibernateAutoMapping.clearLegacyColumnMapping(((SimpleValueMapping)collection.getElement()),null,false);                 
            }
            else
            {
                collection.clear();
            }
        }
	}
	
 
    Map getUniCollectionsKeysMap() {
        return uniCollectionsKeysMap;
    }

    public void process()
    {
        while (!manyToManyCollectionsToClear.isEmpty())
        {
            ICollectionMapping mapping = (ICollectionMapping) manyToManyCollectionsToClear.get(0);
            
            //find more mappings in this link table
            //if there are some except current then remove all them from queue and exit
            
            boolean skipCurrentCollection = false;
            
            Iterator itr = manyToManyCollectionsToClear.iterator();
            
            while (itr.hasNext())
            {
                ICollectionMapping collection = (ICollectionMapping) itr.next();
                
                if (collection.getCollectionTable() == mapping.getCollectionTable() && collection != mapping)
                {
                    skipCurrentCollection = true;
                    itr.remove();
                }
            }
            
            manyToManyCollectionsToClear.remove(0);
            
            if (skipCurrentCollection)
                continue;

            if (mapping.getElement() instanceof SimpleValueMapping) {
                HibernateAutoMapping.clearLegacyColumnMapping(
                        (SimpleValueMapping) mapping.getElement(), null, false);
                
                mapping.setElement(null);
            }
            if (mapping.getKey() instanceof SimpleValueMapping) {
                HibernateAutoMapping.clearLegacyColumnMapping(
                        (SimpleValueMapping) mapping.getKey(), null, false);

                mapping.setKey(null);
            }
            
            HibernateAutoMappingHelper.removeMapping(mapping.getFieldMapping());
        }
    }
    
    public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument) {
        Iterator it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getFieldMappingIterator());
        while(it.hasNext()) {
            ((IOrmElement)it.next()).accept(this,mapping);
        }        
        return null;
        
    }

}
 