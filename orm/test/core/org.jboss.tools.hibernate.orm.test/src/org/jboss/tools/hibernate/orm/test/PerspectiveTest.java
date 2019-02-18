package org.jboss.tools.hibernate.orm.test;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleMessages;
import org.jboss.tools.hibernate.orm.test.utils.HibernateConsoleTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PerspectiveTest {
	
	private HibernateConsoleTestHelper hcth = null;
	
	@Before
	public void setUp() throws Exception {
		hcth = new HibernateConsoleTestHelper();
		hcth.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		hcth.tearDown();
		hcth = null;
	}

	@Test
	public void testEnableHibernateProject() {
		IPerspectiveDescriptor perspective = PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getPerspective();
		Assert.assertEquals(
				perspective.getLabel(), 
				TestConsoleMessages.PerspectiveTest_hibernate);
	}

}
