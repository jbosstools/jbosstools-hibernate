/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaPackageInfo;

/**
 * @author Dmitry Geraskov
 *
 */
public interface PackageInfoRef {

	// ********** java persistent type **********

	/**
	 * String constant associated with changes to the java pacakge info
	 */
	final static String JAVA_PACKAGE_INFO_PROPERTY = "javaPackageInfo"; //$NON-NLS-1$

	/**
	 * Return the JavaPackageInfo that corresponds to this PackageInfoRef.
	 * This can be null.
	 * This is not settable by users of this API.
	 */
	JavaPackageInfo getJavaPackageInfo();

}
