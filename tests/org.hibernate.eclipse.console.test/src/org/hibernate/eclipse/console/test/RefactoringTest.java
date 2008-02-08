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
package org.hibernate.eclipse.console.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.eclipse.launch.core.refactoring.HibernateRefactoringUtil;

/**
 * @author Dmitry Geraskov
 *
 */
public class RefactoringTest extends TestCase {
	
	private final String[] oldPathElements = new String[]{"oldPrj","oldSrc", "oldPack", "oldHibernate.cfg.xml"};	
	private final String[] newPathElements = new String[]{"newPrj","newSrc", "newPack", "newHibernate.cfg.xml"};	
			
	private TestLaunchConfig testStrConfig  = null;
	private TestLaunchConfig testStrListConfig  = null;
	private TestLaunchConfig testNotChangedConfig  = null;
	private String oldPathStr = null;
	private Path oldPath = null;
		
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		oldPathStr = oldPathElements[0];
		String notChangedPathStr = oldPathElements[0] + 1;
		for (int i = 1; i < oldPathElements.length; i++) {
			oldPathStr += "/" + oldPathElements[i];
			notChangedPathStr += "/" + oldPathElements[i] + 1;
		}
		Map testStrAttr = new HashMap();
		Map testStrListAttr = new HashMap();
		Map testNotChangedAttr = new HashMap();
		
		testStrAttr.put(IConsoleConfigurationLaunchConstants.CFG_XML_FILE, oldPathStr);
		
		testStrListAttr.put(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Arrays.asList(new String[]{oldPathStr}));
		
		testNotChangedAttr.put(IConsoleConfigurationLaunchConstants.CFG_XML_FILE, notChangedPathStr);
		testNotChangedAttr.put(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Arrays.asList(new String[]{notChangedPathStr}));
	
		testStrConfig = new TestLaunchConfig(testStrAttr);
		testStrListConfig = new TestLaunchConfig(testStrListAttr);
		testNotChangedConfig = new TestLaunchConfig(testNotChangedAttr);
		oldPath = new Path(oldPathStr);
	}
	
	public void testFindChanged(){
		try {
			assertTrue(HibernateRefactoringUtil.isConfigurationChanged(testStrConfig, oldPath));
			assertTrue(HibernateRefactoringUtil.isConfigurationChanged(testStrListConfig, oldPath));
			assertTrue(!HibernateRefactoringUtil.isConfigurationChanged(testNotChangedConfig, oldPath));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}
	
	public void testProjectNameChange(){
		int segmentNum = 0;
		try {
			updateConfigs(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			checkPaths(generateTruePathForSegment(segmentNum));
		} catch (CoreException e) {
			fail("Exception while ProjectNameChange refactor processing!");
		}
	}
	
	public void testSrcNameChange(){
		int segmentNum = 1;
		try {
			updateConfigs(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			checkPaths(generateTruePathForSegment(segmentNum));
		} catch (CoreException e) {
			fail("Exception while SrcNameChange refactor processing!");
		}
	}
	
	public void testPackNameChange(){
		int segmentNum = 2;
		try {
			updateConfigs(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			checkPaths(generateTruePathForSegment(segmentNum));
		} catch (CoreException e) {
			fail("Exception while PackNameChange refactor processing!");
		}
	}
	
	public void testFileNameChange(){
		int segmentNum = 3;
		try {
			updateConfigs(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			checkPaths(generateTruePathForSegment(segmentNum));
		} catch (CoreException e) {
			fail("Exception while FileNameChange refactor processing!");
		}
	}
	
	private void updateConfigs(Path oldPath, Path newPath) throws CoreException{
		HibernateRefactoringUtil.updateLaunchConfig(testStrConfig, oldPath, newPath);
		HibernateRefactoringUtil.updateLaunchConfig(testStrListConfig, oldPath, newPath);
	}
	
	private void checkPaths(Path truePath) throws CoreException{
		String newPath = (String) testStrConfig.getNewAttribute(IConsoleConfigurationLaunchConstants.CFG_XML_FILE);
		assertEquals(truePath, new Path(newPath));
		newPath = (String) ((List) testStrListConfig.getNewAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS)).get(0);
		assertEquals(truePath, new Path(newPath));
	}
	
	private Path generateNewPathForSegment(int segmentNum){
		assertTrue("SegmentNum is too mach.", segmentNum < oldPathElements.length);
		String newPath = "";
		for (int i = 0; i < segmentNum; i++) {
			newPath += oldPathElements[i] + "/";
		}
		newPath += newPathElements[segmentNum];
		return new Path(newPath);
	}
	
	private Path generateOldPathForSegment(int segmentNum){
		assertTrue("SegmentNum is too mach.", segmentNum < oldPathElements.length);
		String oldPathPart = "";
		for (int i = 0; i <= segmentNum; i++) {
			oldPathPart += oldPathElements[i] + "/";
		}
		return new Path(oldPathPart);
	}
	
	private Path generateTruePathForSegment(int segmentNum){
		assertTrue("SegmentNum is too mach.", segmentNum < oldPathElements.length);
		String newPath = "";
		for (int i = 0; i < oldPathElements.length; i++) {
			if (i != segmentNum){
				newPath += oldPathElements[i] + "/";
			} else
				newPath += newPathElements[i] + "/";
		}
		return new Path(newPath);
	}
	
	
	
	
	
		class TestWorkingCopy implements ILaunchConfigurationWorkingCopy{
			
			private TestLaunchConfig parent;
			
			private Map attributes = new HashMap();
			
			TestWorkingCopy(TestLaunchConfig parent){
				this.parent = parent;
			}

			public void addModes(Set modes) {}

			public ILaunchConfiguration doSave() throws CoreException {
				parent.updatedAttributes.putAll(attributes);
				return parent;
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

			public void removeModes(Set modes) {}

			public void rename(String name) {}

			public void setAttribute(String attributeName, int value) {	}

			public void setAttribute(String attributeName, String value) {
				attributes.put(attributeName, value);
			}

			public void setAttribute(String attributeName, List value) {
				attributes.put(attributeName, value);
			}

			public void setAttribute(String attributeName, Map value) {fail("Method doesn't tested");}

			public void setAttribute(String attributeName, boolean value) {fail("Method doesn't tested");}

			public void setAttributes(Map attributes) {	fail("Method doesn't tested");}

			public void setContainer(IContainer container) {fail("Method doesn't tested");}

			public void setMappedResources(IResource[] resources) {fail("Method doesn't tested");}

			public void setModes(Set modes) {fail("Method doesn't tested");}

			public void setPreferredLaunchDelegate(Set modes, String delegateId) {fail("Method doesn't tested");}

			public boolean contentsEqual(ILaunchConfiguration configuration) {			
				return false;
			}

			public ILaunchConfigurationWorkingCopy copy(String name)
					throws CoreException {
				fail("Method doesn't tested");
				return null;
			}

			public void delete() throws CoreException {}

			public boolean exists() {			
				return false;
			}

			public boolean getAttribute(String attributeName, boolean defaultValue)
					throws CoreException {			
				return parent.getAttribute(attributeName, defaultValue);
			}

			public int getAttribute(String attributeName, int defaultValue)
					throws CoreException {	
				fail("Method doesn't tested");
				return 0;
			}

			public List getAttribute(String attributeName, List defaultValue)
					throws CoreException {			
				return parent.getAttribute(attributeName, defaultValue);
			}

			public Set getAttribute(String attributeName, Set defaultValue)
					throws CoreException {	
				fail("Method doesn't tested");
				return null;
			}

			public Map getAttribute(String attributeName, Map defaultValue)
					throws CoreException {	
				fail("Method doesn't tested");
				return null;
			}

			public String getAttribute(String attributeName, String defaultValue)
					throws CoreException {			
				return parent.getAttribute(attributeName, defaultValue);
			}

			public Map getAttributes() throws CoreException {
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

			public Set getModes() throws CoreException {			
				return null;
			}

			public String getName() {			
				return null;
			}

			public ILaunchDelegate getPreferredDelegate(Set modes)
					throws CoreException {			
				return null;
			}

			public ILaunchConfigurationType getType() throws CoreException {			
				return null;
			}

			public ILaunchConfigurationWorkingCopy getWorkingCopy()
					throws CoreException {			
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

			public ILaunch launch(String mode, IProgressMonitor monitor)
					throws CoreException {			
				return null;
			}

			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build) throws CoreException {			
				return null;
			}

			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build, boolean register) throws CoreException {			
				return null;
			}

			public void migrate() throws CoreException {}

			public boolean supportsMode(String mode) throws CoreException {			
				return false;
			}

			public Object getAdapter(Class adapter) {			
				return null;
			}
			
		}
		
		class TestLaunchConfig implements ILaunchConfiguration{
			
			private Map attributes = new HashMap();
			
			public Map updatedAttributes = new HashMap();
			
			// returns updated attribute
			public Object getNewAttribute(String attributeName){
				return updatedAttributes.get(attributeName);	
			}
			
			TestLaunchConfig(Map attributes){
				if (attributes != null){
					this.attributes = attributes;
				}
			}

			public boolean contentsEqual(ILaunchConfiguration configuration) {
				return false;
			}

			public ILaunchConfigurationWorkingCopy copy(String name)
					throws CoreException {
				return null;
			}

			public void delete() throws CoreException {
			
			}

			public boolean exists() {
				return false;
			}

			public boolean getAttribute(String attributeName, boolean defaultValue)
					throws CoreException {
				if (attributes.containsKey(attributeName)){
					return (Boolean) attributes.get(attributeName);
				} else {
					return defaultValue;
				}
			}

			public int getAttribute(String attributeName, int defaultValue)
					throws CoreException {
				if (attributes.containsKey(attributeName)){
					return (Integer) attributes.get(attributeName);
				} else {
					return defaultValue;
				}
			}

			public List getAttribute(String attributeName, List defaultValue)
					throws CoreException {
				if (attributes.containsKey(attributeName)){
					return (List) attributes.get(attributeName);
				} else {
					return defaultValue;
				}
			}

			public Set getAttribute(String attributeName, Set defaultValue)
					throws CoreException {
				return null;
			}

			public Map getAttribute(String attributeName, Map defaultValue)
					throws CoreException {
				return null;
			}

			public String getAttribute(String attributeName, String defaultValue)
					throws CoreException {
				if (attributes.containsKey(attributeName)){
					return (String) attributes.get(attributeName);
				} else {
					return defaultValue;
				}
			}	

			public Map getAttributes() throws CoreException {
				return null;
			}

			public String getCategory() throws CoreException {
				return "Categiry";
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
				return "Test Launch Config";
			}

			public ILaunchDelegate getPreferredDelegate(Set modes)
					throws CoreException {
				return null;
			}

			public ILaunchConfigurationType getType() throws CoreException {
				return null;
			}

			public ILaunchConfigurationWorkingCopy getWorkingCopy()
					throws CoreException {
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

			public ILaunch launch(String mode, IProgressMonitor monitor)
					throws CoreException {
				return null;
			}

			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build) throws CoreException {
				return null;
			}

			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build, boolean register) throws CoreException {
				return null;
			}

			public void migrate() throws CoreException {
			
			}

			public boolean supportsMode(String mode) throws CoreException {
				return false;
			}

			public Object getAdapter(Class adapter) {
				return null;
			}};
}


