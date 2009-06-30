/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateUIMappingMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.messages"; //$NON-NLS-1$
		
	public static String GenericGeneratorComposite_name;
	public static String GenericGeneratorComposite_strategy;

	private HibernateUIMappingMessages() {}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, HibernateUIMappingMessages.class);
	}
}
