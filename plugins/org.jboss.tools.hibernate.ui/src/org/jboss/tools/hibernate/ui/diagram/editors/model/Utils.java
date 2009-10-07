/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.model;

import java.util.List;
import java.util.Properties;

import org.eclipse.ui.IMemento;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;

/**
 * Some common model utils.
 * 
 * @author vitali
 */
public class Utils {
	
	public static boolean isConnectionExist(Shape source, Shape target) {
		boolean res = false;
		if (source != null && target != null) {
			if (source.equals(target)) {
				res = true;
			} else {
				List<Connection> sourceConnections = source.getSourceConnections();
				for (int i = 0; !res && i < sourceConnections.size(); i++) {
					Connection conn = sourceConnections.get(i);
					if (conn.getTarget().equals(target)) {
						res = true;
					}
				}
			}
		}
		return res;
	}

	public static String getName(Object obj) {
		String res = ""; //$NON-NLS-1$
		if (obj instanceof PersistentClass) {
			PersistentClass rootClass = (PersistentClass)obj;
			if (rootClass.getEntityName() != null) {
				res = rootClass.getEntityName();
			} else {
				res = rootClass.getClassName();
			}
		} else if (obj instanceof Table) {
			res = getTableName((Table)obj);
		} else if (obj instanceof Property) {
			Property property = (Property)obj;
			res = property.getPersistentClass().getEntityName() + "." + property.getName(); //$NON-NLS-1$
		} else if (obj instanceof SimpleValue) {
			SimpleValue sv = (SimpleValue)obj;
			res = getTableName(sv.getTable()) + "." + sv.getForeignKeyName(); //$NON-NLS-1$
		} else if (obj instanceof String) {
			res = (String)obj;
		}
		if (res.length() > 0 && res.indexOf(".") < 0) { //$NON-NLS-1$
			return "default." + res; //$NON-NLS-1$
		}
		if (res.length() == 0) {
			res = "null"; //$NON-NLS-1$
		}
		return res;
	}
	
	public static String getTableName(String catalog, String schema, String name) {
		return (catalog != null ? catalog + "." : "") + (schema != null ? schema + "." : "") + name; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public static String getTableName(Table table) {
		return getTableName(table.getCatalog(), table.getSchema(), table.getName());
	}

	////////////////////////////////////////////
	
	public static void setPropertyValue(IMemento memento, String key, boolean value) {
        memento.putString(key, Boolean.valueOf(value).toString());
	}
	
	public static boolean getPropertyValue(IMemento memento, String key, boolean def) {
		String str = memento.getString(key);
		if (str == null) {
			str = Boolean.toString(def);
		}
		return Boolean.valueOf(str).booleanValue();
	}

	public static void setPropertyValue(Properties properties, String key, boolean value) {
		if (properties.containsKey(key)) {
			properties.remove(key);
		}
		properties.put(key, Boolean.valueOf(value).toString());
	}
	
	public static boolean getPropertyValue(Properties properties, String key, boolean def) {
		String str = properties.getProperty(key, Boolean.toString(def));
		return Boolean.valueOf(str).booleanValue();
	}

	///
	
	public static double getPropertyValue(IMemento memento, String key, double def) {
		String str = memento.getString(key);
		if (str == null) {
			str = Double.toString(def);
		}
		return Double.valueOf(str).doubleValue();
	}
	
	public static double getPropertyValue(Properties properties, String key, double def) {
		String str = properties.getProperty(key, Double.toString(def));
		return Double.valueOf(str).doubleValue();
	}
	
	///
	
	public static int getPropertyValue(IMemento memento, String key, int def) {
		String str = memento.getString(key);
		if (str == null) {
			str = Integer.toString(def);
		}
		return Integer.valueOf(str).intValue();
	}
	
	public static int getPropertyValue(Properties properties, String key, int def) {
		String str = properties.getProperty(key, Integer.toString(def));
		return Integer.valueOf(str).intValue();
	}
	
	///
	
	public static String getPropertyValue(IMemento memento, String key, String def) {
		String str = memento.getString(key);
		if (str == null) {
			str = def;
		}
		return str;
	}

	public static void setPropertyValue(IMemento memento, String key, String value) {
        memento.putString(key, value);
	}
	
	public static String getPropertyValue(Properties properties, String key, String def) {
		return properties.getProperty(key, def);
	}

	public static void setPropertyValue(Properties properties, String key, String value) {
		if (properties.containsKey(key)) {
			properties.remove(key);
		}
		if (value != null) {
			properties.put(key, value);
		}
	}
}
