package org.hibernate.eclipse.console.workbench;

import org.hibernate.console.ConsoleConfiguration;

public class LazyDatabaseSchema {

	private final ConsoleConfiguration ccfg;

	public LazyDatabaseSchema(ConsoleConfiguration ccfg) {
		this.ccfg = ccfg;
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		return ccfg;
	}
		
}
