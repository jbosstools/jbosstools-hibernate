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
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.core.JptCommonCorePlugin;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.JpaResourceModelProvider;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.SourcePackageInfoCompilationUnit;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceCompilationUnit;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaPackageInfoResourceModelProviderPatched implements
		JpaResourceModelProvider {
	// singleton
	private static final JpaResourceModelProvider INSTANCE = new JavaPackageInfoResourceModelProviderPatched();

	/**
	 * Return the singleton.
	 */
	public static JpaResourceModelProvider instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private JavaPackageInfoResourceModelProviderPatched() {
		super();
	}

	public IContentType getContentType() {
		return JptCommonCorePlugin.JAVA_SOURCE_PACKAGE_INFO_CONTENT_TYPE;
	}

	public JavaResourceCompilationUnit buildResourceModel(
			JpaProject jpaProject, IFile file) {
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
		try {
			if (cu.getPackageDeclarations().length > 0){
				return new SourcePackageInfoCompilationUnit(
						cu,
						jpaProject.getJpaPlatform().getAnnotationProvider(),
						jpaProject.getJpaPlatform().getAnnotationEditFormatter(),
						jpaProject.getModifySharedDocumentCommandExecutor());
			} else {
				//ignore package-info placed in default package as
				//it doesn't have package declaration and can't hold annotations
				return null;
			}
		} catch (JavaModelException e) {
			// Ignore -- project is in a bad state. This will get recalled if necessary
			return null;
		}
	}

}
