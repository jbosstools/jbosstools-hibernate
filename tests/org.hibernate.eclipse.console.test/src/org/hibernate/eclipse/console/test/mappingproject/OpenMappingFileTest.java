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

import junit.framework.TestCase;

import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.actions.OpenSourceAction;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingFileTest extends TestCase {
	public void testOpenMappingFileTest(){
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(CreateConsoleConfigTest.ConsoleCFGName);
		assertNotNull(consCFG);
		consCFG.reset();
		Object[] configs = new ConsoleConfigurationWorkbenchAdapter().getChildren(consCFG);
		assertTrue(configs[0] instanceof Configuration);
		Object[] persClasses = new ConfigurationWorkbenchAdapter().getChildren(configs[0]);
		if (persClasses.length > 0){
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[0] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];
				OpenMappingAction.run(persClass, consCFG);
			}			
		}
		//close all editors
	}
}
