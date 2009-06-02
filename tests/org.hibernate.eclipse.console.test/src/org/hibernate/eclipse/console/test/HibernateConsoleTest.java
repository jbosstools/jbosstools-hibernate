package org.hibernate.eclipse.console.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;

public abstract class HibernateConsoleTest extends TestCase {

	private static final long MAX_IDLE = 5*60*1000L;

	private SimpleTestProject project;

	public HibernateConsoleTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.project = createTestProject();


		waitForJobs();
		PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().setPerspective(
						PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(HibernateConsolePerspectiveFactory.ID_CONSOLE_PERSPECTIVE));


		IPackagesViewPart packageExplorer = null;
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}

		IType type = this.project.getTestClassType();
		packageExplorer.selectAndReveal(type);


		FileEditorInput input = new FileEditorInput((IFile) type.getCompilationUnit().getCorrespondingResource());

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, JavaUI.ID_CU_EDITOR );

		delay(2000);
	}

	protected SimpleTestProject createTestProject() {
		return new SimpleTestProject();
	}

	protected void tearDown() throws Exception {
		waitForJobs();

		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$


		this.project.deleteIProject();
		this.project = null;

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
			//display.update();
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
	/*public void waitForJobs() {
		while (Platform.getJobManager().currentJob() != null)
			delay(2000);
	}*/

	public void waitForJobs() {
		long start = System.currentTimeMillis();
		while (!Job.getJobManager().isIdle()) {
			delay(500);
			if ( (System.currentTimeMillis()-start) > MAX_IDLE )
				throw new RuntimeException(ConsoleTestMessages.HibernateConsoleTest_long_running_task_detected);
		}
		delay(1000);
	}

	public boolean noMoreJobs() {
		Job[] queuedJobs= Job.getJobManager().find(null);
		for (int i= 0; i < queuedJobs.length; i++) {
			Job entry= queuedJobs[i];
			if (entry.getState() == Job.RUNNING || entry.getState() == Job.WAITING) {
				return false;
			}
		}
		return true;
	}

	protected SimpleTestProject getProject() {
		return this.project;
	}

}
