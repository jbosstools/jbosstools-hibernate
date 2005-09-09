package org.hibernate.eclipse.graph.model;

import java.util.Observable;

import org.hibernate.mapping.Column;

public class ColumnViewAdapter extends Observable {

	private final TableViewAdapter table;
	private final Column column;

	public ColumnViewAdapter(TableViewAdapter adapter, Column element) {
		this.table = adapter;
		// TODO Auto-generated constructor stub
		this.column = element;
	}

	public Column getcolumn() {
		return column;
	}

}
