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
package org.hibernate.eclipse.console.workbench;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.ProjectUtils;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class ProjectCompilerVersionChecker {
	
	/**
	 * 
	 * @param ccfg
	 * @return false if Projects jdk version is bigger than Eclipse jdk version
	 */
	public static boolean validateProjectComplianceLevel(final ConsoleConfiguration ccfg){
		IJavaProject[] javaProjects = ProjectUtils.findJavaProjects(ccfg);
		if (javaProjects.length > 0){
			for (final IJavaProject iJavaProject : javaProjects) {
				if (iJavaProject.exists()) {
					String projectTarget = iJavaProject.getOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, true);
					String eclipseCompilerVersion = System.getProperty("java.specification.version"); //$NON-NLS-1$
					long projectJdkLevel = CompilerOptions.versionToJdkLevel(projectTarget);
					long eclipseJdkLevel = CompilerOptions.versionToJdkLevel(eclipseCompilerVersion);
					if (eclipseJdkLevel <= projectJdkLevel){
						Display.getDefault().syncExec(new Runnable(){
							@Override
							public void run() {
								MessageDialog.openWarning(null, Messages.ProjectCompilerVersionChecker_title, 
										NLS.bind(Messages.ProjectCompilerVersionChecker_message, iJavaProject.getElementName()));
							}
						});
						return false;
					}
				}
			}
		}
		return true;
	}
}
