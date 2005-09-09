package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;


public class TableViewAdapter extends GraphNode {

	private Table table;
	private final ConfigurationViewAdapter configuration;

	public TableViewAdapter(ConfigurationViewAdapter configuration, Table table) {
		this.configuration = configuration;
		this.table = table;
	}
	
	public void createAssociations() {
		
		
	}

	public Table getTable() {
		return table;
	}

	public List getColumns() {
		List result = new ArrayList();
		Iterator columnIterator = table.getColumnIterator();
		while ( columnIterator.hasNext() ) {
			Column element = (Column) columnIterator.next();
			result.add(new ColumnViewAdapter(this,element));
		}
		
		return result; 
	}

}
