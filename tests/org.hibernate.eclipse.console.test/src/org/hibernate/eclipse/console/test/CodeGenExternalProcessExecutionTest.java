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
package org.hibernate.eclipse.console.test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.EclipseLaunchConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.project.LaunchConfigTestProject2;
import org.hibernate.eclipse.console.test.utils.ResourceReadUtils;

import junit.framework.TestCase;

/**
 * Execute codegeneration launch configuration in external process,
 * to check the generation is successful.
 * Execute codegeneration launch configuration in internal process,
 * to check the generation is successful.
 * Compare generated results - should be the same.
 * Currently both tests are fail, should be success 
 * when JBIDE-7441 be fixed.
 * 
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class CodeGenExternalProcessExecutionTest extends TestCase {

	private ConsoleConfiguration consoleCfg;
	private LaunchConfigTestProject2 project;
	private LaunchManager launchManager = new LaunchManager();

	public CodeGenExternalProcessExecutionTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		this.project = new LaunchConfigTestProject2();

		final String fileNameConsoleConfig = LaunchConfigTestProject2.LAUNCH_CONSOLE_CONFIG_TEST_FILE;
		ILaunchConfiguration launchConfig = loadLaunchConfigFromFile(fileNameConsoleConfig);
		final EclipseLaunchConsoleConfigurationPreferences cfgprefs =
			new EclipseLaunchConsoleConfigurationPreferences(launchConfig);
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		KnownConfigurations.getInstance().addConfiguration(consoleCfg, true);
	}

	protected void tearDown() throws Exception {
		consoleCfg.reset();
		KnownConfigurations.getInstance().removeAllConfigurations();
		consoleCfg = null;
		//
		Exception ex = null;
		for (int i = 0; i < 4; i++) {
			ex = null;
			try {
				this.project.deleteIProject();
				i = 4;
			} catch (Exception e) {
				ex = e;
				if (i < 3) {
					Thread.sleep(3000);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
		this.project = null;
	}
	
	public void testExecuteExternalLaunchConfig() {
		IFolder testFolder = project.getTestFolder();
		int nTest = -1;
		try {
			nTest = testFolder.members().length;
		} catch (CoreException e) {
		}
		assertEquals(0, nTest);
		ILaunchConfiguration launchConfig = null;
		ILaunchConfigurationWorkingCopy launchConfigWC = null;
		//
		final String fileNameCodeGenExtern = LaunchConfigTestProject2.LAUNCH_CODE_GEN_TEST_FILE_EXTERN;
		launchConfig = loadLaunchConfigFromFile(fileNameCodeGenExtern);
		launchConfigWC = null;
		try {
			launchConfigWC = launchConfig.getWorkingCopy();
		} catch (CoreException e) {
		}
		assertNotNull(launchConfigWC);
		launchConfigWC.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		DebugUIPlugin.launchInForeground(launchConfigWC, ILaunchManager.RUN_MODE);
		nTest = -1;
		try {
			nTest = testFolder.members().length;
		} catch (CoreException e) {
		}
		// TODO: uncomment when JBIDE-7441 be fixed
		//assertTrue(nTest > 0);
		//
		final String fileNameCodeGenIntern = LaunchConfigTestProject2.LAUNCH_CODE_GEN_TEST_FILE_INTERN;
		launchConfig = loadLaunchConfigFromFile(fileNameCodeGenIntern);
		launchConfigWC = null;
		try {
			launchConfigWC = launchConfig.getWorkingCopy();
		} catch (CoreException e) {
		}
		assertNotNull(launchConfigWC);
		launchConfigWC.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		DebugUIPlugin.launchInForeground(launchConfigWC, ILaunchManager.RUN_MODE);
		nTest = -1;
		try {
			nTest = testFolder.members().length;
		} catch (CoreException e) {
		}
		assertTrue(nTest > 0);
	}
	
	public void testExecute2LaunchConfigs_External_and_Internal() {
		//
		ILaunchConfiguration launchConfig = null;
		ILaunchConfigurationWorkingCopy launchConfigWC = null;
		IFolder testFolderAllExportersExternal = null;
		IFolder testFolderAllExportersInternal = null;
		int nTest = -1;
		//
		testFolderAllExportersExternal = project.getTestFolderAllExportersExternal();
		nTest = -1;
		try {
			nTest = testFolderAllExportersExternal.members().length;
		} catch (CoreException e) {
		}
		assertEquals(0, nTest);
		//
		final String fileNameCodeGenExtern = LaunchConfigTestProject2.LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_EXTERN;
		launchConfig = loadLaunchConfigFromFile(fileNameCodeGenExtern);
		launchConfigWC = null;
		try {
			launchConfigWC = launchConfig.getWorkingCopy();
		} catch (CoreException e) {
		}
		assertNotNull(launchConfigWC);
		launchConfigWC.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		DebugUIPlugin.launchInForeground(launchConfigWC, ILaunchManager.RUN_MODE);
		nTest = -1;
		try {
			nTest = testFolderAllExportersExternal.members().length;
		} catch (CoreException e) {
		}
		assertTrue(nTest > 0);
		//
		testFolderAllExportersInternal = project.getTestFolderAllExportersInternal();
		nTest = -1;
		try {
			nTest = testFolderAllExportersInternal.members().length;
		} catch (CoreException e) {
		}
		assertEquals(0, nTest);
		//
		final String fileNameCodeGenIntern = LaunchConfigTestProject2.LAUNCH_CODE_GEN_TEST_FILE_ALL_EXPORTERS_INTERN;
		launchConfig = loadLaunchConfigFromFile(fileNameCodeGenIntern);
		launchConfigWC = null;
		try {
			launchConfigWC = launchConfig.getWorkingCopy();
		} catch (CoreException e) {
		}
		assertNotNull(launchConfigWC);
		launchConfigWC.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		DebugUIPlugin.launchInForeground(launchConfigWC, ILaunchManager.RUN_MODE);
		nTest = -1;
		try {
			nTest = testFolderAllExportersInternal.members().length;
		} catch (CoreException e) {
		}
		assertTrue(nTest > 0);
		//
		boolean res = compareFolders(testFolderAllExportersExternal, testFolderAllExportersInternal);
		assertTrue(res);
	}

	protected class ResComparator implements Comparator<IResource> {
		@Override
		public int compare(IResource o1, IResource o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
	
	public boolean compareFiles(IFile testFile1, IFile testFile2) {
		boolean res = false;
		InputStream is1 = null, is2 = null;
		try {
			is1 = testFile1.getContents();
		} catch (CoreException e) {
		}
		try {
			is2 = testFile2.getContents();
		} catch (CoreException e) {
		}
		if (is1 == null || is2 == null) {
			res = is1 == is2;
			if (!res) {
				res = false;
			}
			return res;
		}
		String str1 = ResourceReadUtils.readStream(is1);
		String str2 = ResourceReadUtils.readStream(is2);
		if (str1 == null || str2 == null) {
			res = str1 == str2;
			if (!res) {
				res = false;
			}
			return res;
		}
		res = 0 == str1.compareTo(str2);
		if (!res) {
			res = false;
		}
		return res;
	}
	
	public boolean compareFolders(IFolder testFolder1, IFolder testFolder2) {
		boolean res = false;
		IResource[] res1 = null, res2 = null;
		try {
			res1 = testFolder1.members();
		} catch (CoreException e) {
		}
		try {
			res2 = testFolder2.members();
		} catch (CoreException e) {
		}
		if (res1 == null || res2 == null) {
			return res1 == res2;
		}
		if (res1.length != res2.length) {
			return res;
		}
		final ResComparator cmp = new ResComparator();
		Arrays.sort(res1, cmp);
		Arrays.sort(res2, cmp);
		res = true;
		for (int i = 0; res && i < res1.length; i++) {
			if (0 != res1[i].getName().compareTo(res2[i].getName())) {
				res = false;
			}
			if (res1[i].getType() != res2[i].getType()) {
				res = false;
			}
			if (res && ((IResource.FOLDER & res1[i].getType()) == IResource.FOLDER)) {
				IFolder tf1 = (IFolder)res1[i];
				IFolder tf2 = (IFolder)res2[i];
				res = compareFolders(tf1, tf2);
			}
			//if (res && ((IResource.FILE & res1[i].getType()) == IResource.FILE)) {
			//	IFile tf1 = (IFile)res1[i];
			//	IFile tf2 = (IFile)res2[i];
			//	res = compareFiles(tf1, tf2);
			//}
		}
		return res;
	}
	
	protected LaunchConfigTestProject2 getProject() {
		return this.project;
	}

	public ILaunchConfiguration loadLaunchConfigFromFile(String fileName) {
		IPath path = new Path(fileName);
		IFile ifile = getProject().getIProject().getFile(path);
		ILaunchConfiguration launchConfig = launchManager.getLaunchConfiguration((IFile) ifile);
		return launchConfig;
	}
}
