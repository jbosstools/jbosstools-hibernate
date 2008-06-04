package org.jboss.tools.hibernate.ui.veditor.editors.command;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.veditor.editors.command.messages"; //$NON-NLS-1$
	public static String SHAPESETCONSTRAINTCOMMAND_MOVE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
