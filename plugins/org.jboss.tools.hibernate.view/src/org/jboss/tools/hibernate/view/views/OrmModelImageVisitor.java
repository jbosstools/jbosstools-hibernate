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
package org.jboss.tools.hibernate.view.views;

import java.util.ResourceBundle;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
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
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.view.ViewPlugin;



/**
 * @author Tau
 *
 */
public class OrmModelImageVisitor implements IOrmModelVisitor, IHibernateMappingVisitor {
	
	private ResourceBundle BUNDLE = ViewPlugin.BUNDLE_IMAGE;
	
	private static Integer ID = new Integer(1);
	private static Integer VER = new Integer(2);	

	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitOrmModel(org.jboss.tools.hibernate.core.IOrmProject, java.lang.Object)
	 */
	public Object visitOrmProject(IOrmProject schema, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.OrmModel")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseSchema(org.jboss.tools.hibernate.core.IDatabaseSchema, java.lang.Object)
	 */
	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseSchema")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable, java.lang.Object)
	 */
	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		if ((table != null) && table.isView() ){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseView")); //$NON-NLS-1$			
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseTable")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseColumn(org.jboss.tools.hibernate.core.IDatabaseColumn, java.lang.Object)
	 */
	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		// #added# by Konstantin Mishin on 24.12.2005 fixed for ESORM-256
		if(column.isUnique())
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseUniqueKeyColumn"));
		// #added# 
		else if (column.isPrimaryKey()&& column.getOwnerTable() != null &&  column.getOwnerTable().isForeignKey(column.getName())){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabasePrimaryForeignKeysColumn"));
		} else if (column.isPrimaryKey()){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabasePrimaryKeyColumn")); //$NON-NLS-1$
		} else if (column.getOwnerTable() != null &&  column.getOwnerTable().isForeignKey(column.getName())){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseForeignKeyColumn")); //$NON-NLS-1$
		} else return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseColumn")); //$NON-NLS-1$

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseConstraint(org.jboss.tools.hibernate.core.IDatabaseConstraint, java.lang.Object)
	 */
	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseConstraint")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPackage(org.jboss.tools.hibernate.core.IPackage, java.lang.Object)
	 */
	public Object visitPackage(IPackage pakage, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Package")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitMapping(org.jboss.tools.hibernate.core.IMapping, java.lang.Object)
	 */
	public Object visitMapping(IMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Mapping")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitMappingStorage(org.jboss.tools.hibernate.core.IMappingStorage, java.lang.Object)
	 */
	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.MappingStorage")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentClass(org.jboss.tools.hibernate.core.IPersistentClass, java.lang.Object)
	 */
	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentClass"));  //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentField(org.jboss.tools.hibernate.core.IPersistentField, java.lang.Object)
	 */
	public Object visitPersistentField(IPersistentField field, Object argument) {
	
		//((IHibernateClassMapping)field.getOwnerClass().getPersistentClassMapping()).getIdentifierProperty();
		//==propertyMapping
		
		//add tau 08.04.2005
		
		if (field !=null){
			IPersistentFieldMapping mapping = field.getMapping();
			if (mapping != null){
				IPersistentValueMapping valueMapping = mapping.getPersistentValueMapping();
				if (valueMapping != null){
					IPersistentClass pClass = field.getOwnerClass();
					if (pClass != null){
						IPersistentClassMapping classMapping = pClass.getPersistentClassMapping();
						if (classMapping != null){
							if (mapping == ((IHibernateClassMapping)classMapping).getIdentifierProperty()){
								argument = ID;
							} else if (mapping == ((IHibernateClassMapping)classMapping).getVersion()){
								argument = VER;								
							}
						}
					}
					//akuzmin 14.09.2005 show parent image
					if ((((PropertyMapping)field.getMapping()).getPropertyMappingHolder() instanceof ComponentMapping)
						&&(field.getName().equals(((ComponentMapping)((PropertyMapping)field.getMapping()).getPropertyMappingHolder()).getParentProperty())))
					{
						return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldParent"));//parent image						
					}
					else
						return valueMapping.accept(this, argument);					
				}
			}
		}
		
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentClassMapping(org.jboss.tools.hibernate.core.IPersistentClassMapping, java.lang.Object)
	 */
	public Object visitPersistentClassMapping(IPersistentClassMapping mapping,	Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentClassMapping")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentFieldMapping(org.jboss.tools.hibernate.core.IPersistentFieldMapping, java.lang.Object)
	 */
	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping,	Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMapping")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentValueMapping(org.jboss.tools.hibernate.core.IPersistentValueMapping, java.lang.Object)
	 */
	public Object visitPersistentValueMapping(IPersistentValueMapping mapping,	Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentValueMapping")); //$NON-NLS-1$
	}

	// add tau 08.04.2005 for PersistentField icons
	public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
		if (argument == ID){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple_id")); //$NON-NLS-1$				
		} else if (argument == VER){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple_version")); //$NON-NLS-1$
		} else {
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$			
		}

	}

	public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldAny")); //$NON-NLS-1$
	}

	public Object visitListMapping(IListMapping column, Object argument) {
		if (column !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_list")); //$NON-NLS-1$
		}

		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitArrayMapping(IArrayMapping kage, Object argument) {
		if (kage !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_array")); //$NON-NLS-1$
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
		if (argument == ID){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldComponent_id")); //$NON-NLS-1$				
		} else {
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldComponent")); //$NON-NLS-1$			
		}
	}

	public Object visitBagMapping(IBagMapping bagMapping, Object argument) {
		if (bagMapping !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_bag")); //$NON-NLS-1$			
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitIdBagMapping(IIdBagMapping table, Object argument) {
		if (table !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_idbag")); //$NON-NLS-1$			
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping constraint, Object argument) {
		if (constraint !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_primitive_array")); //$NON-NLS-1$
		}		
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitMapMapping(IMapMapping mapping, Object argument) {
		if (mapping !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_map")); //$NON-NLS-1$
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitSetMapping(ISetMapping mapping, Object argument) {
		if (mapping !=null){
			// edit tau 13.05.2005
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_set")); //$NON-NLS-1$
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldOne-to-many")); //$NON-NLS-1$
	}

	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-many")); //$NON-NLS-1$
	}

	public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-any")); //$NON-NLS-1$
	}

	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
		if (argument == ID){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-one_id")); //$NON-NLS-1$				
		} else {
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-one")); //$NON-NLS-1$			
		}
	}

	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
		if (argument == ID){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldOne-to-one_id")); //$NON-NLS-1$				
		} else {
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldOne-to-one")); //$NON-NLS-1$			
		}		
	}

	public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
		// ??
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentJoinedSubclassMapping")); //$NON-NLS-1$
	}

	public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentRootClassMapping")); //$NON-NLS-1$
	}

	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentSubclassMapping")); //$NON-NLS-1$
	}

	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentJoinedSubclassMapping")); //$NON-NLS-1$
	}

	public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentUnionSubclassMapping")); //$NON-NLS-1$
	}

	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
		if (mapping != null){
			IHibernateValueMapping valueMapping = mapping.getValue();
			if (valueMapping != null) return valueMapping.accept(this, argument);			
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	// add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.NamedQueryMapping")); //$NON-NLS-1$
	}

}
