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

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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

import junit.framework.TestCase;

/**
 * Execute codegeneration launch configuration in external process,
 * to check the generation is successful.
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
