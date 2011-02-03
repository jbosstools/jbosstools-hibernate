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

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.Utils;
import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.ui.view.OpenDiagramActionDelegate;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingDiagramTest extends BaseTestSetCase {

	public OpenMappingDiagramTest() {
	}

	public OpenMappingDiagramTest(String name) {
		super(name);
	}

	public void testOpenMappingDiagram() {
		final Object[] persClasses = getPersistenceClasses(false);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		for (int i = 0; i < persClasses.length; i++) {
			assertTrue(persClasses[i] instanceof PersistentClass);
			PersistentClass persClass = (PersistentClass) persClasses[i];

			IEditorPart editor = null;
			Throwable ex = null;
			try {
				editor = new OpenDiagramActionDelegate().openEditor(persClass, consCFG);
			} catch (PartInitException e) {
				ex = e;
			}
			if (ex == null ) {
				ex = Utils.getExceptionIfItOccured(editor);
			}
			if (ex != null) {
				String out = NLS.bind(ConsoleTestMessages.OpenMappingDiagramTest_mapping_diagram_for_not_opened,
						new Object[]{persClass.getClassName(), ex.getMessage()});
				fail(out);
			}
		}
		//close all editors
	}
}
