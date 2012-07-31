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
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class UIMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.jpt.core.internal.context.Messages";//$NON-NLS-1$

	public static String SYNC_CLASSES_JOB;
	
	public static String SYNC_CLASSES_TASK;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
	}
}
