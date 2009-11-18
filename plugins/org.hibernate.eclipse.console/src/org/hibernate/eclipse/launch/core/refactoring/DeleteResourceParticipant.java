/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.launch.core.refactoring;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Vitali Yemialyanchyk
 */
public class DeleteResourceParticipant extends DeleteParticipant {
	
	private String path2Del = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createPreChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (path2Del == null) {
			return null;
		}
		List<Change> changes = new ArrayList<Change>();
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		for (int i = 0; i < configs.length; i++) {
			final ConsoleConfiguration config = configs[i];
			final URL[] classPathURLs_Jars = config.getCustomClassPathURLs_Jars();
			for (int j = 0; j < classPathURLs_Jars.length; j++) {
				String path2Jar = classPathURLs_Jars[i].getPath();
				if (path2Jar.startsWith(path2Del)) {
					changes.add(new ConsoleConfigUnloadJarChange(config));
					break;
				}
			}
		}
		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	@Override
	public String getName() {
		return HibernateConsoleMessages.DeleteResourceParticipant_hibernate_configurations_updates;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	@Override
	protected boolean initialize(Object element) {
		boolean res = false;
		IResource res2Del = (IResource)element;
		String path2Del = res2Del.getLocationURI().getPath();
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		for (int i = 0; i < configs.length; i++) {
			final ConsoleConfiguration config = configs[i];
			final URL[] classPathURLs_Jars = config.getCustomClassPathURLs_Jars();
			for (int j = 0; j < classPathURLs_Jars.length; j++) {
				String path2Jar = classPathURLs_Jars[i].getPath();
				if (path2Jar.startsWith(path2Del)) {
					res = true;
				}
			}
		}
		this.path2Del = res ? path2Del : null;
		return res;
	}

}
