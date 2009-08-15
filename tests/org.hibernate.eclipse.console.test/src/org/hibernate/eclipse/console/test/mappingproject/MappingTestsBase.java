/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;
import org.hibernate.eclipse.console.utils.ProjectUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Vitali Yemialyanchyk
 *
 */
@SuppressWarnings("restriction")
public abstract class MappingTestsBase extends TestCase {

	protected String consoleConfigName = null;
	
	protected IPackageFragment testPackage = null; 

	protected ConfigurableTestProject testProject = null;

	protected TestResult result = null;

	protected int executions = 0;

	public MappingTestsBase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		testProject = new ConfigurableTestProject("JUnitTestProj"+System.currentTimeMillis()); //$NON-NLS-1$


		consoleConfigName = testProject.getIProject().getName();
		testPackage = null;		

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$

		IPackagesViewPart packageExplorer = null;
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}

		packageExplorer.selectAndReveal(testProject.getIJavaProject());

		PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(HibernateConsolePerspectiveFactory.ID_CONSOLE_PERSPECTIVE));
		
		setUpConsoleConfig();

		ProjectUtils.toggleHibernateOnProject(testProject.getIProject(), true, consoleConfigName);
		testProject.fullBuild();
	}

	abstract protected void setUpConsoleConfig() throws Exception;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult result) {
		this.result = result;
		super.run(result);
	}

	public void tearDown() throws Exception {
		ProjectUtils.toggleHibernateOnProject(testProject.getIProject(), false, consoleConfigName);
		ConsoleConfigUtils.deleteConsoleConfig(consoleConfigName);
		testProject.deleteIProject(false);
		testProject = null;
		consoleConfigName = null;
		testPackage = null;		
		super.tearDown();
	}

	/**
	 * Inspect all it's packages of the current testProject and
	 * execute all tests from TestSet, considering one test package as active.
	 * @throws CoreException
	 */
	public void allTestsRunForProject() throws CoreException {
		testProject.fullBuild();
		IPackageFragmentRoot[] roots = testProject.getIJavaProject().getAllPackageFragmentRoots();
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
				testPackage = (IPackageFragment)javaElement;
				// use packages only with compilation units
				if (testPackage.getCompilationUnits().length == 0) {
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

				Method m = null;
				try {
					m = Display.getCurrent().getClass().getDeclaredMethod("runAsyncMessages", boolean.class);
					m.setAccessible(true);
				} catch (SecurityException e) {
				} catch (NoSuchMethodException e) {
				}
				
				TestSuite suite = TestSet.createTestSuite(consoleConfigName, testPackage, testProject);

				customizeCfgXml(testPackage);
				//==============================
				//run all tests for package
				//suite.run(result);
				for (int k = 0; k < suite.testCount(); k++) {
					Test test = suite.testAt(k);
					test.run(result);
// ----------------------------------------------					
// https://jira.jboss.org/jira/browse/JBIDE-4740 
// first way to fix OutOfMemory problems
					closeAllEditors();
					int LIMIT = 50,
						ii = 0;
					while(ii<LIMIT && Display.getCurrent().readAndDispatch()) {
						//Display.getCurrent().sleep();
						ii++;
					}
				}
				// Second way to fix https://jira.jboss.org/jira/browse/JBIDE-4740
				// invoked to clean up RunnableLock[] in UISynchronizer
				try {
					m.invoke(Display.getCurrent(), Boolean.TRUE);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}				
				//==============================
				executions++;
				if (Customization.USE_CONSOLE_OUTPUT) {
					System.out.print(result.errorCount() - prev_errCount + ConsoleTestMessages.HibernateAllMappingTests_errors + " \t"); //$NON-NLS-1$
					System.out.print(result.failureCount() - prev_failCount + ConsoleTestMessages.HibernateAllMappingTests_fails + "\t");						 //$NON-NLS-1$
					long period = System.currentTimeMillis() - st_pack_time;
					String time = period / 1000 + "." + (period % 1000) / 100; //$NON-NLS-1$
					System.out.println(time + ConsoleTestMessages.HibernateAllMappingTests_seconds + 
							" {" + javaElement.getElementName() + "}");  //$NON-NLS-1$//$NON-NLS-2$
				}

				if (Customization.STOP_AFTER_MISSING_PACK) {
					if (result.failureCount() > prev_failCount) {
						break;
					}
				}
				prev_failCount = result.failureCount();
				prev_errCount = result.errorCount();
			}
		}
	}

	abstract public void testEachPackWithTestSet() throws CoreException, IOException;

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

	public String getConsoleConfigName() {
		return consoleConfigName;
	}

	public void setConsoleConfigName(String consoleConfigName) {
		this.consoleConfigName = consoleConfigName;
	}

	public IPackageFragment getTestPackage() {
		return testPackage;
	}

	public void setTestPackage(IPackageFragment testPackage) {
		this.testPackage = testPackage;
	}

	public ConfigurableTestProject getTestProject() {
		return testProject;
	}

	public void setTestProject(ConfigurableTestProject testProject) {
		this.testProject = testProject;
	}
}
