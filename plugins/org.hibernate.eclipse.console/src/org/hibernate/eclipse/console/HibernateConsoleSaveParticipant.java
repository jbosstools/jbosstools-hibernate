/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console;

import java.io.File;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author max
 *  
 */
public class HibernateConsoleSaveParticipant implements ISaveParticipant {

	static final String SAVENAME = "hibernate-console.xml"; //$NON-NLS-1$
	
	public void prepareToSave(ISaveContext context) throws CoreException {

	}

	public void saving(ISaveContext context) throws CoreException {
		switch (context.getKind() ) {
		case ISaveContext.FULL_SAVE:
		case ISaveContext.PROJECT_SAVE:
		case ISaveContext.SNAPSHOT:
			// save the plug-in state
			int saveNumber = context.getSaveNumber();
			String saveFileName = SAVENAME + "-" + Integer.toString(saveNumber); //$NON-NLS-1$
			File f = HibernateConsolePlugin
					.getDefault().getStateLocation().append(saveFileName)
					.toFile();
			// if we fail to write, an exception is thrown and we do not update
			// the path
			HibernateConsolePlugin
					.getDefault().writeStateTo(f);
			context.map(new Path(SAVENAME), new Path(saveFileName) );
			context.needSaveNumber();
			break;
		//case ISaveContext.PROJECT_SAVE:
			// get the project related to this save operation
			//IProject project = context.getProject();
			// save its information, if necessary
			//break;
		//case ISaveContext.SNAPSHOT:
			// This operation needs to be really fast because
			// snapshots can be requested frequently by the
			// workspace.
			//break;
		}

	}

	public void doneSaving(ISaveContext context) {
		HibernateConsolePlugin myPluginInstance = HibernateConsolePlugin
				.getDefault();

		// delete the old saved state since it is not necessary anymore
		int previousSaveNumber = context.getPreviousSaveNumber();
		String oldFileName = SAVENAME + "-" + Integer.toString(previousSaveNumber); //$NON-NLS-1$
		File f = myPluginInstance.getStateLocation().append(oldFileName)
				.toFile();
		//System.out.println("delete " + f);
		f.delete();
	}

	public void rollback(ISaveContext context) {
		HibernateConsolePlugin myPluginInstance = HibernateConsolePlugin
				.getDefault();

		// since the save operation has failed, delete the saved state we have
		// just written
		int saveNumber = context.getSaveNumber();
		String saveFileName = SAVENAME + "-" + Integer.toString(saveNumber); //$NON-NLS-1$
		File f = myPluginInstance.getStateLocation().append(saveFileName)
				.toFile();
		f.delete();
	}

	public void doStart(HibernateConsolePlugin plugin) throws CoreException {
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(plugin, this);
		if (lastState == null) {
			return;
		}
		IPath location = lastState.lookup(new Path(HibernateConsoleSaveParticipant.SAVENAME) );
		if (location == null) {
			return;
		}
		File f = plugin.getStateLocation().append(location).toFile();
		plugin.readStateFrom(f);		
	}

}
