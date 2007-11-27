/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Tau
 *
 */
public class OrmHibernateNature implements IProjectNature {
	
	public static final String ORM_HIBERNATE_BUILDER_ID = "org.jboss.tools.hibernate.core.OrmHibernateBuilder";

	private IProject project;
	
	// add tau 27.09.2005
	//private static final String COMMAND_ENABLED= "CommandEnabled";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		// add tau 24.03.2005
		IProjectDescription description = this.project.getDescription();
		ICommand[] builderCommands = description.getBuildSpec();

		for (int i = 0; i < builderCommands.length; i++) 
			if (builderCommands[i].getBuilderName().equals(ORM_HIBERNATE_BUILDER_ID))
				// builder already added no need to add again
				return;
			
		// builder not found, must be added
		ICommand command = description.newCommand();
		command.setBuilderName(ORM_HIBERNATE_BUILDER_ID);
		
		//add tau 28.09.2005
		/* del tau 28.09.2005
		if (command.isConfigurable()){
			command.setBuilding(IncrementalProjectBuilder.AUTO_BUILD,false);
			command.setBuilding(IncrementalProjectBuilder.FULL_BUILD,false);
			command.setBuilding(IncrementalProjectBuilder.INCREMENTAL_BUILD,false);			
		}
		*/
		
		// add tau 27.09.2005
		/* del tau 28.09.2005
		Map args= command.getArguments();
		if (args == null) {
			args= new HashMap(1);
		}
		args.put(COMMAND_ENABLED, Boolean.valueOf(false));
		command.setArguments(args);
		//--
		 */
		
		ICommand[] updatedBuilderCommands = new ICommand[builderCommands.length + 1];
		System.arraycopy(builderCommands, 0, updatedBuilderCommands, 0, builderCommands.length);
		updatedBuilderCommands[updatedBuilderCommands.length - 1] = command;
		description.setBuildSpec(updatedBuilderCommands);
		this.project.setDescription(description, null);
  
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	
	static String EXTERNAL_TOOL_BUILDER = "org.eclipse.ui.externaltools.ExternalToolBuilder";
	static final String LAUNCH_CONFIG_HANDLE = "LaunchConfigHandle";	
	
	public void deconfigure() throws CoreException {
		// add tau 24.03.2005
		// edit tau 28.09.2005
		IProjectDescription description = this.project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			String builderName = commands[i].getBuilderName();
			if (!builderName.equals(ORM_HIBERNATE_BUILDER_ID)) {
				if(!builderName.equals(EXTERNAL_TOOL_BUILDER)) continue;
				Object handle = commands[i].getArguments().get(LAUNCH_CONFIG_HANDLE);
				if(handle == null || handle.toString().indexOf(ORM_HIBERNATE_BUILDER_ID) < 0) continue;				
			}
			ICommand[] newCommands = new ICommand[commands.length - 1];
			System.arraycopy(commands, 0, newCommands, 0, i);
			System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
			description.setBuildSpec(newCommands);
			this.project.setDescription(description, null);
			return;

		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project; 

	}

}
