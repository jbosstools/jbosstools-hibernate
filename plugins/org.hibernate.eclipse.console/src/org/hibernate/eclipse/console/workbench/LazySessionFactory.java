package org.hibernate.eclipse.console.workbench;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.node.ConfigurationEntitiesNode;

public class LazySessionFactory {

	private final ConsoleConfiguration ccfg;
	private ConfigurationEntitiesNode cfgNode;
	
	
	public LazySessionFactory(ConsoleConfiguration ccfg) {
		this.ccfg = ccfg;		
	}
	
	public ConsoleConfiguration getConsoleConfiguration() {
		return ccfg;
	}
	
	public ConfigurationEntitiesNode getCfgNode() {
		return cfgNode;
	}
	
	public void setCfgNode(ConfigurationEntitiesNode cfgNode) {
		this.cfgNode = cfgNode;
	}
}