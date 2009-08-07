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

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
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
			List<Connection> sourceConnections = source.getSourceConnections();
			for (int i = 0; !res && i < sourceConnections.size(); i++) {
				Connection conn = sourceConnections.get(i);
				if (conn.getTarget().equals(target)) {
					res = true;
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
		} else if (obj instanceof String) {
			res = (String)obj;
		}
		if (res.length() > 0 && res.indexOf(".") < 0) { //$NON-NLS-1$
			return "default." + res; //$NON-NLS-1$
		}
		return res;
	}
	
	public static String getTableName(String catalog, String schema, String name) {
		return (catalog != null ? catalog + "." : "") + (schema != null ? schema + "." : "") + name; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public static String getTableName(Table table) {
		return getTableName(table.getCatalog(), table.getSchema(), table.getName());
	}
}
