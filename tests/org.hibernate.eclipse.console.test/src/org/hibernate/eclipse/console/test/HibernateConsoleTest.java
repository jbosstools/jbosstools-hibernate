package org.hibernate.eclipse.console.test;

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;

public abstract class HibernateConsoleTest extends TestCase {

	private SimpleTestProject project;

	public HibernateConsoleTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.project = new SimpleTestProject();

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
	
	protected void tearDown() throws Exception {
		waitForJobs();

		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective"));


		this.project.deleteIProject();

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
	
	protected SimpleTestProject getProject() {
		return this.project;
	}
	
}
 