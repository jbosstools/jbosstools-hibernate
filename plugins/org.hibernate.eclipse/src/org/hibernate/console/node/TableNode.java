package org.hibernate.console.node;

import org.hibernate.mapping.Table;

public class TableNode extends BaseNode {

	public TableNode(BaseNode parent, Table table) {
		super(null, parent);
		name = table.getName();
	}
	
	protected void checkChildren() {
		// TODO Auto-generated method stub
	}

	public String getHQL() {
		return null;
	}

}
