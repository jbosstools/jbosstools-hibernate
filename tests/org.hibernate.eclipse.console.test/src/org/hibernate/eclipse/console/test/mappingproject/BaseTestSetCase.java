/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PersistentClassWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PropertyWorkbenchAdapter;

import junit.framework.TestCase;

/**
 * @author vy (vyemialyanchyk@gmail.com)
 */
public class BaseTestSetCase extends TestCase {

	protected int openEditors = 0;
	
	protected String consoleConfigName = null;
	
	protected IPackageFragment testPackage = null; 
	
	final static protected ConsoleConfigurationWorkbenchAdapter ccWorkbenchAdapter = new ConsoleConfigurationWorkbenchAdapter(); 
	
	final static protected ConfigurationWorkbenchAdapter configWorkbenchAdapter = new ConfigurationWorkbenchAdapter(); 
	
	final static protected PersistentClassWorkbenchAdapter pcWorkbenchAdapter = new PersistentClassWorkbenchAdapter(); 

	final static protected PropertyWorkbenchAdapter propertyWorkbenchAdapter = new PropertyWorkbenchAdapter(); 

	public BaseTestSetCase() {
	}

	public BaseTestSetCase(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
		consoleConfigName = null;
		testPackage = null;
		closeAllEditors();
	}

	public Object[] getPersistenceClasses(boolean resetCC) {
		final ConsoleConfiguration consCFG = getConsoleConfig();
		if (resetCC) {
			consCFG.reset();
			consCFG.build();
		}
		assertTrue(consCFG.hasConfiguration());
		if (resetCC) {
			consCFG.execute(new ExecutionContext.Command() {
				public Object execute() {
					if (consCFG.hasConfiguration()) {
						consCFG.getConfiguration().buildMappings();
					}
					return consCFG;
				}
			});
		}
		Object[] configs = null;
		Object[] persClasses = null;
		try {
			configs = ccWorkbenchAdapter.getChildren(consCFG);
			assertNotNull(configs);
			assertEquals(3, configs.length);
			assertTrue(configs[0] instanceof Configuration);
			persClasses = configWorkbenchAdapter.getChildren(configs[0]);
		} catch (InvalidMappingException ex) {
			String out = NLS.bind(ConsoleTestMessages.OpenMappingDiagramTest_mapping_diagrams_for_package_cannot_be_opened,
				new Object[] { testPackage.getElementName(), ex.getMessage() });
			fail(out);
		}
		return persClasses;
	}

	public ConsoleConfiguration getConsoleConfig() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(consoleConfigName);
		assertNotNull(consCFG);
		return consCFG;
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
