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

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingFileTest extends TestCase {
	
	public void testOpenMappingFileTest() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(ProjectUtil.ConsoleCFGName);
		assertNotNull(consCFG);
		consCFG.reset();
		Object[] configs = null;
		Object[] persClasses = null;
		try {
			configs = new ConsoleConfigurationWorkbenchAdapter().getChildren(consCFG);
			assertTrue(configs[0] instanceof Configuration);
			persClasses = new ConfigurationWorkbenchAdapter().getChildren(configs[0]);
		} catch (InvalidMappingException ex){
			fail("Mapping Files for package " + HibernateAllMappingTests.getActivePackage().getElementName()
					+ " can't be opened:\n " + ex.getMessage());
		}
		if (persClasses.length > 0){
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[0] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];
				IEditorPart editor = null;
				Throwable ex = null;
				try {
					editor = OpenMappingAction.run(persClass, consCFG);
				} catch (PartInitException e) {
					ex = e;
				} catch (JavaModelException e) {
					ex = e;
				} catch (FileNotFoundException e) {
					ex = e;
				}				
				if (ex == null ) ex = ProjectUtil.getExceptionIfItOccured(editor);
				if (ex != null) fail("Mapping file for " + persClass.getClassName()
						+ " not opened:\n" + ex.getMessage());
			}
		}
		//close all editors
	}
}
