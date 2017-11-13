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
package org.jboss.tools.hibernate.orm.test.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
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
import org.junit.Assert;

public class TestWorkingCopy implements ILaunchConfigurationWorkingCopy {

	private TestLaunchConfig parent;

	private Map<String, Object> attributes = new HashMap<String, Object>();

	public TestWorkingCopy(TestLaunchConfig parent) {
		this.parent = parent;
	}

	public void addModes(Set<String> modes) {
	}

	public ILaunchConfiguration doSave() throws CoreException {
		parent.updatedAttributes.putAll(attributes);
		return parent;
	}

	public boolean hasAttribute(String attributeName) throws CoreException {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
		return false;
	}

	public Object removeAttribute(String attributeName) {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
		return null;
	}

	public ILaunchConfiguration getOriginal() {
		return parent;
	}

	public ILaunchConfigurationWorkingCopy getParent() {
		return null;
	}

	public boolean isDirty() {
		return true;
	}

	public void removeModes(Set<String> modes) {
	}

	public void rename(String name) {
	}

	public void setAttribute(String attributeName, int value) {
	}

	public void setAttribute(String attributeName, String value) {
		attributes.put(attributeName, value);
	}

	public void setAttribute(String attributeName, List<String> value) {
		attributes.put(attributeName, value);
	}

	public void setAttribute(String attributeName, Map<String, String> value) {
		attributes.put(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		attributes.put(attributeName, value);
	}

	public void setAttributes(Map<String, ? extends Object> attribs) {
		attributes.putAll(attribs);
	}

	public void setContainer(IContainer container) {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
	}

	public void setMappedResources(IResource[] resources) {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
	}

	public void setModes(Set<String> modes) {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
	}

	public void setPreferredLaunchDelegate(Set<String> modes, String delegateId) {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
	}

	public boolean contentsEqual(ILaunchConfiguration configuration) {
		return false;
	}

	public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
		return null;
	}

	public void delete() throws CoreException {
	}

	public boolean exists() {
		return false;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
		return parent.getAttribute(attributeName, defaultValue);
	}

	public int getAttribute(String attributeName, int defaultValue) throws CoreException {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
		return 0;
	}

	public List<String> getAttribute(String attributeName, List<String> defaultValue) throws CoreException {
		return parent.getAttribute(attributeName, defaultValue);
	}

	public Set<String> getAttribute(String attributeName, Set<String> defaultValue) throws CoreException {
		Assert.fail(TestConsoleMessages.RefactoringTest_method_not_tested);
		return null;
	}

	public Map<String, String> getAttribute(String attributeName, Map<String, String> defaultValue) throws CoreException {
		return parent.getAttribute(attributeName, defaultValue);
	}

	public String getAttribute(String attributeName, String defaultValue) throws CoreException {
		return parent.getAttribute(attributeName, defaultValue);
	}

	public Map<String, Object> getAttributes() throws CoreException {
		return attributes;
	}

	public String getCategory() throws CoreException {
		return parent.getCategory();
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

	public Set<String> getModes() throws CoreException {
		return null;
	}

	public String getName() {
		return null;
	}

	public ILaunchDelegate getPreferredDelegate(Set<String> modes) throws CoreException {
		return null;
	}

	public ILaunchConfigurationType getType() throws CoreException {
		return null;
	}

	public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
		return null;
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

	public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	public ILaunch launch(String mode, IProgressMonitor monitor, boolean build)
			throws CoreException {
		return null;
	}

	public ILaunch launch(String mode, IProgressMonitor monitor, boolean build, boolean register)
			throws CoreException {
		return null;
	}

	public void migrate() throws CoreException {
	}

	public boolean supportsMode(String mode) throws CoreException {
		return false;
	}

	public <T> T  getAdapter(Class<T> adapter) {
		return null;
	}

	public void setAttribute(String attributeName, Set<String> value) {
		attributes.put(attributeName, value);
	}

}
