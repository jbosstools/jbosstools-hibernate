package org.jboss.tools.hibernate.orm.test;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.junit.Assert;
import org.junit.Test;

public class ProjectUtilsTest {
	
	@Test
	public void testAvailablePersistenceUnits() throws Exception {
		// First test the main case 
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		project.create(null);
		project.open(null);
		IFolder folder = project.getFolder("META-INF");
		folder.create(true, true, null);
		IFile file = folder.getFile("persistence.xml");
		String contents = "<persistence><persistence-unit name='foo'/></persistence>";
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		javaProject.setOutputLocation(project.getFullPath(), null);
		javaProject.open(null);
		Assert.assertArrayEquals(
				new String[] { "foo" },
				ProjectUtils.availablePersistenceUnits(javaProject));
		// Second test the edge case
		Assert.assertArrayEquals(
				new String[0], 
				ProjectUtils.availablePersistenceUnits(null));
	}

}
