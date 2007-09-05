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

import java.sql.Types;
import java.util.ResourceBundle;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ContentViewer;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;

public class OrmModelNameVisitor /*implements IOrmModelVisitor*/ {
	
	private ContentViewer viewer;

	static private String SPACE = " ";

	static private String POINTER = " -> ";

	private ResourceBundle BUNDLE = ResourceBundle
			.getBundle(OrmModelNameVisitor.class.getPackage().getName()
					+ ".views");

	public OrmModelNameVisitor(ContentViewer viewer) {
		super();
		this.viewer = viewer;
	}

	public Object visitDatabaseColumn(Column column, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(column.getName());

//		String s = JDBCToHibernateTypeHelper.getJDBCTypeName(column.getSqlTypeCode().intValue());
		
		return name.toString();

	}

	public Object visitPersistentClass(RootClass clazz, Object argument) {

		StringBuffer name = new StringBuffer();
		name.append(clazz.getEntityName() != null ? clazz.getEntityName() : clazz.getClassName());

		Table table = clazz.getTable(); // upd tau 06.06.2005
		if (table != null) {
			String tableName = HibernateUtils.getTableName(table);
			if (tableName != null) {
				name.append(POINTER);
				name.append(tableName);
			}
		}

		return name.toString();
	}

	public Object visitTable(Table table, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(HibernateUtils.getTableName(table));
		return name.toString();
	}

	public Object visitPersistentClass(Subclass clazz, Object argument) {

		StringBuffer name = new StringBuffer();
		name.append(clazz.getEntityName());

		Table table = clazz.getTable();
		if (table != null) {
			String tableName = HibernateUtils.getTableName(table);
			if (tableName != null) {
				name.append(POINTER);
				name.append(tableName);
			}
		}

		return name.toString();
	}

	public Object visitPersistentField(Property field, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(field.getName());
		name.append(BUNDLE.getString("OrmModelNameVisitor.Colon"));
		String typeString = null;
		try {
			if (field.getType().isEntityType()) {
				typeString =  ((EntityType)field.getType()).getAssociatedEntityName();
			} else {
				typeString = field.getType().getReturnedClass().getName();
			}
		} catch (Exception e) {
			if (field.getValue() instanceof Component) {
				typeString = ((Component)field.getValue()).getComponentClassName();
			}
		}
		if (typeString != null) {
			name.append(typeString);
		}
		
		return name.toString();

	}

	public Object visitCollectionKeyMapping(DependantValue mapping,	Object argument) {
		return "key";
	}

	public Object visitComponentMapping(Component mapping,	Object argument) {
		return "element";
	}
}