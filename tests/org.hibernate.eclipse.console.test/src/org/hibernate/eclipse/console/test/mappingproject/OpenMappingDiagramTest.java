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

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;
import org.hibernate.eclipse.console.test.utils.ProjectUtil;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.ui.view.views.OpenDiagramActionDelegate;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingDiagramTest extends TestCase {
	public void testOpenMappingDiagram() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(ConsoleConfigUtils.ConsoleCFGName);
		assertNotNull(consCFG);
		consCFG.reset();
		Object[] configs = null;
		Object[] persClasses = null;
		try {
			configs = new ConsoleConfigurationWorkbenchAdapter().getChildren(consCFG);
			assertTrue(configs[0] instanceof Configuration);
			persClasses = new ConfigurationWorkbenchAdapter().getChildren(configs[0]);
		} catch (InvalidMappingException ex){
			String out = NLS.bind(ConsoleTestMessages.OpenMappingDiagramTest_mapping_diagrams_for_package_cannot_be_opened,
					new Object[]{HibernateAllMappingTests.getActivePackage().getElementName(), ex.getMessage()});
			fail(out);
		}

		if (persClasses.length > 0){
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[0] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];

				IEditorPart editor = null;
				Throwable ex = null;
				try {
					editor = new OpenDiagramActionDelegate().openEditor(persClass, consCFG);
				} catch (PartInitException e) {
					ex = e;
				}
				if (ex == null ) ex = ProjectUtil.getExceptionIfItOccured(editor);
				if (ex != null) {
					String out = NLS.bind(ConsoleTestMessages.OpenMappingDiagramTest_mapping_diagram_for_not_opened,
							new Object[]{persClass.getClassName(), ex.getMessage()});
					fail(out);
				}
			}
		}
		//close all editors
	}


}
