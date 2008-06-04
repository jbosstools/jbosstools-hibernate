package org.hibernate.eclipse.mapper.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.mapper.model.messages"; //$NON-NLS-1$
	public static String DOMREVERSEENGINEERINGDEFINITION_UNKNOWN_CHANGE;
	public static String REVENGPRIMARYKEYADAPTER_COLUMN;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
