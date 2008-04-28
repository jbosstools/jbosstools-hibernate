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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author Dmitry Geraskov
 *
 */
public class LaunchConfigurationResourceNameChange extends Change {
	
	private ILaunchConfiguration fLaunchConfiguration;
	private IPath fOldPath;
	private IPath fNewPath;
	
	/**
	 * LaunchConfigurationResourceMoveChange constructor.
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
		return "Update resource path in launch configuration " + fLaunchConfiguration.getName();
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

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		fLaunchConfiguration = HibernateRefactoringUtil.updateLaunchConfig(fLaunchConfiguration, fOldPath, fNewPath);			
		return new LaunchConfigurationResourceNameChange(fLaunchConfiguration, fNewPath, fOldPath);
	}
}
