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
package org.jboss.tools.hibernate.internal.core.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.internal.core.data.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ColumnPropertyDescriptorsHolder_GeneralCategory;

	public static String ColumnPropertyDescriptorsHolder_NullableN;

	public static String ColumnPropertyDescriptorsHolder_NullableD;

	public static String ColumnPropertyDescriptorsHolder_UniqueN;

	public static String ColumnPropertyDescriptorsHolder_UniqueD;

	public static String ColumnPropertyDescriptorsHolder_LengthN;

	public static String ColumnPropertyDescriptorsHolder_LengthD;

	public static String ColumnPropertyDescriptorsHolder_PrecisionN;

	public static String ColumnPropertyDescriptorsHolder_PrecisionD;

	public static String ColumnPropertyDescriptorsHolder_ScaleN;

	public static String ColumnPropertyDescriptorsHolder_ScaleD;

	public static String ColumnPropertyDescriptorsHolder_SqlTypeNameN;

	public static String ColumnPropertyDescriptorsHolder_SqlTypeNameD;

	public static String ColumnPropertyDescriptorsHolder_NameN;

	public static String ColumnPropertyDescriptorsHolder_NameD;

	public static String ColumnPropertyDescriptorsHolder_CheckConstraintN;

	public static String ColumnPropertyDescriptorsHolder_CheckConstraintD;
}
