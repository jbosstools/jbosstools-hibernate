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
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.Utils;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingFileTest extends BaseTestSetCase {

	public OpenMappingFileTest() {
	}

	public OpenMappingFileTest(String name) {
		super(name);
	}

	public void testOpenMappingFileTest() {
		final Object[] persClasses = getPersistenceClasses(false);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		final String testClass = "class"; //$NON-NLS-1$
		for (int i = 0; i < persClasses.length; i++) {
			assertTrue(persClasses[i] instanceof IPersistentClass);
			IPersistentClass persClass = (IPersistentClass) persClasses[i];
			openTest(persClass, consCFG);
			Object[] props =  pcWorkbenchAdapter.getChildren(persClass);
			for (int j = 0; j < props.length; j++) {
				if (!(props[j] instanceof IProperty && ((IProperty)props[j]).classIsPropertyClass())) {
					continue;
				}
				openTest(props[j], consCFG);
				Object[] compProperties = propertyWorkbenchAdapter.getChildren(props[j]);
				for (int k = 0; k < compProperties.length; k++) {
					//test Composite properties
					if (!(compProperties[k] instanceof IProperty && ((IProperty)props[j]).classIsPropertyClass())) {
						continue;
					}
					final IProperty prop = (IProperty)compProperties[k];
					if (testClass.equals(prop.getName()) || testClass.equals(prop.getName())) {
						continue;
					}
					openPropertyTest((IProperty)compProperties[k], (IProperty) props[j], consCFG);
				}
			}
		}
		//close all editors
	}

	private void openPropertyTest(IProperty compositeProperty, IProperty parentProperty, ConsoleConfiguration consCFG){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(consCFG, compositeProperty, parentProperty);
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_highlighted_region_for_property_is_empty_package,
						new Object[]{compositeProperty.getName(), testPackage.getElementName()});
				if (Customization.USE_CONSOLE_OUTPUT)
					System.err.println(out);
				fail(out);
			}
			Object[] compProperties = propertyWorkbenchAdapter.getChildren(compositeProperty);
			for (int k = 0; k < compProperties.length; k++) {
				//test Composite properties
				assertTrue(compProperties[k] instanceof IProperty);
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
					new Object[]{compositeProperty.getName(), testPackage.getElementName(), ex.getMessage()});
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
		} catch (Exception e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(ConsoleTestMessages.OpenMappingFileTest_mapping_file_for_not_opened_package,
					new Object[]{selection, testPackage.getElementName(), ex.getMessage()});
			ex.printStackTrace();
			fail(out);
		}
	}
}
