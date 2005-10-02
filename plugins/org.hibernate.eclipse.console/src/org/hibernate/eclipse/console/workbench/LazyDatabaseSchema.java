package org.hibernate.eclipse.console.workbench;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.ConsoleConfiguration;

public class LazyDatabaseSchema {

	private final ConsoleConfiguration ccfg;
	private final ReverseEngineeringStrategy res;
	
	public LazyDatabaseSchema(ConsoleConfiguration ccfg) {
		this(ccfg, new DefaultReverseEngineeringStrategy());
	}

	public LazyDatabaseSchema(ConsoleConfiguration ccfg, ReverseEngineeringStrategy res) {
		this.ccfg = ccfg;
		this.res = res;
	}
	public ConsoleConfiguration getConsoleConfiguration() {
		return ccfg;
	}

	public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return res;
	}
		
}
