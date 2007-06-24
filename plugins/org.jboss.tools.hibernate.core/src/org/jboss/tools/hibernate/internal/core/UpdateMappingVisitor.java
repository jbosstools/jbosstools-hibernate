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

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinedSubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IListMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IMapMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPrimitiveArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISetMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.ISubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IUnionSubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.data.PrimaryKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;

/** *  * @author yk * */public class UpdateMappingVisitor extends BaseMappingVisitor {
	private IMapping 	projectMapping;
	private Hashtable<String,IPersistentField>	duplicate_fields = new Hashtable<String,IPersistentField>();
		public UpdateMappingVisitor(IMapping projectMapping){
		setProjectMapping(projectMapping);
	}
	public void setProjectMapping(IMapping projectMapping)
	{
		this.projectMapping=projectMapping;
	}
	/**	 * update mapping for persistent classes	 * @param mappings - mappipngs of persistent classes 	 */	public void doMappingsUpdate(IPersistentClassMapping[] mappings) {

		int mappingsLength = mappings.length;
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("=>> UpdateMappingVisitor.doMappingsUpdate(...), IPersistentClassMapping[]= " + mappingsLength);		
		
		
// added by yk 08.09.2005
        IPersistentClassMapping[] sortedmappings = new IPersistentClassMapping[mappings.length];
        System.arraycopy(mappings,0,sortedmappings,0,mappings.length);
        Arrays.sort(sortedmappings,HibernateAutoMapping.mappingComparator);
// added by yk 08.09.2005.
		doUpdateClassMappings(sortedmappings);		doUpdateFieldMappings(sortedmappings);
		// need 2 call - tau 23.03.2006
		doUpdateFieldMappings(sortedmappings);
 		duplicate_fields.clear();
	}
	private void doUpdateClassMappings(IPersistentClassMapping[] mappings)	{		for(int i = 0; i < mappings.length; i++)		{
			duplicate_fields.clear();			try{
				mappings[i].accept(this, null);			}catch(Exception ex){				ExceptionHandler.logInfo("update failed for "+mappings[i].getName());			}		}	}
	private void doUpdateFieldMappings(IPersistentClassMapping[] mappings)	{		for(int i=0;i<mappings.length;++i)		{
			try 			{
				duplicate_fields.clear();
				IHibernateClassMapping hcm=(IHibernateClassMapping) mappings[i];
				if(hcm instanceof ISubclassMapping)
				{
					if(hcm.getSuperclass() == null) continue; // incorrect mapping file for the mapping;
				}
				if(hcm.getDiscriminator()!=null)hcm.getDiscriminator().accept(this, null);
				if(hcm.getIdentifierProperty()!=null)hcm.getIdentifierProperty().accept(this, null);
				else if(hcm.getIdentifier()!=null) hcm.getIdentifier().accept(this, null);
				if(hcm.getVersion()!=null)hcm.getVersion().accept(this, null);
				Iterator it = mappings[i].getFieldMappingIterator();
				while(it.hasNext())
				{
					((IOrmElement)it.next()).accept(this,null);
				}
// added by yk 14.07.2005
				if(duplicate_fields.size() > 0)
				{// remove duplicate fields from the class mapping;
					IPersistentField[] fields = mappings[i].getPersistentClass().getFields();
					for(int t = 0; t < fields.length; t++)
					{
						String duplicate_name = fields[t].getName();
						if( duplicate_fields.containsKey(duplicate_name) )
						{
							mappings[i].getPersistentClass().removeField(duplicate_name);
						}
					}
				}
// added by yk 14.07.2005 stop
							}			catch (Exception ex)			{				ExceptionHandler.logInfo("update failed for "+mappings[i].getName());;			}
		}	}		private void updateComponent(IComponentMapping mapping){		if(mapping.isDynamic()) return;
		try{		    String className=mapping.getComponentClassName();
		    if(className==null)
		    {
		        if (mapping.getFieldMapping() != null)
		        {
		            if (mapping.getFieldMapping().getPersistentField() != null)
		            {
		                IPersistentField field = mapping.getFieldMapping().getPersistentField();
		                mapping.setComponentClassName(field.getType());
		            }
		        }
		        className=mapping.getComponentClassName();
		        if(className==null)
		            return;
		    }
			
			PersistentClass pc=(PersistentClass)mapping.getComponentClass();
			pc.isResourceChanged();
//				pc.refresh();

            //TODO Nick - hard test this code
			if(pc.getPersistentClassMapping() == null)
			{
				//pc.setPersistentClassMapping( ((ComponentMapping)mapping).getOwner().getPersistentClass().getPersistentClassMapping() );
                pc.setPersistentClassMapping( ((ComponentMapping)mapping).getOwner());
			}
            // Task end
            
			for(Iterator it = mapping.getPropertyIterator(); it.hasNext();)
			{
				PropertyMapping fm = (PropertyMapping)it.next();
				PersistentField pf=pc.getOrCreateField(fm.getName());
				fm.setPersistentField(pf);
				fm.setPersistentClassMapping((ClassMapping)mapping.getComponentClass().getPersistentClassMapping());
				pf.setMapping(fm);
				if(pf.getType() == null)
				{	
					if(fm.getValue() != null && fm.getValue().getType() != null && fm.getValue().getType().getJavaType() != null)
					pf.setType(fm.getValue().getType().getJavaType().getName());
				}
			}
			if(mapping.isProperties_component())
			{		removeUnmappedFields(pc);		}

		} catch(Exception cex){
			ExceptionHandler.logInfo("update failed for "+mapping.getComponentClassName());	
		}
	}

	private void removeUnmappedFields(PersistentClass pc)
	{
		IPersistentField[] fields = pc.getFields();
		for(int i=0;i<fields.length;++i)
		{
			if(fields[i].getMapping()==null) pc.removeField(fields[i].getName());
			else duplicate_fields.put(fields[i].getName(),fields[i]);
		}
	}		private int hibernateTypeToSqlType(String hbTypeName){		if(hbTypeName!=null){			Type hbType=Type.getType(hbTypeName);			if(hbType!=null) return hbType.getSqlType();		}		return Types.NULL;	}		public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {		visitAnyMapping(mapping, argument);		return null;	}		public Object visitAnyMapping(IAnyMapping mapping, Object argument) {		int col=mapping.getColumnSpan();		Iterator it=mapping.getColumnIterator();		if(col>0){			IDatabaseColumn column=(IDatabaseColumn)it.next();			//- check types of col			if(column.getSqlTypeCode()==0){				column.setSqlTypeCode(hibernateTypeToSqlType(mapping.getMetaType()));			}		}		if(col>1){			IDatabaseColumn column=(IDatabaseColumn)it.next();			//- check types of col			if(column.getSqlTypeCode()==0){				column.setSqlTypeCode(hibernateTypeToSqlType(mapping.getIdentifierType()));			}		}		return null;	}
	public Object visitComponentMapping(IComponentMapping mapping, Object argument) 	{
		updateComponent(mapping);		
		if(isComponentHasParentProperty(mapping))
		{
			setMappingForComponentParentProperty(mapping);
			updateComponent(mapping);		
		}
		Iterator it = mapping.getPropertyIterator();
		while(it.hasNext())		{			IPropertyMapping ipm = (IPropertyMapping)it.next();// todo cme			ipm.accept(this, null);
		}		return null;	}
	private boolean isComponentHasParentProperty(IComponentMapping mapping)
	{		return mapping.getParentProperty() != null && mapping.getParentProperty().length() > 0 ;		}

	private void setMappingForComponentParentProperty(IComponentMapping mapping)
	{
		IPersistentField ipf = mapping.getComponentClass().getField(mapping.getParentProperty());
		if(ipf != null && ipf.getMapping() == null)
		{
	        HibernateAutoMappingHelper helper = new HibernateAutoMappingHelper(
	                ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping) projectMapping));

	        PropertyMapping pm = helper.createPropertyMapping(ipf);
	        pm.setValue(helper.createOneToOneMapping(ipf, ((ComponentMapping)mapping).getOwner().getPersistentClass()));
	        pm.setPropertyMappingHolder(mapping);
	        Iterator it = ((ComponentMapping)mapping).getOwner().getIdentifier().getColumnIterator();
	        while(it.hasNext())
	        {
	        	// added by Nick 29.09.2005
	            IDatabaseColumn column = (IDatabaseColumn)it.next();
                IPersistentValueMapping value = column.getPersistentValueMapping();
                // by Nick
                
                ((ISimpleValueMapping)pm.getValue()).addColumn(column);
                
                // added by Nick 29.09.2005
                if (value != null)
                    column.setPersistentValueMapping(value);
                // by Nick
	        }
	        ipf.setMapping(pm);
		}
	}
	
	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
		
		String className=mapping.getReferencedEntityName();
		if(className==null)
		{		className = getAssociatedClassName(mapping.getFieldMapping());			}
		if(className!=null){
			IPersistentClass persClass = projectMapping.findClass( className );
			if ( persClass == null ) 
			{		return null;		}
			IHibernateClassMapping 	associatedClassMapping 	= (IHibernateClassMapping) persClass.getPersistentClassMapping() ;
			IHibernateKeyMapping 	kmap 					= associatedClassMapping.getIdentifier();
			if(kmap == null)		{ return null;}
			Iterator it 		= mapping.getColumnIterator();
			Iterator itassoc 	= kmap.getColumnIterator();

// added by yk 02.12.2005
			IDatabaseTable table1 = ((ICollectionMapping)argument).getCollectionTable();
			IDatabaseTablePrimaryKey pk1= table1.getPrimaryKey();
			if(pk1 == null) pk1 = new PrimaryKey();
			if(associatedClassMapping.getClassName() != null)
				getOrCreateForeignKey(mapping, associatedClassMapping.getClassName() );
			table1.setPrimaryKey(pk1);
// added by yk 02.12.2005.
			while(it.hasNext())
			{
				IDatabaseColumn keycolumn = (IDatabaseColumn)it.next();
				// added by yk 02.12.2005
				if(pk1.getColumnIndex(keycolumn) == -1)
					pk1.addColumn(keycolumn);
				// added by yk 02.12.2005.

				if(keycolumn.getSqlTypeCode() != 0) continue;
				if(itassoc.hasNext())
					setColumnParam(keycolumn, (IDatabaseColumn)itassoc.next());
				
				if(keycolumn.getSqlTypeCode() == 0)
				{
					if(argument != null && argument instanceof ICollectionMapping)
					{
						IDatabaseTable table = ((ICollectionMapping)argument).getCollectionTable(); 
						if(table != null)
						{
							IDatabaseColumn tempcolumn = table.getColumn(keycolumn.getName());
							keycolumn.setSqlTypeCode( (tempcolumn != null) ? tempcolumn.getSqlTypeCode() : 0 );
						}
					if(keycolumn.getSqlTypeCode() == 0)
					{
						IHibernateKeyMapping keymapping = ((ICollectionMapping)argument).getKey();
						if(keymapping != null)
						{
							Iterator itclms = keymapping.getColumnIterator();
							while(itclms.hasNext())
							{
								IDatabaseColumn clm = (IDatabaseColumn)itclms.next();
								if(clm != null && clm.getName() != null 		&& 
									clm.getName().equals(keycolumn.getName()) 	&&
									clm.getSqlTypeCode() != 0)
									setColumnParam(keycolumn, clm);
							}
						}
					}
				}
				}
			}
		}
		return null;
	}

	private void setColumnParam(IDatabaseColumn thecolumn, IDatabaseColumn origin)
	{
		if(thecolumn.getSqlTypeCode() == 0)
			thecolumn.setSqlTypeCode( (origin != null) ? origin.getSqlTypeCode() : 0 );
		if(thecolumn.getLength() == Column.DEFAULT_LENGTH)
			thecolumn.setLength( (origin != null) ? origin.getLength() : 0 );
		if(thecolumn.getPrecision() == Column.DEFAULT_PRECISION)
			thecolumn.setPrecision((origin != null) ? origin.getPrecision() : 0);
		if(thecolumn.getScale() == Column.DEFAULT_SCALE)
			thecolumn.setScale((origin != null) ? origin.getScale() : 0);
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
	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) 	{
		if(mapping.getColumnSpan() < 0)
		{
			IDatabaseTable table = mapping.getTable();
			IDatabaseTableForeignKey fk = table.getForeignKey(mapping.getForeignKeyName());
			if(fk != null)
			{
	            //HibernateAutoMapping.clearLegacyColumnMapping(mapping,null,false);
	            Iterator columns = fk.getColumnIterator();
				while(columns.hasNext())
				{	
					IDatabaseColumn column1 = (IDatabaseColumn)columns.next();
					column1.setOwnerTable(table);
					mapping.addColumn(column1);
				}
			}			
		}
//		 added by yk 20.09.2005
		if( needRecovery(mapping) )
		{
			if(argument instanceof IPropertyMapping)
			{
				IPersistentField field = ((IPropertyMapping)argument).getPersistentField();
				if(field == null || field.getOwnerClass() == null) return null;
				IDatabaseTableForeignKey fk = getOrCreateForeignKey(mapping, field.getOwnerClass().getName());
				if(fk != null)
				{
					Iterator fkcolumns = fk.getColumnIterator();
					if(fk.getColumnSpan() > 0)
					{	
						//HibernateAutoMapping.clearLegacyColumnMapping(mapping, null, false);
						while(fkcolumns.hasNext())
						{
							mapping.addColumn((IDatabaseColumn)fkcolumns.next());
						}
					}
				}
			}
		}
// added by yk 20.09.2005.
		return null;	}
	private boolean needRecovery(IManyToOneMapping mapping)
	{
		boolean recovery = false;
		recovery = (isColumnHasWrongType(getCopyIterator(mapping.getColumnIterator())) || 
				(mapping.getTable() != null && mapping.getTable().getForeignKey(mapping.getForeignKeyName()) == null) );
		return recovery;
	}
	private Iterator<IDatabaseColumn> getCopyIterator(Iterator it) {
		List<IDatabaseColumn> items = new ArrayList<IDatabaseColumn>();
		while(it.hasNext()) {
			Object o = it.next();
			if(o instanceof IDatabaseColumn) items.add((IDatabaseColumn)it.next());
		}
		return items.iterator();
	}
	
	private boolean isColumnHasWrongType(Iterator<IDatabaseColumn> columns) {
		boolean result = false;
		while(columns.hasNext()) {
			IDatabaseColumn column = columns.next();
			if(column.getSqlTypeCode() == 0) {
				result = true; break;
			}
		}
		return result;
	}

	private IDatabaseTableForeignKey getOrCreateForeignKey(IManyToOneMapping mapping, String ownerclasname) {
		ForeignKey fk = null;
		String fkname = mapping.getForeignKeyName();
		fk = (fkname == null) ? null : (ForeignKey)mapping.getTable().getForeignKey(fkname);
		if(fk == null)
		{
			fk = new ForeignKey();
			fk.setName(mapping.getForeignKeyName());
			fk.setReferencedEntityName(mapping.getReferencedEntityName());
		}
			IPersistentClass pc = projectMapping.findClass(mapping.getReferencedEntityName());
			if(pc != null)
			{
				if(pc.getPersistentClassMapping() != null)
				{
					IDatabaseTable reftable = pc.getPersistentClassMapping().getDatabaseTable();
					if(reftable == null) 	return null;
					IDatabaseTablePrimaryKey refpk = reftable.getPrimaryKey();
					if(refpk 	== null) 	return null;
					fk.setReferencedTable(reftable);
					checkRefTablePKColumns(pc, ownerclasname);
					Iterator refpkcolumns = refpk.getColumnIterator();
					
					List columnnames = getColumnNames(mapping.getColumnIterator());
					Iterator names 	 = columnnames.iterator();
					while(refpkcolumns.hasNext() && names.hasNext())
					{
						IDatabaseColumn column = copyColumn((IDatabaseColumn)refpkcolumns.next(),mapping, (String)names.next());
						IDatabaseColumn existingcolumn = mapping.getTable().getColumn(column.getName()); 
						if(existingcolumn != null )
						{// column exist
							if(existingcolumn.getSqlTypeCode() == 0)
								existingcolumn.setSqlTypeCode(column.getSqlTypeCode());
							if(existingcolumn.getSqlTypeName() == null || existingcolumn.getSqlTypeName().length() ==0)
								existingcolumn.setSqlTypeName(column.getSqlTypeName());
							column = existingcolumn;
						}
						else
						{
							mapping.getTable().addColumn(column);							
						}
						fk.addColumn(column);
					}
				}
			((Table)mapping.getTable()).addForeignKey(fk.getName(),fk);
			fk.setTable(mapping.getTable());
		}
		return fk;
	}
	private List getColumnNames(Iterator columns) {
		ArrayList<String> columnnames = new ArrayList<String>();
		while(columns.hasNext()) {
			columnnames.add( ((IDatabaseColumn)columns.next()).getName());
		}
		return columnnames;
	}
	
	private void checkRefTablePKColumns(IPersistentClass pc, String ownerclassname) {
		ArrayList<String> backward = new ArrayList<String>();
		backward.add(ownerclassname);
		IDatabaseTablePrimaryKey refpk = pc.getPersistentClassMapping().getDatabaseTable().getPrimaryKey();
		if(isColumnHasWrongType(refpk.getColumnIterator()))
			attemptSetPKColumnsType(refpk.getColumnIterator(), backward);
	}
	
	private void attemptSetPKColumnsType(Iterator it, ArrayList backward) {
		while(it.hasNext()) {
			IDatabaseColumn refcolumn = (IDatabaseColumn)it.next();
			if(refcolumn.getPersistentValueMapping() != null &&
					refcolumn.getPersistentValueMapping() instanceof IManyToOneMapping && 
					refcolumn.getSqlTypeCode() == 0)
			{
				String refname = ((IManyToOneMapping)refcolumn.getPersistentValueMapping()).getReferencedEntityName();
				if(backward.contains(refname)) continue; // do not process backward classes.
				if(refname != null)
				{
					IPersistentClass pc = projectMapping.findClass(refname);
					checkRefTablePKColumns(pc,refname);
					IDatabaseTablePrimaryKey refpk = pc.getPersistentClassMapping().getDatabaseTable().getPrimaryKey();
					refcolumn.setSqlTypeCode(((IDatabaseColumn)refpk.getColumnIterator().next()).getSqlTypeCode());
				}
			}
		}
	}

	/*
	 * helper method.
	 */
	private IDatabaseColumn copyColumn(IDatabaseColumn pattern, ISimpleValueMapping mapping, String name)
	{
		Column thecolumn = new Column();
		if(pattern != null)
		{
			thecolumn.setCheckConstraint(pattern.getCheckConstraint());
			thecolumn.setLength(pattern.getLength());
			thecolumn.setName( (name != null && name.length() > 0) ? name : pattern.getName());
			thecolumn.setNullable(pattern.isNullable());
			thecolumn.setPersistentValueMapping(mapping);
			thecolumn.setPrecision(pattern.getPrecision());
			thecolumn.setScale(pattern.getScale());
			thecolumn.setSqlTypeCode(pattern.getSqlTypeCode());
			thecolumn.setSqlTypeName(pattern.getSqlTypeName());
			thecolumn.setUnique(pattern.isUnique());
		}
		return thecolumn;
	}
	public Object visitOneToManyMapping(IOneToManyMapping oneToMany, Object argument) {
		ICollectionMapping collection=(ICollectionMapping) argument;
		String assocClass = oneToMany.getReferencedEntityName();
		IPersistentClass persClass = projectMapping.findClass( assocClass );
		if ( persClass != null ) {
				IHibernateClassMapping classMapping = (IHibernateClassMapping) persClass.getPersistentClassMapping() ;
				oneToMany.setAssociatedClass( classMapping );
				if(collection!=null){
					IDatabaseTable temp=collection.getCollectionTable();
					if(temp!=null && temp!=classMapping.getDatabaseTable()){
						classMapping.getDatabaseTable().addColumns(temp);
					}
					collection.setCollectionTable(classMapping.getDatabaseTable());
				}
		}
		return null;
	}
	
	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) 
	{// not set columns for unidirecion
		// remove all existing columns.
		Iterator itmap = mapping.getColumnIterator();
		while(itmap.hasNext())
		{	itmap.next(); itmap.remove();			}

		if(mapping.getReferencedPropertyName() != null )
		{// association is bidirection;
			String fieldtype 		= mapping.getFieldMapping().getPersistentField().getType();
			String refentitytype 	= mapping.getReferencedEntityName();
			if(fieldtype == null || refentitytype == null || !fieldtype.equals(refentitytype)) return null;
			else
			{
				setOnetoOneColumnsForBiDirection(mapping);
			}
		}
		return null;
	}
	private void setOnetoOneColumnsForBiDirection(IOneToOneMapping mapping)
	{
		String 				refname 	= mapping.getReferencedPropertyName();
		IPersistentClass 	refclass 	= getReferencedClass(mapping);
		if ( refclass == null ) {	return;									}
		IPropertyMapping ipm = ((ClassMapping)refclass.getPersistentClassMapping()).getProperty(refname);
		if(ipm == null){			return;									}
		Iterator it = ipm.getColumnIterator();
		while(it.hasNext())
		{// add new columns
			Column refcolumn = (Column)it.next();
			IPersistentValueMapping pvm = refcolumn.getPersistentValueMapping();
			((SimpleValueMapping)mapping).addColumn(refcolumn);
			refcolumn.setPersistentValueMapping(pvm);
		}
	}
	
	
	private IPersistentClass getReferencedClass(IOneToOneMapping mapping)
	{
		IPersistentClass persClass 	= null;
		String 			 className	=mapping.getReferencedEntityName();
		if(className==null)
		{			className = getAssociatedClassName(mapping.getFieldMapping());		}
		if(className!=null)
		{			persClass = projectMapping.findClass( className );		}
		return persClass;
	}
	
	public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
		if(simple.getColumnSpan() < 1) return null;
 		Iterator it = simple.getColumnIterator();
 		while(it.hasNext())
 		{
 			IDatabaseColumn column = (IDatabaseColumn)it.next();
 			if(column.getSqlTypeCode()==0)
 			{
 				if(simple.getType()!=null)
 					column.setSqlTypeCode(simple.getType().getSqlType());
 			}
 		}
		return null;
	}

	
	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) 
	{
		if(mapping.getValue()!=null)
		{
			IPersistentField pf= mapping.getPersistentField();
			if(pf!=null && (mapping.getValue().getType()) == null){
				if(pf.getType()!=null){
					Type type = TypeUtils.javaTypeToHibType(pf.getType());
					if(type!=null){
						mapping.getValue().setType(type);
					}
				}
			}
			if(pf!=null && (mapping.getValue().getTable()) == null )
			{
				mapping.getValue().setTable(pf.getOwnerClass().getPersistentClassMapping().getDatabaseTable());
			}
			
			mapping.getValue().accept(this, mapping/*null*/);		}		return null;	}
	//------------------------------ class mappings -----------------------------------	public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) 	{	return null;	}	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) 	{		if(mapping.getSuperclass() == null){			IPersistentClass sClass=null;			if(mapping.getExtends()!=null){				sClass=projectMapping.findClass(mapping.getExtends());			}else {				sClass=mapping.getPersistentClass().getSuperClass();			}			if(sClass!=null){				IHibernateClassMapping superclassMapping=(IHibernateClassMapping)sClass.getPersistentClassMapping();				mapping.setSuperclass(superclassMapping);				if(superclassMapping!=null){					superclassMapping.addSubclass(mapping);	                	                //added by Nick 25.05.2005	                if (superclassMapping.getDatabaseTable() != null)	                {						superclassMapping.getDatabaseTable().addPersistentClassMapping(mapping);	                }	                //by Nick	            }			}		}
/*		
		else
		{
			IHibernateValueMapping key = mapping.getKey();
			if(key.getType() == null)
			{		setColumnType(key);			}
		}
*/		
		return null;
	}


	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
		visitSubclassMapping(mapping, argument);
		return null;
	}

	public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
		visitSubclassMapping(mapping, argument);
		return null;
	}
	
	//----------------------------- collections ---------------------------
	public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		mapping.getIdentifier().accept(this, null);
		return null;
	}
	
	public Object visitListMapping(IListMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		mapping.getIndex().accept(this, null);
		if(mapping.getIndex().getType() == null)
		{
			setCollectionIndexTypeListMapping(mapping);
		}
		// for kaa
		if(mapping.getIndex() instanceof IComponentMapping)
		{
			mapping.getIndex().setFieldMapping(mapping.getFieldMapping());
		}
		// for kaa.
		return null;
	}
	private void setCollectionIndexTypeListMapping(IListMapping mapping)
	{
		if( (mapping instanceof IListMapping) )
		{
			Type type = TypeUtils.javaTypeToHibType("int");
			if(type == null)
			{
				//ExceptionHandler.log("UpdateMappingVisitor::setCollectionIndexType(). Error of standard type");
				return;	
			}
			IHibernateValueMapping valmap = mapping.getIndex();
			if(valmap == null)
			{
				//ExceptionHandler.log("UpdateMappingVisitor::setCollectionIndexType(). Error of getting index type for " + mapping.getName());
				return;	
			}
			// set type for valuemapping of the index of the collection.
			valmap.setType(type);
			
			final int columns_count = valmap.getColumnSpan();
			if(columns_count > 0)
			{
				Iterator it = valmap.getColumnIterator();
				Column column = null;
				while(it.hasNext())
				{
					column = (Column)it.next();
					column.setSqlTypeCode(type.getSqlType());
				}
			}
		}
	}
	
	public Object visitMapMapping(IMapMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		mapping.getIndex().accept(this, null);

		IHibernateValueMapping index = mapping.getIndex();
		IHibernateValueMapping element = mapping.getElement();
		if(element.getType() == null)
		{
			setColumnType(element);
		}
		if(index.getType() == null)
		{
			setColumnType(index);
		}
		
		// for kaa
		if(mapping.getIndex() instanceof IComponentMapping)
		{
			mapping.getIndex().setFieldMapping(mapping.getFieldMapping());
		}
		// for kaa.
		
		return null;
	}
	
	private void setColumnType(IHibernateValueMapping mapping)
	{
		if(mapping instanceof IManyToManyMapping)
		{
			String refname = ((IManyToManyMapping)mapping).getReferencedEntityName();
			if(refname != null)
			{
				IPersistentClass 	persClass 	= projectMapping.findClass(refname);
				setColumnTypeForManyToManyMapping((IManyToManyMapping)mapping, persClass);
			}
		}
	}
	private void setColumnTypeForManyToManyMapping(IHibernateValueMapping mapping, IPersistentClass persClass)
	{
			final int 				index_columns_count 		= mapping.getColumnSpan();
			Iterator 				it_index					= mapping.getColumnIterator();
			if(persClass == null) return;
			IHibernateClassMapping 	associatedClassMapping 		= (IHibernateClassMapping) persClass.getPersistentClassMapping() ;
			IDatabaseTablePrimaryKey pk_link 					= associatedClassMapping.getDatabaseTable().getPrimaryKey();
			
            // added by Nick 19.09.2005
            if (pk_link == null)
                return;
            // by Nick
            
            final int 				link_table_pkcolumns_count 	= pk_link.getColumnSpan();
			Iterator 				it_link_pk 					= pk_link.getColumnIterator();
			
			if(link_table_pkcolumns_count != index_columns_count)
			{
				//ExceptionHandler.log(mapping.getName() + "  UpdateMappingVisitor::setColumnTypeForManyToManyMapping() set index type error.");
				return;
			}
			Column index_column = null;
			Column link_column	= null;
			while(it_link_pk.hasNext())
			{
				index_column = (Column)it_index.next();
				link_column	 = (Column)it_link_pk.next();
				
				index_column.setSqlTypeCode(link_column.getSqlTypeCode());
				index_column.setLength(link_column.getLength());
			}
	}
	
	private void checkFKForPrimitiveArray(IArrayMapping mapping)
	{
		IHibernateKeyMapping key = mapping.getKey();
		IDatabaseTable collectiontable = mapping.getCollectionTable();
		if( collectiontable != null && key != null)
		{
			ForeignKey	fk = (ForeignKey)mapping.getCollectionTable().getForeignKey(key.getForeignKeyName());
			checkReferenceTable(mapping.getOwner());
			if(fk == null)
			{
				fk = new ForeignKey();
				fk.setName(key.getForeignKeyName());
				((Table)collectiontable).addForeignKey(fk.getName(),fk);
				fk.setTable(collectiontable);
				fk.setReferencedTable(mapping.getOwner().getDatabaseTable());
			}
			Iterator columns = mapping.getKey().getColumnIterator();
			
            // changed by Nick 19.09.2005
            if (mapping.getOwner().getDatabaseTable().getPrimaryKey() != null)
            {
                Iterator refcolumns = mapping.getOwner().getDatabaseTable().getPrimaryKey().getColumnIterator();
                while(columns.hasNext() && refcolumns.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn)columns.next();
                    IDatabaseColumn refcolumn = (IDatabaseColumn)refcolumns.next();
                    if(column.getSqlTypeCode() == 0)
                        column.setSqlTypeCode(refcolumn.getSqlTypeCode());
                    if(column.getSqlTypeName() == null || column.getSqlTypeName().length() < 1 ||
                            !column.getSqlTypeName().equals(refcolumn.getSqlTypeName()))
                        column.setSqlTypeName(refcolumn.getSqlTypeName());
                    fk.addColumn(column);
                }
            }
		}
	}
	private void checkReferenceTable(IHibernateClassMapping owner)
	{
		IDatabaseTable table = owner.getDatabaseTable();
		IDatabaseTable roottable = owner.getRootTable(); 
		if(table == null) return;
		
        // added by Nick 19.09.2005
		if (table.getPrimaryKey() == null || roottable.getPrimaryKey() == null)
            return ;
        // by Nick
        
        Iterator pkcolumns = table.getPrimaryKey().getColumnIterator();
		Iterator rootcolumns = roottable.getPrimaryKey().getColumnIterator();
		while(pkcolumns.hasNext() && rootcolumns.hasNext())
		{
			IDatabaseColumn column = (IDatabaseColumn)pkcolumns.next();
			IDatabaseColumn rootcolumn = (IDatabaseColumn)rootcolumns.next();
			if(column.getSqlTypeCode() == 0)
				column.setSqlTypeCode(rootcolumn.getSqlTypeCode());
		}
	}
	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping mapping, Object argument) {
		checkFKForPrimitiveArray(mapping);
		visitListMapping((IListMapping)mapping, argument);
		return null;
	}
	public Object visitSetMapping(ISetMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		return null;
	}
	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
		checkFKForPrimitiveArray(mapping);
		visitListMapping((IListMapping)mapping, argument);
		return null;
	}
	public Object visitBagMapping(IBagMapping mapping, Object argument) {
		visitCollectionMapping(mapping, argument);
		return null;
	}

	/**
	 * update element mapping & key
	 * */
	public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) 
	{
		if(mapping.getElement() == null) return null;
		mapping.getElement().accept(this, mapping);
		// for kaa
		if(mapping.getElement() instanceof IComponentMapping)
		{
			mapping.getElement().setFieldMapping(mapping.getFieldMapping());
		}
		// for kaa.
		
		if(mapping.getKey() == null)
		{		return null;			}
		
		mapping.getKey().setTable(mapping.getCollectionTable());
		mapping.getKey().accept(this, null);
		
// add by yk Jun 13, 2005.
		if(mapping.getKey().getType() == null)
		{	setCollectionKeyType(mapping);		}
// add by yk Jun 13, 2005 stop.
		return null;
	}

	private void setCollectionKeyType(ICollectionMapping mapping)
	{
 		Iterator keycolumns = mapping.getKey().getColumnIterator();
		Iterator idcolumns = mapping.getOwner().getIdentifier().getColumnIterator();
		
		while(keycolumns.hasNext())
		{
			IDatabaseColumn keycolumn = (IDatabaseColumn)keycolumns.next();
			if(keycolumn.getSqlTypeCode() != 0) continue;
			if(idcolumns.hasNext())
				setColumnParam(keycolumn, (IDatabaseColumn)idcolumns.next());

			if(keycolumn.getSqlTypeCode() == 0)
			{
				IDatabaseTable table = mapping.getCollectionTable();
				if(table != null)
				{
					IDatabaseColumn tempcolumn = table.getColumn(keycolumn.getName());
					keycolumn.setSqlTypeCode( (tempcolumn != null) ? tempcolumn.getSqlTypeCode() : 0 );
				}
				if(keycolumn.getSqlTypeCode() == 0)
				{
					IHibernateValueMapping element = mapping.getElement();
					if(element != null)
					{
						Iterator itclms = mapping.getElement().getColumnIterator();
						while(itclms.hasNext())
						{
							IDatabaseColumn clm = (IDatabaseColumn)itclms.next();
							if(clm != null && clm.getName() != null 		&& 
								clm.getName().equals(keycolumn.getName()) 	&&
								clm.getSqlTypeCode() != 0)
								setColumnParam(keycolumn, clm);
						}
					}
				}
			}
		}
	}
}
