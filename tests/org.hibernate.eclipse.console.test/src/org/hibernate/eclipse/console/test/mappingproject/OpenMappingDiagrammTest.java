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

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.ui.view.views.OpenDiagramActionDelegate;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingDiagrammTest extends TestCase {
	public void testOpenMappingDiagramm() throws Throwable{
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(ProjectUtil.ConsoleCFGName);
		assertNotNull(consCFG);
		consCFG.reset();
		Object[] configs = new ConsoleConfigurationWorkbenchAdapter().getChildren(consCFG);
		assertTrue(configs[0] instanceof Configuration);
		Object[] persClasses = new ConfigurationWorkbenchAdapter().getChildren(configs[0]);
		if (persClasses.length > 0){
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[0] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];
				try {
					IEditorPart editor = new OpenDiagramActionDelegate().openEditor(persClass, consCFG);
					ProjectUtil.throwExceptionIfItOccured(editor);
				} catch (PartInitException e) {
					fail("Error opening Mapping Diagramm for class " + persClass.getNodeName()
							+ ".\n"+ e.getMessage());
				}
			}			
		}
		//close all editors
	}

	
}
