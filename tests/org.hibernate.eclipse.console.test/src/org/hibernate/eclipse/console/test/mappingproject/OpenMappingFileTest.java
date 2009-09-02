/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.Utils;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PersistentClassWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PropertyWorkbenchAdapter;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingFileTest extends TestCase {

	protected String consoleConfigName = null;
	
	protected IPackageFragment testPackage = null; 

	protected int openEditors = 0;

	public OpenMappingFileTest() {
	}

	public OpenMappingFileTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
		consoleConfigName = null;
		testPackage = null;
		closeAllEditors();
	}

	public void testOpenMappingFileTest() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(consoleConfigName);
		assertNotNull(consCFG);
		consCFG.reset();
		Object[] configs = null;
		Object[] persClasses = null;
		Object[] props = null;
		try {
			configs = new ConsoleConfigurationWorkbenchAdapter().getChildren(consCFG);
			assertTrue(configs[0] instanceof Configuration);
			persClasses = new ConfigurationWorkbenchAdapter().getChildren(configs[0]);
		} catch (InvalidMappingException ex){
			String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_mapping_files_for_package_cannot_be_opened,
					new Object[]{testPackage.getElementName(), ex.getMessage()});
			fail(out);
		}
		if (persClasses.length > 0) {
			final String testClass = "class"; //$NON-NLS-1$
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[i] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];
				openTest(persClass, consCFG);
				props =  new PersistentClassWorkbenchAdapter().getChildren(persClass);
				for (int j = 0; j < props.length; j++) {
					if (props[j].getClass() != Property.class) {
						continue;
					}
					openTest(props[j], consCFG);
					Object[] compProperties = new PropertyWorkbenchAdapter().getChildren(props[j]);
					for (int k = 0; k < compProperties.length; k++) {
						//test Composite properties
						if (compProperties[k].getClass() != Property.class) {
							continue;
						}
						final Property prop = (Property)compProperties[k];
						if (testClass.equals(prop.getNodeName()) || testClass.equals(prop.getName())) {
							continue;
						}
						openPropertyTest((Property)compProperties[k], (Property) props[j], consCFG);
					}
				}
			}
		}
		//close all editors
	}

	private void openPropertyTest(Property compositeProperty, Property parentProperty, ConsoleConfiguration consCFG){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(consCFG, compositeProperty, parentProperty);
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_highlighted_region_for_property_is_empty_package,
						new Object[]{compositeProperty.getNodeName(), testPackage.getElementName()});
				fail(out);
			}
			Object[] compProperties = new PropertyWorkbenchAdapter().getChildren(compositeProperty);
			for (int k = 0; k < compProperties.length; k++) {
				//test Composite properties
				assertTrue(compProperties[k] instanceof Property);
				// use only first level to time safe
				//openPropertyTest((Property)compProperties[k], compositeProperty, consCFG);
			}
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_mapping_file_for_property_not_opened_package,
					new Object[]{compositeProperty.getNodeName(), testPackage.getElementName(), ex.getMessage()});
			fail(out);
		}
	}

	private void openTest(Object selection, ConsoleConfiguration consCFG){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(consCFG, selection, null);
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_highlighted_region_for_is_empty_package,
						new Object[]{selection, testPackage.getElementName()});
				fail(out);
			}
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_mapping_file_for_not_opened_package,
					new Object[]{selection, testPackage.getElementName(), ex.getMessage()});
			fail(out);
		}
	}

	public String getConsoleConfigName() {
		return consoleConfigName;
	}

	public void setConsoleConfigName(String consoleConfigName) {
		this.consoleConfigName = consoleConfigName;
	}

	public IPackageFragment getTestPackage() {
		return testPackage;
	}

	public void setTestPackage(IPackageFragment testPackage) {
		this.testPackage = testPackage;
	}
	
	protected void closeAllEditors() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				openEditors += workbenchPage.getEditorReferences().length;
				workbenchPage.closeAllEditors(false);
			}
		}
		// clean up event queue to avoid "memory leak",
		// this is necessary to fix https://jira.jboss.org/jira/browse/JBIDE-4824
		while (Display.getCurrent().readAndDispatch());
	}
}
