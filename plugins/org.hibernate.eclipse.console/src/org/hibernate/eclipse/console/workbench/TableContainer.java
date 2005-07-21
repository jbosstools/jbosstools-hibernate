package org.hibernate.eclipse.console.workbench;

import java.util.List;

public class TableContainer {

	private final List tables;
	private final String name;

	public TableContainer(String name, List tables) {
		this.tables = tables;
		this.name = name;
	}

	public List getTables() {
		return tables;
	}

	public String getName() {
		return name;
	}

}
