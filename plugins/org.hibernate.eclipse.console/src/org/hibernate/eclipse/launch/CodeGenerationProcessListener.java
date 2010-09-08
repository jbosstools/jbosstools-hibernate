/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.launch;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

/**
 * Refreshes resources as specified by a launch configuration, when 
 * an associated process terminates.
 * 
 * Here the listener intended to delete temporary generated Ant build.xml file.
 * 
 * @author Vitali Yemialyanchyk
 */
public class CodeGenerationProcessListener implements IDebugEventSetListener  {

	protected IProcess process;
	protected String fileName;
	protected Set<String> outputDirs;

	public CodeGenerationProcessListener(IProcess process, String fileName, 
			Set<String> outputDirs) {
		this.process = process;
		this.fileName = fileName;
		this.outputDirs = outputDirs;
	}
	
	/**
	 * If the process has already terminated, resource refreshing is done
	 * immediately in the current thread. Otherwise, refreshing is done when the
	 * process terminates.
	 */
	public void startBackgroundRefresh() {
		synchronized (process) {
			if (process.isTerminated()) {
				refresh();
			} else {
				DebugPlugin.getDefault().addDebugEventListener(this);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			if (event.getSource() == process && event.getKind() == DebugEvent.TERMINATE) {
				DebugPlugin.getDefault().removeDebugEventListener(this);
				refresh();
				break;
			}
		}
	}
	
	/**
	 * Submits a job to do the refresh, i.e. delete temporary build.xml and 
	 * it's parent directory.
	 */
	protected void refresh() {
    	// erase file fileName
		IPath path2File = new Path(fileName);
		File file =  path2File.toFile();
		file.delete();
		//
		String externalPropFileName = CodeGenXMLFactory.getExternalPropFileNameStandard(fileName);
		path2File = new Path(externalPropFileName);
		file =  path2File.toFile();
		file.delete();
        for (String path : outputDirs) {
        	CodeGenerationUtils.refreshOutputDir(path);
		}
	}
}
