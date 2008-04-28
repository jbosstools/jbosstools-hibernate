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
package org.jboss.tools.hibernate.internal.core.hibernate.validation;

import java.util.Iterator;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IListMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPrimitiveArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.ISetMapping;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
 
//TODO delete this class

public class MappingValidationCorrectVisitor extends BaseMappingVisitor 
{

//	private UpdateMappingVisitor updater;
	private IMapping 	projectMapping;
	
	
	public MappingValidationCorrectVisitor(IMapping mapping)
	{	projectMapping = mapping;
		//updater = new UpdateMappingVisitor(mapping);
	}
	
	public void verifyValidtion()
	{
		IPersistentClassMapping[] classmappings = projectMapping.getPersistentClassMappings();
		for(int i = 0; i < classmappings.length; i++)
		{
			Iterator it = classmappings[i].getFieldMappingIterator();
			while(it.hasNext())
			{
				((IOrmElement)it.next()).accept(this,null);
			}
		}
	}
	
	/**
	 * return class name which associated with the <fieldmapping> 
	 * @param fieldmapping
	 * @return class name or null.
	 */
	private String getAssociatedClassName(IPersistentFieldMapping fieldmapping)
	{
		String name = null;
		if(fieldmapping != null)
		{
			IPersistentField pf = fieldmapping.getPersistentField();
			if(pf != null)
				{name = pf.getType();}
		}
		return name;
	}

	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) 
	{
		if(projectMapping == null) return null;
		String className=mapping.getReferencedEntityName();
		if(className==null){
			className = getAssociatedClassName(mapping.getFieldMapping());
			}
		if(className!=null){
			IPersistentClass persClass = projectMapping.findClass( className );
			if ( persClass == null ) {
				return null;
			}
			IHibernateClassMapping associatedClassMapping = (IHibernateClassMapping) persClass.getPersistentClassMapping() ;
			if(mapping.getReferencedPropertyName() == null)
			{
				IHibernateKeyMapping kmap = associatedClassMapping.getIdentifier();
				if(kmap == null)
				{ return null;}
                
    			IDatabaseTable table = mapping.getTable();
                Iterator it;
    			if(table != null)
    			{
    				if(mapping.getForeignKeyName() == null) return null;
    				IDatabaseTableForeignKey fk = table.getForeignKey(mapping.getForeignKeyName());
    				if(fk == null)
    				{
    					ForeignKey temp = new ForeignKey();
    					temp.setName(mapping.getForeignKeyName());
    					((Table)table).addForeignKey(mapping.getForeignKeyName(), temp);
        				fk = table.getForeignKey(mapping.getForeignKeyName());
    				}
   					if(fk.getColumnSpan() < 1)
   					{		
   						Iterator it1 	 = mapping.getColumnIterator();
   						Iterator itassoc = kmap.getColumnIterator();
   						IDatabaseColumn column1 = null;
   						IDatabaseColumn refcolumn = null;
   				        while(it1.hasNext() && itassoc.hasNext())
   						{
   				        	column1 	= (IDatabaseColumn)it1.next();
   				        	refcolumn 	= (IDatabaseColumn)itassoc.next();
   				        	
   				        	int typecode = refcolumn.getSqlTypeCode();
   				        	String typename = refcolumn.getSqlTypeName();
   				        	if(typecode == 0)
   				        	{
   				        		kmap.accept(this,null);
   				        		if(kmap.getTable() != null && column1.getName() != null && kmap.getTable().getColumn(column1.getName()) != null)
   				        		typecode = kmap.getTable().getColumn(column1.getName()).getSqlTypeCode();
   				        	}
   							if(column1.getSqlTypeCode() == 0 || column1.getSqlTypeCode() == 1111)
   							{
   								if(typecode == 0)
   								{
   									if(refcolumn.getPersistentValueMapping() instanceof SimpleValueMapping)
   									{
   										SimpleValueMapping svmapping = (SimpleValueMapping)refcolumn.getPersistentValueMapping();
   										if(svmapping.getType() != null)
   											typecode = svmapping.getType().getSqlType();
   									}
   								}
   								column1.setSqlTypeCode(typecode);
                                // changed by Nick 07.09.2005
                                if (column1.isNativeType())
                                {
                                    column1.setSqlTypeName(typename);
                                }
                                // by Nick
   							}
   					        fk.addColumn(column1);
   				        }
  					}

			        if(fk.getTable() == null || fk.getTable() != mapping.getTable())
			        	{		fk.setTable(mapping.getTable());			}
			        if(fk.getReferencedTable() == null ||fk.getReferencedTable() != kmap.getTable())
			        	{		((ForeignKey)fk).setReferencedTable(kmap.getTable());		}

   	                HibernateAutoMapping.clearLegacyColumnMapping(mapping,null,false);
					it = fk.getColumnIterator();
					while(it.hasNext())
					{	
						IDatabaseColumn column1 = (IDatabaseColumn)it.next();
						column1.setOwnerTable(table);
						mapping.addColumn(column1);		
					}
    			}
			}
		}
		return null;
	}
	
	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) 
	{		visitManyToOneMapping(mapping, argument);		return null;		}
	
	public Object visitBagMapping(IBagMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		return null;
	}

	/**
	 * update element mapping & key
	 * */
	public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) 
	{
		if(mapping.getElement() != null)
			mapping.getElement().accept(this, mapping);
		
		if(mapping.getKey() == null)
		{		return null;			}
		
			Iterator columns = mapping.getKey().getColumnIterator();
			while(columns.hasNext())
			{
				IDatabaseColumn column = (IDatabaseColumn)columns.next();
				if(column.getOwnerTable() == null || column.getOwnerTable() != mapping.getCollectionTable())
				{		column.setOwnerTable(mapping.getCollectionTable());		}
			}
			if(mapping.getKey().getTable() == null || mapping.getKey().getTable() != mapping.getCollectionTable())
			{
				mapping.getKey().setTable(mapping.getCollectionTable());
			}
		return null;
	}

	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping mapping, Object argument) {
		visitListMapping((IListMapping)mapping, argument);
		return null;
	}
	public Object visitSetMapping(ISetMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		return null;
	}
	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
		visitListMapping((IListMapping)mapping, argument);
		return null;
	}
	public Object visitListMapping(IListMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		mapping.getIndex().accept(this, null);
		return null;
	}
	public Object visitComponentMapping(IComponentMapping mapping, Object argument)
	{
		Iterator it = mapping.getPropertyIterator();
		while(it.hasNext())
		{
			IPropertyMapping ipm = (IPropertyMapping)it.next();// todo cme
			ipm.accept(this, null);
		}	
		return null;
	}
}
 