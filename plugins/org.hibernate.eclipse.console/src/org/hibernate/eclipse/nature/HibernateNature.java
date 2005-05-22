package org.hibernate.eclipse.nature;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.eclipse.builder.HibernateBuilder;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class HibernateNature implements IProjectNature {

	private IProject project;

	public void configure() throws CoreException {
		HibernateConsolePlugin.getDefault().log("Configuring " + project + " as a Hibernate project");
		
		IProjectDescription desc = project.getDescription();
		   ICommand[] commands = desc.getBuildSpec();
		   boolean found = false;

		   for (int i = 0; i < commands.length; ++i) {
		      if (commands[i].getBuilderName().equals(HibernateBuilder.BUILDER_ID)) {
		         found = true;
		         break;
		      }
		   }
		   if (!found) { 
		      //add builder to project
		      ICommand command = desc.newCommand();
		      command.setBuilderName(HibernateBuilder.BUILDER_ID);
		      ArrayList list = new ArrayList();
		      list.addAll(Arrays.asList(commands));
		      list.add(command);
		      desc.setBuildSpec((ICommand[])list.toArray(new ICommand[]{}));
		      project.setDescription(desc, new NullProgressMonitor());
		   }
	}

	public void deconfigure() throws CoreException {
		HibernateConsolePlugin.getDefault().log("Deconfiguring " + project + " as a Hibernate project");
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
