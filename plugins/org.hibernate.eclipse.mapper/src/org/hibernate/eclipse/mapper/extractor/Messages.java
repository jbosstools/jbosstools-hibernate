package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.mapper.extractor.messages"; //$NON-NLS-1$
	public static String GENERATORTYPEHANDLER_GENERATOR_TYPE;
	public static String GENERATORTYPEHANDLER_RETURN_CLASS;
	public static String HBMINFOEXTRACTOR_ACCESS_FIELDS_DIRECTLY;
	public static String HBMINFOEXTRACTOR_DO_NOT_PERFORM_ANY_ACCESS;
	public static String HBMINFOEXTRACTOR_USE_JAVABEAN_ACCESSOR_METHODS;
	public static String HIBERANTETYPEHANDLER_HIBERNATE_TYPE;
	public static String HIBERANTETYPEHANDLER_RETURN_CLASS;
	public static String HIBERANTETYPEHANDLER_RETURN_PRIMITIVE;
	public static String PROPERTYACCESSHANDLER_ACCESS_METHOD;
	public static String PROPERTYACCESSHANDLER_DESCRIPTION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
