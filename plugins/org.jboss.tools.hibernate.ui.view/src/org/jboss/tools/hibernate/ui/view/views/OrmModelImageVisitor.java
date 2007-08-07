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

import java.util.Iterator;
import java.util.ResourceBundle;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UnionSubclass;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

import sun.text.CompactShortArray;



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
			try {
				if (field.getType().isCollectionType()) {
					return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldCollection")); //$NON-NLS-1$		
				}
			} catch (Exception e) {}
		}
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$		
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
		return ViewPlugin.getImageDescriptor(BUNDLE.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
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
