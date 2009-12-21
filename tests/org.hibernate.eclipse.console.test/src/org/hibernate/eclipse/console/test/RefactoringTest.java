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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.DefaultProjectClasspathEntry;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.eclipse.launch.core.refactoring.HibernateRefactoringUtil;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class RefactoringTest extends TestCase {

	private static final String HBMTEMPLATE0 = "hbmtemplate0";
	private static final String HBMTEMPLATE0_PROPERTIES = HibernateLaunchConstants.ATTR_EXPORTERS + '.' + HBMTEMPLATE0 + ".properties";
	private final String[] oldPathElements = new String[]{"oldPrj","oldSrc", "oldPack", "oldHibernate.cfg.xml"};	  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private final String[] newPathElements = new String[]{"newPrj","newSrc", "newPack", "newHibernate.cfg.xml"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	SimpleTestProject project = null;

	private TestLaunchConfig testStrConfig  = null;
	private TestLaunchConfig testStrListConfig  = null;
	private TestLaunchConfig testNotChangedConfig  = null;
	private TestLaunchConfig testCodeGenerationConfig = null;
	private TestLaunchConfig testNotChangedCodeGenerationConfig = null;


	private IRuntimeClasspathEntry[] runtimeClasspathEntries = null;
	private int[] affectSegmentsCount = null; // shows how much segments should affect path

	private String oldPathStr = null;

	@Override
	protected void setUp() throws Exception {
		oldPathStr = oldPathElements[0];
		String notChangedPathStr = oldPathElements[0] + 1;
		for (int i = 1; i < oldPathElements.length; i++) {
			oldPathStr += "/" + oldPathElements[i]; //$NON-NLS-1$
			notChangedPathStr += "/" + oldPathElements[i] + 1; //$NON-NLS-1$
		}
		Map<String, Object> testStrAttr = new HashMap<String, Object>();
		Map<String, Object> testStrListAttr = new HashMap<String, Object>();
		Map<String, Object> testNotChangedAttr = new HashMap<String, Object>();
		Map<String, Object> testCodeGenerationAttr = new HashMap<String, Object>();
		Map<String, Object> testNotChangedCodeGenerationAttr = new HashMap<String, Object>();

		testStrAttr.put(IConsoleConfigurationLaunchConstants.CFG_XML_FILE, oldPathStr);

		testStrListAttr.put(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Arrays.asList(new String[]{oldPathStr}));

		testNotChangedAttr.put(IConsoleConfigurationLaunchConstants.CFG_XML_FILE, notChangedPathStr);
		testNotChangedAttr.put(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Arrays.asList(new String[]{notChangedPathStr}));

		testCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, generateOldPathForSegment(2).toString());
		testCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, generateOldPathForSegment(2).toString());
		testCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, oldPathStr.toString());
		testCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_EXPORTERS, Collections.singletonList(HBMTEMPLATE0));
		Map<String, String> expProps = new HashMap<String, String>();
		expProps.put("outputdir", generateOldPathForSegment(2).toString());
		testCodeGenerationAttr.put(HBMTEMPLATE0_PROPERTIES,	expProps);
		
		testNotChangedCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, notChangedPathStr.toString());
		testNotChangedCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, notChangedPathStr.toString());
		testNotChangedCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, notChangedPathStr.toString());
		testCodeGenerationAttr.put(HibernateLaunchConstants.ATTR_EXPORTERS, Collections.singletonList(HBMTEMPLATE0));
		Map<String, String> expProps2 = new HashMap<String, String>();
		expProps2.put("outputdir", generateOldPathForSegment(2).toString());
		testNotChangedCodeGenerationAttr.put(HBMTEMPLATE0_PROPERTIES, expProps2);

		
		testStrConfig = new TestLaunchConfig(testStrAttr);
		testStrListConfig = new TestLaunchConfig(testStrListAttr);
		testNotChangedConfig = new TestLaunchConfig(testNotChangedAttr);
		testCodeGenerationConfig = new TestLaunchConfig(testCodeGenerationAttr);
		testNotChangedCodeGenerationConfig = new TestLaunchConfig(testNotChangedCodeGenerationAttr);
		
		project = new SimpleTestProject(oldPathElements[0]);
		IJavaProject proj = project.getIJavaProject();

		{	//initialize IRuntimeClassPathEntry[] and affectSegmentsCount for it
			runtimeClasspathEntries = new IRuntimeClasspathEntry[5];
			affectSegmentsCount = new int[runtimeClasspathEntries.length];
			affectSegmentsCount[0] = 0;
			affectSegmentsCount[1] = 0;
			affectSegmentsCount[2] = 1;
			affectSegmentsCount[3] = 2;
			affectSegmentsCount[4] = oldPathElements.length - 1; //all changes

			runtimeClasspathEntries[0] = new DefaultProjectClasspathEntry(proj);//project
			runtimeClasspathEntries[1] = new RuntimeClasspathEntry( JavaCore.newProjectEntry(generateOldPathForSegment(affectSegmentsCount[1]).makeAbsolute()));
			runtimeClasspathEntries[2] = new RuntimeClasspathEntry( JavaCore.newVariableEntry(generateOldPathForSegment(affectSegmentsCount[2]).makeAbsolute(), null, null));
			runtimeClasspathEntries[3] = new RuntimeClasspathEntry( JavaCore.newVariableEntry(generateOldPathForSegment(affectSegmentsCount[3]).makeAbsolute(), null, null));
			runtimeClasspathEntries[4] = new RuntimeClasspathEntry( JavaCore.newLibraryEntry(generateOldPathForSegment(affectSegmentsCount[4]).makeAbsolute(), null, null));
		}
	}

	public void testFindChanged(){
		for (int i = 0; i < oldPathElements.length - 1; i++) {
			IPath oldPathPart = generateOldPathForSegment(i);
			try {
				assertTrue(HibernateRefactoringUtil.isConsoleConfigAffected(testStrConfig, oldPathPart));
				assertTrue(HibernateRefactoringUtil.isConsoleConfigAffected(testStrListConfig, oldPathPart));
				assertFalse(HibernateRefactoringUtil.isConsoleConfigAffected(testNotChangedConfig, oldPathPart));
				assertTrue(HibernateRefactoringUtil.isCodeGenerationConfigAffected(testCodeGenerationConfig, oldPathPart));
				assertFalse(HibernateRefactoringUtil.isConsoleConfigAffected(testNotChangedCodeGenerationConfig, oldPathPart));
			} catch (CoreException e) {
				fail(ConsoleTestMessages.RefactoringTest_exception_while_findchange_launch_config_processing + e.getMessage());
			}
		}
	}

	public void testClassPathChanges(){
		for (int i = 0; i < oldPathElements.length - 1; i++) {
			IPath oldPathPart = generateOldPathForSegment(i);
			for (int j = 0; j < runtimeClasspathEntries.length; j++) {
				if (i <= affectSegmentsCount[j]){
					assertTrue(HibernateRefactoringUtil.isRuntimeClassPathEntriesAffected(new IRuntimeClasspathEntry[]{runtimeClasspathEntries[j]}, oldPathPart));
					try {
						IPath newPath = generateNewPathForSegment(i);
						String oldMemento = runtimeClasspathEntries[j].getMemento();
						List<String> oldMementos = new ArrayList<String>();
						List<String> newMementos = new ArrayList<String>();
						oldMementos.add(runtimeClasspathEntries[j].getMemento());
						boolean isChanged = HibernateRefactoringUtil.updateClasspathEntries(new IRuntimeClasspathEntry[]{runtimeClasspathEntries[j]}, oldMementos, newMementos, oldPathPart, newPath);
						assertTrue(isChanged);
						String newMemento = newMementos.get(0);
						checkMementoChanged(oldMemento, newMemento, oldPathPart, newPath);
					} catch (CoreException e) {
						fail(ConsoleTestMessages.RefactoringTest_coreexception_occurred_work_with_memento + e.getMessage());
					}
				} else {
					assertFalse(HibernateRefactoringUtil.isRuntimeClassPathEntriesAffected(new IRuntimeClasspathEntry[]{runtimeClasspathEntries[j]}, oldPathPart));
				}
			}
		}
	}

	public void testProjectNameChange(){
		int segmentNum = 0;
		try {
			updatePaths(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			Path truePath = generateTruePathForSegment(segmentNum);
			checkPaths(truePath);
			checkAdditional(truePath);	
		} catch (CoreException e) {
			fail(ConsoleTestMessages.RefactoringTest_exception_while_projnamechange_refactor);
		}
	}

	public void testSrcNameChange(){
		int segmentNum = 1;
		try {
			updatePaths(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			Path truePath = generateTruePathForSegment(segmentNum);
			checkPaths(truePath);
			checkAdditional(truePath);		
		} catch (CoreException e) {
			fail(ConsoleTestMessages.RefactoringTest_exception_while_srcnamechange_refactor);
		}
	}

	public void testPackNameChange(){
		int segmentNum = 2;
		try {
			updatePaths(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			Path truePath = generateTruePathForSegment(segmentNum);
			checkPaths(truePath);
			checkAdditional(truePath);
		} catch (CoreException e) {
			fail(ConsoleTestMessages.RefactoringTest_exception_while_packnamechange_refactor);
		}
	}

	public void testFileNameChange(){
		int segmentNum = 3;
		try {
			updatePaths(generateOldPathForSegment(segmentNum), generateNewPathForSegment(segmentNum));
			checkPaths(generateTruePathForSegment(segmentNum));
		} catch (CoreException e) {
			fail(ConsoleTestMessages.RefactoringTest_exception_while_filenamechange_refactor);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		project.deleteIProject();
		project = null;
		testStrConfig  = null;
		testStrListConfig  = null;
		testNotChangedConfig  = null;
		runtimeClasspathEntries = null;
		affectSegmentsCount = null;
		oldPathStr = null;
	}

	private void updatePaths(Path oldPath, Path newPath) throws CoreException{
		HibernateRefactoringUtil.updateConsoleConfig(testStrConfig, oldPath, newPath);
		HibernateRefactoringUtil.updateConsoleConfig(testStrListConfig, oldPath, newPath);
		HibernateRefactoringUtil.updateCodeGenerationConfig(testCodeGenerationConfig, oldPath, newPath);
	}

	private void checkMementoChanged(String oldMemento, String newMemento, IPath oldPathPart, IPath newPath){
		/*
		 * /myProj/mySrc, \myProj\mySrc, myProj/mySrc, myProj\mySrc, myProj/mySrc/, myProj\mySrc\
		 * mean the same path and should be represented as myProj[\\\\,/]mySrc regular expression
		 */
		String oldPathStr = oldPathPart.toOSString().replaceAll("\\\\", "/"); //replace all "\" on "/" //$NON-NLS-1$ //$NON-NLS-2$
		String newPathStr = newPath.toOSString().replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		if (oldPathStr.charAt(0) == '/') oldPathStr = oldPathStr.substring(1); //remove first slash
		if (newPathStr.charAt(0) == '/') newPathStr = newPathStr.substring(1);
		if (oldPathStr.charAt(oldPathStr.length() - 1) == '/') oldPathStr = oldPathStr.substring(0, oldPathStr.length() - 1); //remove last slash
		if (newPathStr.charAt(newPathStr.length() - 1) == '/') newPathStr = newPathStr.substring(0 , newPathStr.length() - 1);
		oldPathStr = oldPathStr.replaceAll("/", "[\\\\,/]?"); //$NON-NLS-1$ //$NON-NLS-2$
		newPathStr = newPathStr.replaceAll("/", "[\\\\,/]?");  //$NON-NLS-1$//$NON-NLS-2$

		int count_old = 0;
		int count_new = 0;
		int last_index = 0;
		while(last_index >= 0){
			last_index = oldMemento.indexOf(oldPathStr, last_index);
			if (last_index >= 0){
				++count_old;
				++last_index;
			}
		}
		last_index = 0;
		while(last_index >= 0){
			last_index = newMemento.indexOf(newPathStr, last_index);
			if (last_index >= 0){
				++count_new;
				++last_index;
			}
		}
		assertTrue(count_new == count_old);
	}

	@SuppressWarnings("unchecked")
	private void checkPaths(Path truePath) throws CoreException{
		String newPath = (String) testStrConfig.getNewAttribute(IConsoleConfigurationLaunchConstants.CFG_XML_FILE);
		assertEquals(truePath.makeAbsolute(), new Path(newPath).makeAbsolute());
		newPath = ((List<String>) testStrListConfig.getNewAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS)).get(0);
		assertEquals(truePath.makeAbsolute(), new Path(newPath).makeAbsolute());
		
		newPath = (String) testCodeGenerationConfig.getNewAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS);
		assertEquals(truePath.makeAbsolute(), new Path(newPath).makeAbsolute());
	}
	
	@SuppressWarnings("unchecked")
	private void checkAdditional(Path truePath) throws CoreException {
		String newPath = (String) testCodeGenerationConfig.getNewAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR);
		assertEquals(truePath.removeLastSegments(1).makeAbsolute(), new Path(newPath).makeAbsolute());
		newPath = (String) testCodeGenerationConfig.getNewAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR);
		assertEquals(truePath.removeLastSegments(1).makeAbsolute(), new Path(newPath).makeAbsolute());
		Map<String, String> props = testCodeGenerationConfig.getAttribute(HBMTEMPLATE0_PROPERTIES,
				new HashMap<String, String>());
		assertEquals(truePath.removeLastSegments(1).makeAbsolute(), new Path(props.get("outputdir")).makeAbsolute());
	}

	private Path generateNewPathForSegment(int segmentNum){
		assertTrue(ConsoleTestMessages.RefactoringTest_segmentnum_too_match, segmentNum < oldPathElements.length);
		String newPath = ""; //$NON-NLS-1$
		for (int i = 0; i < segmentNum; i++) {
			newPath += oldPathElements[i] + "/"; //$NON-NLS-1$
		}
		newPath += newPathElements[segmentNum];
		return new Path(newPath);
	}

	private Path generateOldPathForSegment(int segmentNum){
		assertTrue(ConsoleTestMessages.RefactoringTest_segmentnum_too_match, segmentNum < oldPathElements.length);
		String oldPathPart = ""; //$NON-NLS-1$
		for (int i = 0; i <= segmentNum; i++) {
			oldPathPart += oldPathElements[i] + "/"; //$NON-NLS-1$
		}
		return new Path(oldPathPart);
	}

	private Path generateTruePathForSegment(int segmentNum){
		assertTrue(ConsoleTestMessages.RefactoringTest_segmentnum_too_match, segmentNum < oldPathElements.length);
		String newPath = ""; //$NON-NLS-1$
		for (int i = 0; i < oldPathElements.length; i++) {
			if (i != segmentNum){
				newPath += oldPathElements[i] + "/"; //$NON-NLS-1$
			} else
				newPath += newPathElements[i] + "/"; //$NON-NLS-1$
		}
		return new Path(newPath);
	}

//====================================================================================
		@SuppressWarnings("unchecked")
		static class TestWorkingCopy implements ILaunchConfigurationWorkingCopy{

			private TestLaunchConfig parent;

			private Map<String, Object> attributes = new HashMap<String, Object>();

			TestWorkingCopy(TestLaunchConfig parent){
				this.parent = parent;
			}

			public void addModes(Set modes) {}

			public ILaunchConfiguration doSave() throws CoreException {
				parent.updatedAttributes.putAll(attributes);
				return parent;
			}

			public boolean hasAttribute(String attributeName)
					throws CoreException {
				fail(ConsoleTestMessages.RefactoringTest_method_not_tested);
				return false;
			}

			public Object removeAttribute(String attributeName) {
				fail(ConsoleTestMessages.RefactoringTest_method_not_tested);
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

			
			public void removeModes(Set modes) {}

			public void rename(String name) {}

			public void setAttribute(String attributeName, int value) {	}

			public void setAttribute(String attributeName, String value) {
				attributes.put(attributeName, value);
			}

			public void setAttribute(String attributeName, List value) {
				attributes.put(attributeName, value);
			}

			public void setAttribute(String attributeName, Map value) {
				attributes.put(attributeName, value);
			}

			public void setAttribute(String attributeName, boolean value) {fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public void setAttributes(Map attributes) {	fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public void setContainer(IContainer container) {fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public void setMappedResources(IResource[] resources) {fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public void setModes(Set modes) {fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public void setPreferredLaunchDelegate(Set modes, String delegateId) {fail(ConsoleTestMessages.RefactoringTest_method_not_tested);}

			public boolean contentsEqual(ILaunchConfiguration configuration) {
				return false;
			}

			public ILaunchConfigurationWorkingCopy copy(String name)
					throws CoreException {
				fail(ConsoleTestMessages.RefactoringTest_method_not_tested);
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
				fail(ConsoleTestMessages.RefactoringTest_method_not_tested);
				return 0;
			}

			public List getAttribute(String attributeName, List defaultValue)
					throws CoreException {
				return parent.getAttribute(attributeName, defaultValue);
			}

			public Set getAttribute(String attributeName, Set defaultValue)
					throws CoreException {
				fail(ConsoleTestMessages.RefactoringTest_method_not_tested);
				return null;
			}

			public Map getAttribute(String attributeName, Map defaultValue)
					throws CoreException {
				return parent.getAttribute(attributeName, defaultValue);
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

		@SuppressWarnings("unchecked")
		static class TestLaunchConfig implements ILaunchConfiguration{

			private Map<String, Object> attributes = new HashMap<String, Object>();

			public Map<String, Object> updatedAttributes = new HashMap<String, Object>();

			// returns updated attribute
			public Object getNewAttribute(String attributeName){
				return updatedAttributes.get(attributeName);
			}

			TestLaunchConfig(Map<String, Object> attributes){
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

			public boolean hasAttribute(String attributeName)
					throws CoreException {
				return attributes.containsKey(attributeName);
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
				if (attributes.containsKey(attributeName)){
					return (Map) attributes.get(attributeName);
				} else {
					return defaultValue;
				}
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
			}


		};
}


