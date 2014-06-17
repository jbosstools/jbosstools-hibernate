package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.spi.ITable;

public class TableProxy implements ITable {
	
	private Table target = null;
	
	public TableProxy(Table table) {
		target = table;
	}

	@Override
	public String getName() {
		return target.getName();
	}

}
