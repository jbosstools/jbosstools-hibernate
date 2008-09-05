package org.hibernate.console;

import org.eclipse.osgi.util.NLS;

public class ConsoleMessages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.console.ConsoleMessages"; //$NON-NLS-1$
	public static String ConsoleConfiguration_connection_profile_not_found;
	public static String ConsoleConfiguration_could_not_access;
	public static String ConsoleConfiguration_could_not_configure_entity_resolver;
	public static String ConsoleConfiguration_could_not_configure_naming_strategy;
	public static String ConsoleConfiguration_could_not_create_jpa_based_configuration;
	public static String ConsoleConfiguration_could_not_load_annotationconfiguration;
	public static String ConsoleConfiguration_could_not_load_jpa_configuration;
	public static String ConsoleConfiguration_could_not_parse_configuration;
	public static String ConsoleConfiguration_factory_not_closed_before_build_new_factory;
	public static String ConsoleConfiguration_invalid_configuration;
	public static String ConsoleConfiguration_persistence_unit_not_found;
	public static String ConsoleConfiguration_problems_while_loading_database_driverclass;
	public static String JavaPage_no_info;
	public static String JavaPage_not_allowed;
	public static String KnownConfigurations_could_not_write_state;
	public static String KnownConfigurations_hibernate_log;
	public static String KnownConfigurations_unknown;
	public static String DefaultExecutionContext_existing_classloader;
	public static String ClassNode_uninitialized_proxy;
	public static String ConfigurationNode_mapped_entities;
	public static String NodeFactory_unknown;
	public static String PersistentCollectionNode_could_not_access_property_value;
	public static String AbstractConsoleConfigurationPreferences_could_not_load_prop_file;
	public static String AbstractConsoleConfigurationPreferences_name_not_null_or_empty;
	public static String AbstractConsoleConfigurationPreferences_unknown;
	public static String StandAloneConsoleConfigurationPreferences_could_not_resolve_classpaths;
	public static String StandAloneConsoleConfigurationPreferences_errors_while_parsing;
	public static String PluginFileAppender_missing_plugin_state_location;
	public static String PluginLogAppender_missing_layout_for_appender;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ConsoleMessages.class);
	}

	private ConsoleMessages() {
	}
}
