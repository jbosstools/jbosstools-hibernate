/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console;

import java.io.File;

import org.eclipse.core.resources.IProject;
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

	static final String SAVENAME = "hibernate-console.xml";
	
	public void prepareToSave(ISaveContext context) throws CoreException {

	}

	public void saving(ISaveContext context) throws CoreException {
		switch (context.getKind() ) {
		case ISaveContext.FULL_SAVE:
			HibernateConsolePlugin myPluginInstance = HibernateConsolePlugin
					.getDefault();
			// save the plug-in state
			int saveNumber = context.getSaveNumber();
			String saveFileName = SAVENAME + "-" + Integer.toString(saveNumber);
			File f = myPluginInstance.getStateLocation().append(saveFileName)
					.toFile();
			// if we fail to write, an exception is thrown and we do not update
			// the path
			myPluginInstance.writeStateTo(f);
			context.map(new Path(SAVENAME), new Path(saveFileName) );
			context.needSaveNumber();
			break;
		case ISaveContext.PROJECT_SAVE:
			// get the project related to this save operation
			IProject project = context.getProject();
			// save its information, if necessary
			break;
		case ISaveContext.SNAPSHOT:
			// This operation needs to be really fast because
			// snapshots can be requested frequently by the
			// workspace.
			break;
		}

	}

	public void doneSaving(ISaveContext context) {
		HibernateConsolePlugin myPluginInstance = HibernateConsolePlugin
				.getDefault();

		// delete the old saved state since it is not necessary anymore
		int previousSaveNumber = context.getPreviousSaveNumber();
		String oldFileName = "save-" + Integer.toString(previousSaveNumber);
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
		String saveFileName = "save-" + Integer.toString(saveNumber);
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
