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
package org.hibernate.eclipse.console.test.launchcfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;

@SuppressWarnings("rawtypes")
public class TestLaunchConfig implements ILaunchConfiguration {

	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Map<String, Object> updatedAttributes = new HashMap<String, Object>();

	// returns updated attribute
	public Object getNewAttribute(final String attributeName) {
		return updatedAttributes.get(attributeName);
	}

	public TestLaunchConfig(final Map<String, Object> attributes) {
		if (attributes != null) {
			this.attributes = attributes;
		}
	}

	public boolean contentsEqual(final ILaunchConfiguration configuration) {
		return false;
	}

	public ILaunchConfigurationWorkingCopy copy(final String name) throws CoreException {
		return null;
	}

	public void delete() throws CoreException {

	}

	public boolean exists() {
		return false;
	}

	public boolean getAttribute(final String attributeName, final boolean defaultValue) throws CoreException {
		if (attributes.containsKey(attributeName)) {
			return (Boolean) attributes.get(attributeName);
		} else {
			return defaultValue;
		}
	}

	public boolean hasAttribute(final String attributeName) throws CoreException {
		return attributes.containsKey(attributeName);
	}

	public int getAttribute(final String attributeName, final int defaultValue) throws CoreException {
		if (attributes.containsKey(attributeName)) {
			return (Integer) attributes.get(attributeName);
		} else {
			return defaultValue;
		}
	}

	public List getAttribute(final String attributeName, final List defaultValue) throws CoreException {
		if (attributes.containsKey(attributeName)) {
			return (List) attributes.get(attributeName);
		} else {
			return defaultValue;
		}
	}

	public Set getAttribute(final String attributeName, final Set defaultValue) throws CoreException {
		return null;
	}

	public Map getAttribute(final String attributeName, final Map defaultValue) throws CoreException {
		if (attributes.containsKey(attributeName)) {
			return (Map) attributes.get(attributeName);
		} else {
			return defaultValue;
		}
	}

	public String getAttribute(final String attributeName, final String defaultValue) throws CoreException {
		if (attributes.containsKey(attributeName)) {
			return (String) attributes.get(attributeName);
		} else {
			return defaultValue;
		}
	}

	public Map getAttributes() throws CoreException {
		return null;
	}

	public String getCategory() throws CoreException {
		return ConsoleTestMessages.RefactoringTest_category;
	}

	public IFile getFile() {
		return null;
	}

	public IPath getLocation() {
		return null;
	}

	public IResource[] getMappedResources() throws CoreException {
		return null;
	}

	public String getMemento() throws CoreException {
		return null;
	}

	public Set getModes() throws CoreException {
		return null;
	}

	public String getName() {
		return ConsoleTestMessages.RefactoringTest_test_launch_config;
	}

	public ILaunchDelegate getPreferredDelegate(final Set modes) throws CoreException {
		return null;
	}

	public ILaunchConfigurationType getType() throws CoreException {
		return null;
	}

	public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
		return new TestWorkingCopy(this);
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isMigrationCandidate() throws CoreException {
		return false;
	}

	public boolean isReadOnly() {
		return false;
	}

	public boolean isWorkingCopy() {
		return false;
	}

	public ILaunch launch(final String mode, final IProgressMonitor monitor) throws CoreException {
		return null;
	}

	public ILaunch launch(final String mode, final IProgressMonitor monitor, final boolean build)
			throws CoreException {
		return null;
	}

	public ILaunch launch(final String mode, final IProgressMonitor monitor, final boolean build, final boolean register)
			throws CoreException {
		return null;
	}

	public void migrate() throws CoreException {

	}

	public boolean supportsMode(final String mode) throws CoreException {
		return false;
	}

	public Object getAdapter(final Class adapter) {
		return null;
	}

};
