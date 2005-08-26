package org.hibernate.eclipse.console.perspective;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.hibernate.eclipse.console.HibernateConsoleTest;

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
