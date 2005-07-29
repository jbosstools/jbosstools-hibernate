package org.hibernate.eclipse.console;

import org.eclipse.osgi.util.NLS;

public class Messages {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.console.messages"; //$NON-NLS-1$


	public static String popup_copy_text;
	public static String popup_paste_text;
	public static String popup_select_all;
	public static String find_replace_action_label;
	public static String find_replace_action_tooltip;
	public static String find_replace_action_image;
	public static String find_replace_action_description;

	private Messages() { 
		// noop		
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
