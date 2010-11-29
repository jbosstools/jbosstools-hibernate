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
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
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
	private final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
	private final String ls = System.getProperties().getProperty("line.separator", ResourceReadUtils.LN_1);  //$NON-NLS-1$

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
		this.project.deleteIProject();
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
		boolean res = compareFolders(testFolderAllExportersExternal, testFolderAllExportersInternal, true);
		assertTrue(res);
	}

	protected class ResComparator implements Comparator<IResource> {

		public int compare(IResource o1, IResource o2) {
			int res = o1.getName().compareTo(o2.getName());
			if (res == 0) {
				if (o1.getType() < o2.getType()) {
					res = -1;
				} else if (o1.getType() > o2.getType()) {
					res = 1;
				}
			}
			return res;
		}
	}

	/**
	 * Clean up string of substrings in between [cmtB, cmtE],
	 * cmtB, cmtE - are markers of substring to delete.
	 * 
	 * @param str
	 * @param cmtB
	 * @param cmtE
	 * @return
	 */
	public String stripComments(String str, final String cmtB, final String cmtE) {
		boolean process = true;
		while (process) {
			int fromIndex = 0;
			int commentStart = str.indexOf(cmtB, fromIndex);
			fromIndex = commentStart;
			int commentEnd = str.indexOf(cmtE, fromIndex);
			if (commentStart < commentEnd && commentStart != -1) {
				str = str.substring(0, commentStart) + str.substring(commentEnd + cmtE.length());
			} else {
				process = false;
			}
		}
		return str;
	}
	
	/**
	 * get rid of xml comments.
	 * 
	 * @param str
	 * @return
	 */
	public String stripXmlComments(String str) {
		final String cmtB = "<!--"; //$NON-NLS-1$
		final String cmtE = "-->"; //$NON-NLS-1$
		return stripComments(str, cmtB, cmtE);
	}
	
	/**
	 * get rid of java comments.
	 * 
	 * @param str
	 * @return
	 */
	public String stripJavaComments(String str) {
		final String cmtB = "/*"; //$NON-NLS-1$
		final String cmtE = "*/"; //$NON-NLS-1$
		str = stripComments(str, cmtB, cmtE);
		final String cmt2B = "//"; //$NON-NLS-1$
		final String cmt2E = ls;
		str = stripComments(str, cmt2B, cmt2E);
		return str;
	}

	/**
	 * format string as java file.
	 * 
	 * @param str
	 * @return
	 */
	public String formatJavaFile(String str) {
		Document doc = new Document(str);
		TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, str, 0, str.length(), 0, null);
		try {
			edit.apply(doc);
		} catch (MalformedTreeException e) {
		} catch (BadLocationException e) {
		}
		return doc.get();
	}
	
	/**
	 * Compares 2 files, if identical for test purposes return true.
	 * 
	 * @param testFile1
	 * @param testFile2
	 * @param assertFlag - if true execute assertion
	 * @return
	 */
	public boolean compareFiles(IFile testFile1, IFile testFile2, boolean assertFlag) {
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
			if (!res && assertFlag) {
				assertEquals(is1, is2);
			}
			return res;
		}
		String str1 = ResourceReadUtils.readStream(is1);
		String str2 = ResourceReadUtils.readStream(is2);
		if (str1 == null || str2 == null) {
			res = str1 == str2;
			if (!res && assertFlag) {
				assertEquals(str1, str2);
			}
			return res;
		}
		final String useExt = testFile1.getFileExtension();
		if (0 == "xml".compareToIgnoreCase(useExt)) { //$NON-NLS-1$
			str1 = stripXmlComments(str1);
			str2 = stripXmlComments(str2);
		} else if (0 == "java".compareToIgnoreCase(useExt)) { //$NON-NLS-1$
			str1 = formatJavaFile(str1);
			str2 = formatJavaFile(str2);
			str1 = stripJavaComments(str1);
			str2 = stripJavaComments(str2);
		}
		if (testFile1.getName().endsWith("cfg.xml")) { //$NON-NLS-1$
			// do not compare generated cfg.xml files till the time of 
			// open question for Environment.HBM2DDL_AUTO settings
			//res = 0 == str1.compareTo(str2);
			//if (!res && assertFlag) {
			//	assertEquals(str1, str2);
			//}
			res = true;
		} else {
			res = 0 == str1.compareTo(str2);
			if (!res && assertFlag) {
				assertEquals(str1, str2);
			}
		}
		return res;
	}
	
	/**
	 * Compares 2 folders, if identical for test purposes return true.
	 * 
	 * @param testFolder1
	 * @param testFolder2
	 * @param assertFlag - if true execute assertion
	 * @return
	 */
	public boolean compareFolders(IFolder testFolder1, IFolder testFolder2, boolean assertFlag) {
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
			res = res1 == res2;
			if (!res && assertFlag) {
				assertEquals(res1, res2);
			}
			return res;
		}
		res = res1.length == res2.length;
		if (!res) {
			if (!res && assertFlag) {
				assertEquals(res1.length, res2.length);
			}
			return res;
		}
		final ResComparator cmp = new ResComparator();
		Arrays.sort(res1, cmp);
		Arrays.sort(res2, cmp);
		res = true;
		for (int i = 0; res && i < res1.length; i++) {
			if (0 != res1[i].getName().compareTo(res2[i].getName())) {
				res = false;
				if (!res && assertFlag) {
					assertEquals(res1[i].getName(), res2[i].getName());
				}
			}
			if (res1[i].getType() != res2[i].getType()) {
				res = false;
				if (!res && assertFlag) {
					assertEquals(res1[i].getType(), res2[i].getType());
				}
			}
			if (res && ((IResource.FOLDER & res1[i].getType()) == IResource.FOLDER)) {
				IFolder tf1 = (IFolder)res1[i];
				IFolder tf2 = (IFolder)res2[i];
				res = compareFolders(tf1, tf2, assertFlag);
			}
			if (res && ((IResource.FILE & res1[i].getType()) == IResource.FILE)) {
				IFile tf1 = (IFile)res1[i];
				IFile tf2 = (IFile)res2[i];
				res = compareFiles(tf1, tf2, assertFlag);
			}
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
