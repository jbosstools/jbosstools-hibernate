package org.jboss.tools.hibernate.orm.test;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.jboss.tools.hibernate.orm.test.utils.HibernateConsoleTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ContentAssistTest {
	
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
	public void testEnableHibernateNature() throws BackingStoreException, CoreException {
		
		IJavaProject prj = hcth.getProject().getIJavaProject();
		IScopeContext scope = new ProjectScope(prj.getProject() );
		
	
		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
		
		node.putBoolean(HibernatePropertiesConstants.HIBERNATE3_ENABLED, true );
		node.put(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, "testcfg" ); //$NON-NLS-1$
		node.flush();
		
		
		ProjectUtils.addProjectNature(prj.getProject(), HibernatePropertiesConstants.HIBERNATE_NATURE, new NullProgressMonitor() );
		ProjectUtils.removeProjectNature(prj.getProject(), HibernatePropertiesConstants.HIBERNATE_NATURE, new NullProgressMonitor() );
	}
}