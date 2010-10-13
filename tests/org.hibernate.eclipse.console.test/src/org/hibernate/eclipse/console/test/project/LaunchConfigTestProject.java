/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.hibernate.eclipse.console.test.utils.ResourceReadUtils;

/**
 * @author Vitali Yemialyanchyk
 */
public class LaunchConfigTestProject extends TestProject {
	
	public static final String TEST_TMP_OUT_FOLDER = "temp_test_out_folder"; //$NON-NLS-1$
	public static final String PROJECT_PATH = "res/project/"; //$NON-NLS-1$
	public static final String LAUNCH_TEST_FILE_0 = "testLaunchCfg_0.launch"; //$NON-NLS-1$
	public static final String LAUNCH_TEST_FILE_1 = "testLaunchCfg_1.launch"; //$NON-NLS-1$

	public LaunchConfigTestProject() {
		super("LaunchConfigTestProject"); //$NON-NLS-1$
	}

	public LaunchConfigTestProject(String projectName) {
		super(projectName);
	}
	
	protected void buildProject() throws JavaModelException, CoreException, IOException {
		super.buildProject();
		IPackageFragmentRoot tst = createFolder(TEST_TMP_OUT_FOLDER);
		tst.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		importLaunchConfigFileToProject(LAUNCH_TEST_FILE_0);
		importLaunchConfigFileToProject(LAUNCH_TEST_FILE_1);
	}

	public String getSample(String fileName) {
		return ResourceReadUtils.getSample(PROJECT_PATH + fileName);
	}

	public void importLaunchConfigFileToProject(String fileName) throws CoreException {
		getIProject().getFile(fileName).create(
			new ByteArrayInputStream(getSample(fileName).getBytes()),
			false, new NullProgressMonitor());
	}
}

