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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
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
					long projectJdkLevel = versionToJdkLevel(projectTarget);
					long eclipseJdkLevel = versionToJdkLevel(eclipseCompilerVersion);
					if (eclipseJdkLevel < projectJdkLevel){
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
	
	private static final String VERSION_JSR14 = "jsr14"; //$NON-NLS-1$
	private static final String VERSION_CLDC1_1 = "cldc1.1"; //$NON-NLS-1$

	private static long versionToJdkLevel(Object versionID) {
		if (versionID instanceof String) {
			String version = (String) versionID;
			// verification is optimized for all versions with same length and same "1." prefix
			if (version.length() == 3 && version.charAt(0) == '1' && version.charAt(1) == '.') {
				switch (version.charAt(2)) {
					case '1':
						return ClassFileConstants.JDK1_1;
					case '2':
						return ClassFileConstants.JDK1_2;
					case '3':
						return ClassFileConstants.JDK1_3;
					case '4':
						return ClassFileConstants.JDK1_4;
					case '5':
						return ClassFileConstants.JDK1_5;
					case '6':
						return ClassFileConstants.JDK1_6;
					case '7':
						return ClassFileConstants.JDK1_7;
					case '8':
						return ClassFileConstants.JDK1_8;
					default:
						return 0; // unknown
				}
			}
			if (VERSION_JSR14.equals(versionID)) {
				return ClassFileConstants.JDK1_4;
			}
			if (VERSION_CLDC1_1.equals(versionID)) {
				return ClassFileConstants.CLDC_1_1;
			}
		}
		return 0; // unknown
	}

	
}
