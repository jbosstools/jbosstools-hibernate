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
package org.jboss.tools.hibernate.jpt.core.internal.libval;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jpt.common.core.internal.libval.LibValUtil;
import org.eclipse.jpt.common.core.libprov.JptLibraryProviderInstallOperationConfig;
import org.eclipse.jpt.common.core.libval.LibraryValidator;
import org.eclipse.jpt.common.utility.internal.iterables.TransformationIterable;
import org.eclipse.jpt.jpa.core.internal.libprov.JpaUserLibraryProviderInstallOperationConfig;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateUserLibraryValidator implements LibraryValidator {

	@Override
	public IStatus validate(JptLibraryProviderInstallOperationConfig config) {
		JpaUserLibraryProviderInstallOperationConfig jpaConfig = (JpaUserLibraryProviderInstallOperationConfig) config;
		Set<String> classNames = new HashSet<String>();
		//classNames.add("javax.persistence.Entity"); //$NON-NLS-1$
		classNames.add("org.hibernate.SessionFactory");//$NON-NLS-1$

		Iterable<IPath> libraryPaths = new TransformationIterable<IClasspathEntry, IPath>(
				jpaConfig.resolve()) {
			@Override
			protected IPath transform(IClasspathEntry o) {
				return o.getPath();
			}
		};

		return LibValUtil.validate(libraryPaths, classNames);
	}

}
