package org.hibernate.eclipse.console.test;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
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
		
	
		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
		
		node.putBoolean(HibernatePropertiesConstants.HIBERNATE3_ENABLED, true );
		node.put(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, "testcfg" ); //$NON-NLS-1$
		node.flush();
		
		
		ProjectUtils.addProjectNature(prj.getProject(), HibernatePropertiesConstants.HIBERNATE_NATURE, new NullProgressMonitor() );
		ProjectUtils.removeProjectNature(prj.getProject(), HibernatePropertiesConstants.HIBERNATE_NATURE, new NullProgressMonitor() );
	}
}