package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNatureRemoveTest extends TestCase {
	public void testEnableHibernateNature() throws BackingStoreException, CoreException {
		MappingTestProject project = MappingTestProject.getTestProject();
		IJavaProject prj = project.getIJavaProject();
		ProjectUtils.removeProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
	}
}
