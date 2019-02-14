package org.jboss.tools.hibernate.ui.xml;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.xml.messages"; //$NON-NLS-1$
	public static String HibConfig3CompoundEditor_SecurityLabel;
	public static String HibConfig3CompoundEditor_SecurityTitle;
	public static String HibConfig3CompoundEditor_SessionFactoryLabel;
	public static String HibConfig3CompoundEditor_SessionFactoryTitle;
	public static String Hibernate3CompoundEditor_HibernateXMLEditor;
	public static String HibernateUIXMLPlugin_NoExceptionMessage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
