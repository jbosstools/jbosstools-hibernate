package org.hibernate.console.node;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.console.node.messages"; //$NON-NLS-1$
	public static String CLASSNODE_UNINITIALIZED_PROXY;
	public static String CONFIGURATIONNODE_MAPPED_ENTITIES;
	public static String NODEFACTORY_UNKNOWN;
	public static String PERSISTENTCOLLECTIONNODE_COULD_NOT_ACCESS_PROPERTY_VALUE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
