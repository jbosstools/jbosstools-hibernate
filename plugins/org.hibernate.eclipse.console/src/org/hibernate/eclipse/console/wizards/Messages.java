/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.wizards;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.console.wizards.messages"; //$NON-NLS-1$
	public static String ConnectionProfile;
	public static String SelectConnectionProfile;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
