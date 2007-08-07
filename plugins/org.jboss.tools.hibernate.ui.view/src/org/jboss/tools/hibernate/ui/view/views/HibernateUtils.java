package org.jboss.tools.hibernate.ui.view.views;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public class HibernateUtils {
	
	public static String getTableName(Table table) {
		String schema = table.getSchema();
		String catalog = table.getCatalog();
		return (schema != null ? schema + "." : "") + (catalog != null ? catalog + "." : "") + table.getName();
	}
	
	public static boolean isPrimaryKey(Column column){
		Table table = getTable(column);
		if(table != null){
			if(table.getPrimaryKey() != null){
				if(table.getPrimaryKey().containsColumn(column)) return true;
			}
		}
		return false;
	}
	
	public static boolean isForeignKey(Column column){
		Table table = getTable(column);
		if(table != null){
			Iterator iter = table.getForeignKeyIterator();
			while(iter.hasNext()){
				ForeignKey fk = (ForeignKey)iter.next();
				if(fk.containsColumn(column)) return true;
			}
		}
		return false;
		
	}
	
	public static Table getTable(Column column){
		if(column.getValue() != null)
			return column.getValue().getTable();
		return null;
	}
}
