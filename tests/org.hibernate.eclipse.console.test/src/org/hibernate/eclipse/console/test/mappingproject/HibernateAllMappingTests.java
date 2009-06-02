/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.utils.ProjectUtils;

public class HibernateAllMappingTests extends TestCase {

	private ConfigurableTestProject project;

	private static IPackageFragment activePackage;

	public HibernateAllMappingTests(String name) {
		super(name);
	}

	private TestResult result = null;

	protected void setUp() throws Exception {
		super.setUp();
		this.project = ConfigurableTestProject.getTestProject();

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$

		IPackagesViewPart packageExplorer = null;
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}

		packageExplorer.selectAndReveal(project.getIJavaProject());

		PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(HibernateConsolePerspectiveFactory.ID_CONSOLE_PERSPECTIVE));

		IPath cfgFilePath = new Path(ConfigurableTestProject.PROJECT_NAME + "/" +  //$NON-NLS-1$
				FilesTransfer.SRC_FOLDER + "/" + ConsoleConfigUtils.CFG_FILE_NAME); //$NON-NLS-1$
		ConsoleConfigUtils.createConsoleConfig(ConsoleConfigUtils.ConsoleCFGName, 
				cfgFilePath, ConfigurableTestProject.PROJECT_NAME);
		ProjectUtils.toggleHibernateOnProject(project.getIProject(), true, ConsoleConfigUtils.ConsoleCFGName);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult result) {
		this.result = result;
		super.run(result);
	}

	public void tearDown() throws Exception {
		ProjectUtils.toggleHibernateOnProject(project.getIProject(), false, ConsoleConfigUtils.ConsoleCFGName);
		ConsoleConfigUtils.deleteConsoleConfig(ConsoleConfigUtils.ConsoleCFGName);
		project.deleteIProject(false);
		project = null;
		super.tearDown();
	}

	protected ConfigurableTestProject getProject() {
		return this.project;
	}

	public void testEachPackWithTestSet() throws JavaModelException {
	   	long start_time = System.currentTimeMillis();
		TestSuite suite = TestSet.getTests();
		int pack_count = 0;
		IPackageFragmentRoot[] roots = project.getIJavaProject().getAllPackageFragmentRoots();
		for (int i = 0; i < roots.length; i++) {
	    	if (roots[i].getClass() != PackageFragmentRoot.class) {
	    		continue;
	    	}
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot) roots[i];
			IJavaElement[] els = packageFragmentRoot.getChildren();
			for (int j = 0; j < els.length; j++) {
				IJavaElement javaElement = els[j];
				if (!(javaElement instanceof IPackageFragment)) {
					continue;
				}
				IPackageFragment pack = (IPackageFragment)javaElement;
				// use packages only with compilation units
				if (pack.getCompilationUnits().length == 0) {
					continue;
				}
				if (Customization.U_TEST_PACKS_PATTERN) {
					if (!Pattern.matches(Customization.TEST_PACKS_PATTERN, javaElement.getElementName())) {
						continue;
					}
				}

				long st_pack_time = System.currentTimeMillis();
				int prev_failCount = result.failureCount();
				int prev_errCount = result.errorCount();

				if (Customization.SHOW_EACH_TEST) {
					// this display result for each test in JUinit view
					suite = TestSet.getTests();
				}

				activePackage = pack;
				customizeCfgXml(pack);
				//==============================
				//run all tests for package
				//suite.run(result);
				for (int k = 0; k < suite.testCount(); k++) {
					Test test = suite.testAt(k);
					test.run(result);
				}
				closeAllEditors();
				//==============================
				pack_count++;
				if (Customization.USE_CONSOLE_OUTPUT){
					System.out.print( result.errorCount() - prev_errCount + ConsoleTestMessages.HibernateAllMappingTests_errors + " \t"); //$NON-NLS-1$
					System.out.print( result.failureCount() - prev_failCount + ConsoleTestMessages.HibernateAllMappingTests_fails + "\t");						 //$NON-NLS-1$
					long period = System.currentTimeMillis() - st_pack_time;
					String time = period / 1000 + "." + (period % 1000) / 100; //$NON-NLS-1$
					System.out.println( time +ConsoleTestMessages.HibernateAllMappingTests_seconds + 
							" {" + javaElement.getElementName() + "}");  //$NON-NLS-1$//$NON-NLS-2$
				}

				if (Customization.STOP_AFTER_MISSING_PACK){
					if (result.failureCount() > prev_failCount) break;
				}
				prev_failCount = result.failureCount();
				prev_errCount = result.errorCount();
			}
		}
		if (Customization.USE_CONSOLE_OUTPUT){
			System.out.println( "====================================================="); //$NON-NLS-1$
			System.out.print( result.errorCount() + ConsoleTestMessages.HibernateAllMappingTests_errors + " \t"); //$NON-NLS-1$
			System.out.print( result.failureCount() + ConsoleTestMessages.HibernateAllMappingTests_fails + "\t");						 //$NON-NLS-1$
			System.out.print(( System.currentTimeMillis() - start_time ) / 1000 + ConsoleTestMessages.HibernateAllMappingTests_seconds + "\t" );	 //$NON-NLS-1$
			System.out.println( pack_count + ConsoleTestMessages.HibernateAllMappingTests_packages_tested );
		}
	}

	protected void customizeCfgXml(IPackageFragment pack) {
		assertNotNull(pack);
		try {
			ConsoleConfigUtils.customizeCfgXmlForPack(pack);
		} catch (CoreException e) {
			String out = NLS.bind(ConsoleTestMessages.UpdateConfigurationTest_error_customising_file_for_package,
					new Object[] { ConsoleConfigUtils.CFG_FILE_NAME, pack.getPath(), e.getMessage() } );
			fail(out);
		}
	}
	
	protected void closeAllEditors() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	/**
	 * @return the activePackage
	 */
	public static synchronized IPackageFragment getActivePackage() {
		return activePackage;
	}
}
