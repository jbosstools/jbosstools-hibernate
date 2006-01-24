package org.hibernate.eclipse.console.test.perspective;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.test.HibernateConsoleTest;

public class PerspectiveTest extends HibernateConsoleTest {

	public PerspectiveTest(String name) {
		super( name );
	}
	
	public void testPerspective() {

		IPerspectiveDescriptor perspective = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().getPerspective();

		assertEquals(perspective.getLabel(), "Hibernate Console");		
	}

}
