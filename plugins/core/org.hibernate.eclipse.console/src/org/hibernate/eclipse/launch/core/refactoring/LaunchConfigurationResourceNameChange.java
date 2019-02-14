/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.launch.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class LaunchConfigurationResourceNameChange extends Change {

	private ILaunchConfiguration fLaunchConfiguration;
	private IPath fOldPath;
	private IPath fNewPath;

	/**
	 * LaunchConfigurationResourceNameChange constructor.
	 * @param launchConfiguration the launch configuration to modify
	 * @param oldPath the old Path of the resource.
	 * @param newPath the new Path of the resource.
	 */
	LaunchConfigurationResourceNameChange(ILaunchConfiguration launchConfiguration, IPath oldPath, IPath newPath){
		fLaunchConfiguration = launchConfiguration;
		fOldPath = oldPath;
		fNewPath = newPath;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	public Object getModifiedElement() {
		return fLaunchConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	public String getName() {
		return NLS.bind(HibernateConsoleMessages.LaunchConfigurationResourceNameChange_update_resource_path_in_launch_cfg, fLaunchConfiguration.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initializeValidationData(IProgressMonitor pm) {	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	@SuppressWarnings("deprecation")
	public Change perform(IProgressMonitor pm) throws CoreException {
		if (!fLaunchConfiguration.exists()){
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath rootLoacation = root.getLocation();
			IPath oldConfigLocationPath = fLaunchConfiguration.getLocation();
			if (oldConfigLocationPath == null && fLaunchConfiguration.getFile() != null){
				oldConfigLocationPath = fLaunchConfiguration.getFile().getFullPath();
			}
			if (oldConfigLocationPath != null){
				int matchSegment = oldConfigLocationPath.matchingFirstSegments(rootLoacation);
				IPath relativePath = oldConfigLocationPath.removeFirstSegments(matchSegment);
				relativePath = relativePath.setDevice(null).makeAbsolute();

				if (HibernateRefactoringUtil.isAttributeChanged(relativePath.toOSString(), fOldPath)){
					matchSegment = relativePath.matchingFirstSegments(fOldPath);
					IPath newLaunchPath = fNewPath.append(relativePath.removeFirstSegments(matchSegment));
					IFile file = root.getFileForLocation(rootLoacation.append(newLaunchPath));
					if (file != null){
						fLaunchConfiguration = DebugPlugin.getDefault().getLaunchManager().getLaunchConfiguration(file);
					}
				}
			}
		}
		fLaunchConfiguration = HibernateRefactoringUtil.updateConsoleConfig(fLaunchConfiguration, fOldPath, fNewPath);
		return new LaunchConfigurationResourceNameChange(fLaunchConfiguration, fNewPath, fOldPath);
	}
}
