/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
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

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.actions.OpenSourceAction;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.Utils;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenSourceFileTest extends BaseTestSetCase {

	public OpenSourceFileTest() {
	}

	public OpenSourceFileTest(String name) {
		super(name);
	}

	public void testOpenSourceFileTest() {
		//fail("test fail");
		final Object[] persClasses = getPersistenceClasses(false);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		for (int i = 0; i < persClasses.length; i++) {
			assertTrue(persClasses[i] instanceof IPersistentClass);
			IPersistentClass persClass = (IPersistentClass) persClasses[i];
			String fullyQualifiedName = persClass.getClassName();
			// test PersistentClasses
			openTest(persClass, consCFG, fullyQualifiedName);
			Object[] fields = pcWorkbenchAdapter.getChildren(persClass);
			for (int j = 0; j < fields.length; j++) {
				if (!(fields[j] instanceof IProperty && ((IProperty)fields[j]).classIsPropertyClass())) {
					continue;
				}
				fullyQualifiedName = persClass.getClassName();
				// test Properties
				openTest(fields[j], consCFG, fullyQualifiedName);
				if (fields[j] instanceof IProperty
					&& ((IProperty)fields[j]).isComposite()) {
					fullyQualifiedName =((IProperty) fields[j]).getValue().getComponentClassName();

					Object[] compProperties = propertyWorkbenchAdapter.getChildren(fields[j]);
					for (int k = 0; k < compProperties.length; k++) {
						if (!(compProperties[k] instanceof IProperty && ((IProperty)compProperties[k]).classIsPropertyClass())) {
							continue;
						}
						//test Composite properties
						openTest(compProperties[k], consCFG, fullyQualifiedName);
					}
				}
			}
		}
		//close all editors
	}


	private void openTest(Object selection, ConsoleConfiguration consCFG, String fullyQualifiedName){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenSourceAction.run(consCFG, selection, fullyQualifiedName);
			if (Object.class.getName().equals(fullyQualifiedName)){
				return;
			}
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(ConsoleTestMessages.OpenSourceFileTest_highlighted_region_for_is_empty, selection);
				if (Customization.USE_CONSOLE_OUTPUT)
					System.err.println(out);
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
			String out = NLS.bind(ConsoleTestMessages.OpenSourceFileTest_mapping_file_for_not_opened,
					fullyQualifiedName/*.getClassName()*/, ex.getMessage());
			fail(out);
		}
	}
}
