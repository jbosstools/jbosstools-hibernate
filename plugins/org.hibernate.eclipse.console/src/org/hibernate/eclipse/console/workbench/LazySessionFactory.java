package org.hibernate.eclipse.console.workbench;

import org.hibernate.console.ConsoleConfiguration;

public class LazySessionFactory {

	private final ConsoleConfiguration ccfg;
	
	public LazySessionFactory(ConsoleConfiguration ccfg) {
		this.ccfg = ccfg;		
	}
	
	public ConsoleConfiguration getConsoleConfiguration() {
		return ccfg;
	}
	
}