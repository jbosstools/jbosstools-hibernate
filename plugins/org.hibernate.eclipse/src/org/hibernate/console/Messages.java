package org.hibernate.console;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.console.messages"; //$NON-NLS-1$
	public static String CONSOLECONFIGURATION_COULD_NOT_ACCESS;
	public static String CONSOLECONFIGURATION_COULD_NOT_CONFIGURE_ENTITY_RESOLVER;
	public static String CONSOLECONFIGURATION_COULD_NOT_CONFIGURE_NAMING_STRATEGY;
	public static String CONSOLECONFIGURATION_COULD_NOT_CREATE_JPA_BASED_CONFIGURATION;
	public static String CONSOLECONFIGURATION_COULD_NOT_LOAD_ANNOTATIONCONFIGURATION;
	public static String CONSOLECONFIGURATION_COULD_NOT_LOAD_JPA_CONFIGURATION;
	public static String CONSOLECONFIGURATION_COULD_NOT_PARSE_CONFIGURATION;
	public static String CONSOLECONFIGURATION_FACTORY_NOT_CLOSED_BEFORE_BUILD_NEW_FACTORY;
	public static String CONSOLECONFIGURATION_INVALID_CONFIGURATION;
	public static String CONSOLECONFIGURATION_PERSISTENCE_UNIT_NOT_FOUND;
	public static String CONSOLECONFIGURATION_PROBLEMS_WHILE_LOADING_DATABASE_DRIVERCLASS;
	public static String JAVAPAGE_NO_INFO;
	public static String JAVAPAGE_NOT_ALLOWED;
	public static String KNOWNCONFIGURATIONS_COULD_NOT_WRITE_STATE;
	public static String KNOWNCONFIGURATIONS_HIBERNATE_LOG;
	public static String KNOWNCONFIGURATIONS_UNKNOWN;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
