package org.hibernate.eclipse.console.test.mappingproject;

import java.util.Enumeration;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
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

		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);
		
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective"));
		
		IPackagesViewPart packageExplorer = null;		
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
		
		packageExplorer.selectAndReveal(project.getIJavaProject());
		
		waitForJobs();
		runTestsAfterSetup();
	}

	private void runTestsAfterSetup() {
		TestSuite suite = TestSetAfterSetup.getTests();
		for (int i = 0; i < suite.testCount(); i++) {
			Test test = suite.testAt(i);
			test.run(result);
			/*if (result.failureCount() > 0 || result.errorCount() > 0){
				// we have failed tests after setup
				fail(((Throwable)result.failures().nextElement()).getMessage());
			}*/
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
		this.project.deleteIProject();
		waitForJobs();
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
		/*if (result.failureCount() > 0 || result.errorCount() > 0){
			// we have failed tests after setup
			fail("One or more setup test failed.");
		}*/
		TestSuite suite = TestSet.getTests();
		IPackageFragmentRoot[] roots = project.getIJavaProject().getAllPackageFragmentRoots();	
		for (int i = 0; i < roots.length; i++) {
	    	if (roots[i].getClass() != PackageFragmentRoot.class) continue;
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot) roots[i];
			IJavaElement[] els = packageFragmentRoot.getChildren();//.getCompilationUnits();
			for (int j = 0; j < els.length; j++) {
				IJavaElement javaElement = els[j];
				if (javaElement instanceof IPackageFragment){
					IPackageFragment pack = (IPackageFragment) javaElement;
					// use packages only with compilation units
					if (pack.getCompilationUnits().length == 0) continue;
					
					activePackage = pack;
					//==============================					
					//run all tests for package
					for (int k = 0; k < suite.testCount(); k++) {
						Test test = suite.testAt(k);
						test.run(result);
						waitForJobs();
					}
					//==============================
				}
			}		
		}
		waitForJobs();
		//delay(2000);
	}

	/**
	 * @return the activePackage
	 */
	public static IPackageFragment getActivePackage() {
		return activePackage;
	}
}
 