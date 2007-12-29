package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class HibernateMappingTests extends TestCase {

	private MappingTestProject project;

	public HibernateMappingTests(String name) {
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
		delay(2000);
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
		this.project.deleteIProject();
		waitForJobs();
		super.tearDown();
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
	
	public void testRunTestSet() {
		TestSuite suite = TestSet.getTests();
		//is waytForJob() necessary between each test?
		//suite.run(result);
		for (int i = 0; i < suite.testCount(); i++) {
			Test test = suite.testAt(i);
			test.run(result);
			/*if (result.failureCount() > 0){
				fail("Test " + test.getClass().getName() + " failed.");
			}
			if (result.errorCount() > 0){
				fail("Test " + test.getClass().getName() + " has errors.");
			}*/
			waitForJobs();
			delay(20000);
		}
		if (result.failureCount() > 0){
			fail("One or more test failed.");
		}
		if (result.errorCount() > 0){
			fail("One or more test has error(s).");
		}
	}
}
 