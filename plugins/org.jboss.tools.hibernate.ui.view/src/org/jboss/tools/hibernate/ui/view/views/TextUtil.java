package org.jboss.tools.hibernate.ui.view.views;

import org.hibernate.mapping.Table;

public class TextUtil {
	public static String getTableName(Table table) {
		String schema = table.getSchema();
		String catalog = table.getCatalog();
		return (schema != null ? schema + "." : "") + (catalog != null ? catalog + "." : "") + table.getName();
	}
}
