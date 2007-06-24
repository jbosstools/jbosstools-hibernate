/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.internal.core.hibernate.automapping.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String HibernateAutoMappingHelper_ReportString1;

	public static String HibernateAutoMappingHelper_ReportString2;

	public static String HibernateAutoMappingHelper_ReportString3;

	public static String HibernateAutoMappingHelper_ReportString4;

	public static String HibernateAutoMappingHelper_ReportString5;

	public static String HibernateAutoMappingHelper_ReportString6;

	public static String HibernateAutoMappingHelper_ReportString7;

	public static String HibernateAutoMappingHelper_ReportString8;

	public static String HibernateAutoMappingHelper_ReportString9;

	public static String HibernateAutoMappingHelper_ReportString10;
}
