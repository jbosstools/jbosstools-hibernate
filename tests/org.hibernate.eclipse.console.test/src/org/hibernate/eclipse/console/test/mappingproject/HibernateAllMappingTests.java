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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;

public class HibernateAllMappingTests extends TestCase {

	private MappingTestProject project;
	
	private static IPackageFragment activePackage;

	public HibernateAllMappingTests(String name) {
		super(name);
	}
	
	private TestResult result = null;

	protected void setUp() throws Exception {
		super.setUp();
		this.project = MappingTestProject.getTestProject();

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

		
		waitForJobs();
		runTestsAfterSetup();
		ProjectUtil.createConsoleCFG();
	}

	private void runTestsAfterSetup() {
		TestSuite suite = TestSetAfterSetup.getTests();
		for (int i = 0; i < suite.testCount(); i++) {
			Test test = suite.testAt(i);
			test.run(result);
		}
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
		waitForJobs();
		runTestsBeforeTearDown();		
		waitForJobs();
		delay(1000);
		//this.project.deleteIProject();
		//waitForJobs();
		super.tearDown();
	}

	private void runTestsBeforeTearDown() {
		TestSuite suite = TestSetBeforeTearDown.getTests();
		for (int i = 0; i < suite.testCount(); i++) {
			Test test = suite.testAt(i);
			test.run(result);	
		}		
	}

	/**
	 * Process UI input but do not return for the specified time interval.
	 * 
	 * @param waitTimeMillis
	 *            the number of milliseconds
	 */
	protected void delay(long waitTimeMillis) {
		if (waitTimeMillis <= 0) return;
		Display display = Display.getCurrent();

		// If this is the UI thread,
		// then process input.
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.update();
		}

		// Otherwise, perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				// Ignored.
			}
		}
	}

	/**
	 * Wait until all background tasks are complete.
	 */
	public void waitForJobs() {
		while (Platform.getJobManager().currentJob() != null)
			delay(1000);
	}
	
	protected MappingTestProject getProject() {
		return this.project;
	}	
	
	public void testEachPackWithTestSet() throws JavaModelException {
	   	long start_time = System.currentTimeMillis();
		TestSuite suite = TestSet.getTests();
		int pack_count = 0;
		IPackageFragmentRoot[] roots = project.getIJavaProject().getAllPackageFragmentRoots();	
		for (int i = 0; i < roots.length; i++) {
	    	if (roots[i].getClass() != PackageFragmentRoot.class) continue;
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot) roots[i];
			IJavaElement[] els = packageFragmentRoot.getChildren();
			for (int j = 0; j < els.length; j++) {
				IJavaElement javaElement = els[j];
				if (javaElement instanceof IPackageFragment){
					IPackageFragment pack = (IPackageFragment) javaElement;
					// use packages only with compilation units
					if (pack.getCompilationUnits().length == 0) continue;
					if (Customization.U_TEST_PACKS_PATTERN &&
						!Pattern.matches(Customization.TEST_PACKS_PATTERN, javaElement.getElementName())){
						continue;
					}
					
					long st_pack_time = System.currentTimeMillis();	
					int prev_failCount = result.failureCount();	
					int prev_errCount = result.errorCount();

					if (Customization.SHOW_EACH_TEST) suite = TestSet.getTests();
				
					activePackage = pack;
					//==============================					
					//run all tests for package
					for (int k = 0; k < suite.testCount(); k++) {
						Test test = suite.testAt(k);
						test.run(result);
						waitForJobs();
					}
					//==============================
					pack_count++;
					if (Customization.USE_CONSOLE_OUTPUT){
						System.out.print( result.errorCount() - prev_errCount + Messages.HIBERNATEALLMAPPINGTESTS_ERRORS + " \t"); //$NON-NLS-1$
						System.out.print( result.failureCount() - prev_failCount + Messages.HIBERNATEALLMAPPINGTESTS_FAILS + "\t");						 //$NON-NLS-1$
						long period = System.currentTimeMillis() - st_pack_time;
						String time = period / 1000 + "." + (period % 1000) / 100; //$NON-NLS-1$
						System.out.println( time +Messages.HIBERNATEALLMAPPINGTESTS_SECONDS + javaElement.getElementName());						
					}
					waitForJobs();
					delay(Customization.EACTH_PACK_TEST_DELAY);
								
					if (Customization.STOP_AFTER_MISSING_PACK){
						if (result.failureCount() > prev_failCount) break;
					}
					prev_failCount = result.failureCount();
					prev_errCount = result.errorCount();
				}				
			}			
		}
		if (Customization.USE_CONSOLE_OUTPUT){
			System.out.println( "====================================================="); //$NON-NLS-1$
			System.out.print( result.errorCount() + Messages.HIBERNATEALLMAPPINGTESTS_ERRORS + " \t"); //$NON-NLS-1$
			System.out.print( result.failureCount() + Messages.HIBERNATEALLMAPPINGTESTS_FAILS + "\t");						 //$NON-NLS-1$
			System.out.print(( System.currentTimeMillis() - start_time ) / 1000 + Messages.HIBERNATEALLMAPPINGTESTS_SECONDS + "\t" );	 //$NON-NLS-1$
			System.out.println( pack_count + Messages.HIBERNATEALLMAPPINGTESTS_PACKAGES_TESTED );
		}
		waitForJobs();		
		
		delay(Customization.AFTER_ALL_PACKS_DELAY);
	}

	/**
	 * @return the activePackage
	 */
	public static IPackageFragment getActivePackage() {
		return activePackage;
	}
}
 