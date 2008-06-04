package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.veditor.editors.actions.messages"; //$NON-NLS-1$
	public static String EXPORTIMAGEACTION_BMP_FORMAT;
	public static String EXPORTIMAGEACTION_ERROR;
	public static String EXPORTIMAGEACTION_EXPORT_AS_IMAGE;
	public static String EXPORTIMAGEACTION_FAILED_TO_EXPORT_IMAGE;
	public static String EXPORTIMAGEACTION_JPG_FORMAT;
	public static String EXPORTIMAGEACTION_PNG_FORMAT;
	public static String OPENMAPPINGACTION_CANOT_FIND_OR_OPEN_MAPPING_FILE;
	public static String OPENMAPPINGACTION_OPEN_MAPPING_FILE;
	public static String OPENSOURCEACTION_CANOT_FIND_SOURCE_FILE;
	public static String OPENSOURCEACTION_CANOT_OPEN_SOURCE_FILE;
	public static String OPENSOURCEACTION_OPEN_SOURCE_FILE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
