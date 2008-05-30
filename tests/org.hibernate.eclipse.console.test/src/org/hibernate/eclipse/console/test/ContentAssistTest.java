package org.hibernate.eclipse.console.test;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ContentAssistTest extends HibernateConsoleTest {

	public ContentAssistTest(String name) {
		super( name ); 
	}
	
	public void testEnableHibernateNature() throws BackingStoreException, CoreException {
		
		IJavaProject prj = getProject().getIJavaProject();
		IScopeContext scope = new ProjectScope(prj.getProject() );
		
	
		Preferences node = scope.getNode("org.hibernate.eclipse.console"); //$NON-NLS-1$
		
		node.putBoolean("hibernate3.enabled", true ); //$NON-NLS-1$
		node.put("default.configuration", "testcfg" ); //$NON-NLS-1$ //$NON-NLS-2$
		node.flush();
		
		
		ProjectUtils.addProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() ); //$NON-NLS-1$
		ProjectUtils.removeProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() ); //$NON-NLS-1$
	}
}