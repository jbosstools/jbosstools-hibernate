package org.hibernate.eclipse.launch;

import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public interface IConsoleConfigurationLaunchConstants {

	public static final String ID = "org.hibernate.eclipse.launch"; //$NON-NLS-1$
	public static final String DIALECT = ID + "DIALECT"; //$NON-NLS-1$
	public static final String NAMING_STRATEGY = ID + "NAMING_STRATEGY"; //$NON-NLS-1$
	public static final String ENTITY_RESOLVER = ID + "ENTITY_RESOLVER"; //$NON-NLS-1$
	public static final String PROPERTY_FILE = ID + ".PROPERTY_FILE"; //$NON-NLS-1$
	public static final String CFG_XML_FILE = ID + ".CFG_XML_FILE"; //$NON-NLS-1$
	public static final String PERSISTENCE_UNIT_NAME = ID + ".PERSISTENCE_UNIT_NAME"; //$NON-NLS-1$
	public static final String CONFIGURATION_FACTORY = ID + ".CONFIGURATION_FACTORY"; //$NON-NLS-1$
	public static final String CONNECTION_PROFILE_NAME = ID + ".CONNECTION_PROFILE_NAME"; //$NON-NLS-1$
	public static final String USE_JPA_PROJECT_PROFILE = ID + ".USE_JPA_PROJECT_PROFILE"; //$NON-NLS-1$
	public static final String FILE_MAPPINGS = ID + ".FILE_MAPPINGS"; //$NON-NLS-1$
	public static final String PROJECT_NAME = IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

}
