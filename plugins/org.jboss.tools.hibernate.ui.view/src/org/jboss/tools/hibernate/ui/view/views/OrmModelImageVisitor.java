/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import java.util.ResourceBundle;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UnionSubclass;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;



/**
 * @author Tau
 *
 */
public class OrmModelImageVisitor implements /*IOrmModelVisitor, IHibernateMappingVisitor,*/ PersistentClassVisitor {
	
	private ResourceBundle BUNDLE = ViewPlugin.BUNDLE_IMAGE;
	
	private static Integer ID = new Integer(1);
	private static Integer VER = new Integer(2);	

	
	public Object visitDatabaseTable(Table table) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseTable")); //$NON-NLS-1$
	}

	public Object visitDatabaseColumn(Column column) {
		if(column.isUnique()) {
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseUniqueKeyColumn"));
		}else if (HibernateUtils.isPrimaryKey(column)&& HibernateUtils.getTable(column) != null &&  HibernateUtils.isForeignKey(column)){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabasePrimaryForeignKeysColumn"));
		} else if (HibernateUtils.isPrimaryKey(column)){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabasePrimaryKeyColumn")); //$NON-NLS-1$
		} else if (HibernateUtils.getTable(column) != null &&  HibernateUtils.isForeignKey(column)){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseForeignKeyColumn")); //$NON-NLS-1$
		} else return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.DatabaseColumn")); //$NON-NLS-1$

	}
	
	public Object visitPersistentField(Property field, Object argument) {
		if (field !=null){
			if(field.getValue() != null){
				if(field.getValue() instanceof ManyToOne)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-one")); //$NON-NLS-1$
			}
			if(field.getPersistentClass().getVersion() == field){
				return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple_version")); //$NON-NLS-1$
			}
			if(field.getPersistentClass().getIdentifierProperty() == field){
				return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple_id")); //$NON-NLS-1$
			}
			if (field.getValue() != null && field.getType() != null && field.getType().isCollectionType()) {
				if(field.getValue() instanceof PrimitiveArray)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_primitive_array"));
				else if(field.getValue() instanceof Array)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_array"));
				else if(field.getValue() instanceof List)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_list"));
				else if(field.getValue() instanceof Set)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_set"));
				else if(field.getValue() instanceof Map)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_map"));
				else if(field.getValue() instanceof Bag)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_bag"));
				else if(field.getValue() instanceof IdentifierBag)
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection_idbag"));
				else
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.Collection"));		
			}
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$		
	}

	public Object visitManyToOneMapping(ManyToOne field) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldMany-to-many")); //$NON-NLS-1$		
	}

	public Object visitOneToManyMapping(OneToMany field) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldOne-to-many")); //$NON-NLS-1$		
	}

	public Object visitSimpleValueMapping(SimpleValue field) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$		
	}

	public Object visitComponentMapping(Component mapping) {
		if (mapping != null){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldComponent")); //$NON-NLS-1$			
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitComponentKeyMapping(DependantValue mapping, Object argument) {
		if (argument == ID){
			return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldComponent_id")); //$NON-NLS-1$				
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$
	}

	public Object visitDatabaseColumn(Column column, Object argument) {
		return null;
	}

	public Object accept(RootClass arg0) {
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentClass"));  //$NON-NLS-1$
	}

	public Object accept(UnionSubclass arg0) {
		return null;
	}

	public Object accept(SingleTableSubclass arg0) {
		return null;
	}

	public Object accept(JoinedSubclass arg0) {
		return null;
	}

	public Object accept(Subclass arg0) {
		return null;
	}

}
