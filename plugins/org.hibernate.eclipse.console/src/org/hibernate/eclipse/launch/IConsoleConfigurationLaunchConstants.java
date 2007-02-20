package org.hibernate.eclipse.launch;

import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public interface IConsoleConfigurationLaunchConstants {

	public static final String ID = "org.hibernate.eclipse.launch";
	public static final String NAMING_STRATEGY = ID + "NAMING_STRATEGY";
	public static final String ENTITY_RESOLVER = ID + "ENTITY_RESOLVER";
	public static final String PROPERTY_FILE = ID + ".PROPERTY_FILE";
	public static final String CFG_XML_FILE = ID + ".CFG_XML_FILE";
	public static final String PERSISTENCE_UNIT_NAME = ID + ".PERSISTENCE_UNIT_NAME";
	public static final String CONFIGURATION_FACTORY = ID + ".CONFIGURATION_FACTORY";
	public static final String FILE_MAPPINGS = ID + ".FILE_MAPPINGS";
	public static final String PROJECT_NAME = IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

}
