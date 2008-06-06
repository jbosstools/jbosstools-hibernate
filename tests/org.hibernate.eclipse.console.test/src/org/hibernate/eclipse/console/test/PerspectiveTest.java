package org.hibernate.eclipse.console.test;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

public class PerspectiveTest extends HibernateConsoleTest {

	public PerspectiveTest(String name) {
		super( name );
	}

	public void testEnableHibernateProject() {

		IPerspectiveDescriptor perspective = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().getPerspective();

		assertEquals(perspective.getLabel(), ConsoleTestMessages.PerspectiveTest_hibernate);
	}

}
