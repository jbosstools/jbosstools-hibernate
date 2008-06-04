package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.jdt.ui.internal.messages"; //$NON-NLS-1$
	public static String SAVEQUERYEDITORLISTENER_REPLACEQUESTION;
	public static String SAVEQUERYEDITORLISTENER_REPLACETITLE;
	public static String SAVEQUERYEDITORLISTENER_HQL_EDITOR;
	public static String SAVEQUERYEDITORLISTENER_CRI_EDITOR;
	public static String SAVEQUERYEDITORLISTENER_CHANGE_NAME;
	public static String SAVEQUERYEDITORLISTENER_COMPOSITE_CHANGE_NAME;
	public static String SAVEQUERYEDITORLISTENER_REFACTORINGTITLE;
	public static String SAVEQUERYEDITORLISTENER_REPLACETITLE_INFO;
	public static String SAVEQUERYEDITORLISTENER_REPLACEQUESTION_CONFIRM;
	public static String SAVEQUERYEDITORLISTENER_ERRORMESSAGE;
	public static String CRITERIAQUICKASSISTPROCESSOR_COPY_TO_CRITERIA_EDITOR;
	public static String CRITERIAQUICKASSISTPROCESSOR_ERRORMESSAGE;
	public static String DEBUGJAVACOMPLETIONPROPOSALCOMPUTER_DISPLAYSTRING;
	public static String HQLJAVACOMPLETIONPROPOSALCOMPUTER_ERRORMESSAGE;
	public static String HQLQUICKASSISTPROCESSOR_COPY_TO_HQL_EDITOR;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
