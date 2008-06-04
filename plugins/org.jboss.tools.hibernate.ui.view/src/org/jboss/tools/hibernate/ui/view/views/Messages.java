package org.jboss.tools.hibernate.ui.view.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.view.views.messages"; //$NON-NLS-1$
	public static String ORMLABELPROVIDER_ELEMENT;
	public static String ORMLABELPROVIDER_ORM_ELEMENT;
	public static String ORMLABELPROVIDER_UNKNOWN_TYPE_OF_ELEMENT_IN_TREE_OF_TYPE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
