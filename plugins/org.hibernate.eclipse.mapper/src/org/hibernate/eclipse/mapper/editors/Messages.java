package org.hibernate.eclipse.mapper.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.mapper.editors.messages"; //$NON-NLS-1$
	public static String CHOOSCONSOLECONFIGURATIONDIALOG_CONSOLE_CONFIGURATION;
	public static String CHOOSCONSOLECONFIGURATIONDIALOG_SELECT_CONSOLE_CONFIGURATION;
	public static String HIBERNATECFGXMLEDITOR_CONFIGURATION;
	public static String HIBERNATECFGXMLEDITOR_COULD_NOT_CREATE_FORM_PART;
	public static String REVERSEENGINEERINGEDITOR_COULD_NOT_CREATE_GRAPHICAL_VIEWER;
	public static String REVERSEENGINEERINGEDITOR_ERROR_WHILE_REFRESHING_DATABASETREE;
	public static String REVERSEENGINEERINGEDITOR_NO_FILTERS_DEFINED;
	public static String REVERSEENGINEERINGEDITOR_NO_FILTERS_HAS_BEEN_DEFINED;
	public static String REVERSEENGINEERINGEDITOR_OVERVIEW;
	public static String REVERSEENGINEERINGEDITOR_TABLE_COLUMN;
	public static String REVERSEENGINEERINGEDITOR_TABLE_FILTERS;
	public static String REVERSEENGINEERINGEDITOR_TYPE_MAPPINGS;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
