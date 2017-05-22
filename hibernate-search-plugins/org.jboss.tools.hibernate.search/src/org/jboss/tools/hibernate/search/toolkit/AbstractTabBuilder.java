package org.jboss.tools.hibernate.search.toolkit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;

public abstract class AbstractTabBuilder {
	
	protected AbstractTabBuilder() {
	}
	
	protected final Map<String, Composite> consoleConfigTab = new HashMap<String, Composite>();
	
	public Composite getTab(CTabFolder folder, ConsoleConfiguration consoleConfig) {
		final String consoleConfigName = consoleConfig.getName();
		if (consoleConfigTab.containsKey(consoleConfigName)) {
			return consoleConfigTab.get(consoleConfigName);
		}
		Composite newTab = buildTab(folder, consoleConfig);
		consoleConfigTab.put(consoleConfigName, newTab);
		return newTab;
	}
	
	abstract protected Composite buildTab(CTabFolder folder, ConsoleConfiguration consoleConfig) ;
	
	public void disposeAll() {
		for (Composite c: consoleConfigTab.values()) {
			c.dispose();
		}
		consoleConfigTab.clear();
	}
}
