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

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.eclipse.console.workbench.TypeNameValueVisitor;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OrmModelNameVisitor /*implements IOrmModelVisitor*/ {
	
	static private String SPACE = " ";
	static private String POINTER = " -> ";
	
	private ResourceBundle BUNDLE = ResourceBundle
			.getBundle(OrmModelNameVisitor.class.getPackage().getName()
					+ ".views");
	
	public OrmModelNameVisitor() {
		super();
	}
	
	private Map mappings = new HashMap();
	private Map dialects = new HashMap();

	public Object visitDatabaseColumn(Column column, Object argument) {

		String type = getColumnSqlType(column, argument);

		StringBuffer name = new StringBuffer();
		name.append(column.getName());

		if (type != null) {
			name.append(" [");
			name.append(type != null ? type.toUpperCase() : "");
			name.append(column.isNullable() ? " Nullable" : "");
			name.append(HibernateUtils.getTable(column) != null
					&& HibernateUtils.isPrimaryKey(column) ? " PK" : "");
			name.append(HibernateUtils.getTable(column) != null
					&& HibernateUtils.isForeignKey(column) ? " FK" : "");
			name.append("]");
		}

		return name.toString();

	}
	
	public String getColumnSqlType(Column column, Object argument) {
		
		Configuration cfg = null;
		Mapping mapping = null;
		Dialect dialect = null;

		if (argument instanceof Configuration) {
			
			cfg = (Configuration) argument;
			
			if (mappings.containsKey(cfg)) {
				mapping = (Mapping) mappings.get(cfg);
			} else {
				mapping = cfg.buildMapping();
				mappings.put(cfg, mapping);
			}

			try {
				String dialectName = cfg.getProperty(Environment.DIALECT);
				if (dialects.containsKey(dialectName)) {
					dialect = (Dialect) dialects.get(dialectName);
				} else {
					dialect = (Dialect) Class.forName(dialectName).newInstance();
					dialects.put(dialectName, dialect);
				}
			} catch (HibernateException e) {
				ViewPlugin.getDefault().logError(e);
			} catch (InstantiationException e) {
				ViewPlugin.getDefault().logError(e);
			} catch (IllegalAccessException e) {
				ViewPlugin.getDefault().logError(e);
			} catch (ClassNotFoundException e) {
				ViewPlugin.getDefault().logError(e);
			}
		}

		return column.getSqlType(dialect, mapping);
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
			typeString = field.getType().getReturnedClass().getName();
		} catch (Exception e) {
			if (field.getValue() instanceof Component) {
				typeString = ((Component)field.getValue()).getComponentClassName();
			} else if (field.getValue().isSimpleValue()) {
				typeString = ((SimpleValue)field.getValue()).getTypeName();
			}
		}
		
		
		
		if (typeString != null) {
			typeString = correctTypeString(typeString);
			name.append(SPACE);
			name.append(typeString);
		}
		
		Value value = field.getValue();
		String typeName = (String) value.accept(new TypeNameValueVisitor(false));
		
		if (typeName!=null) {
			return field.getName() + " : " + typeName;
		}
		
		return field.getName(); 
	}

	private String correctTypeString(String str) {
		String ret = str;
		while (ret.startsWith("[")) {
			ret = ret.substring(1).concat("[]");
		}
		switch (ret.toCharArray()[0]) {
		case 'Z': ret = "boolean".concat(ret.substring(1));break;
		case 'B': ret = "byte".concat(ret.substring(1));break;
		case 'C': ret = "char".concat(ret.substring(1));break;
		case 'L': ret = ret.substring(1);break;
		case 'D': ret = "double".concat(ret.substring(1));break;
		case 'F': ret = "float".concat(ret.substring(1));break;
		case 'I': ret = "int".concat(ret.substring(1));break;
		case 'J': ret = "long".concat(ret.substring(1));break;
		case 'S': ret = "short".concat(ret.substring(1));break;
		}
		return ret;
	}

	public Object visitCollectionKeyMapping(DependantValue mapping,	Object argument) {
		return "key";
	}

	public Object visitComponentMapping(Component mapping,	Object argument) {
		return "element";
	}
}