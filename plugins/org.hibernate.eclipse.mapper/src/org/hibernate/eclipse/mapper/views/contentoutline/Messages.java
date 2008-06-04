package org.hibernate.eclipse.mapper.views.contentoutline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.mapper.views.contentoutline.messages"; //$NON-NLS-1$
	public static String XML_HIBERNATE_TOOLS;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
