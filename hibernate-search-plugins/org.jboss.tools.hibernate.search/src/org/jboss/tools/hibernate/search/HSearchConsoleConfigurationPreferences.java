package org.jboss.tools.hibernate.search;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;

public class HSearchConsoleConfigurationPreferences {
	
	private static final String DEFAULT_HIBERNATE_SEARCH_VERSION = "4.3";
	
	//map, containing mapping between hibernate and hiberante-search verisions
	private static final Map<String, String> versionsMap  = new HashMap<String, String>() {
		{
			put("4.0", "4.0");
			put("4.3", "5.3");
			put("5.0", "5.5");
			put("5.1", "5.5.1");
			put("5.2", "5.7");
		}		
	}; 

	public static String getHSearchVersion(String consoleConfigName) {
		String hibernateVersion = getHibernateVersion(consoleConfigName);
		if (versionsMap.containsKey(hibernateVersion)) {
			return versionsMap.get(hibernateVersion);
		}
		return DEFAULT_HIBERNATE_SEARCH_VERSION;
	}
	
	private static String getHibernateVersion(String consoleConfigName) {
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(consoleConfigName);
		if (consoleConfig == null) {
			return null;
		}
		return consoleConfig.getPreferences().getHibernateVersion();
	}
}
