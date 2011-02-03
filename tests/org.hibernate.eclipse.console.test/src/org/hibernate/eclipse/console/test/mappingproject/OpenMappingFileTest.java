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

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.utils.Utils;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

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
			assertTrue(persClasses[i] instanceof PersistentClass);
			PersistentClass persClass = (PersistentClass) persClasses[i];
			openTest(persClass, consCFG);
			Object[] props =  pcWorkbenchAdapter.getChildren(persClass);
			for (int j = 0; j < props.length; j++) {
				if (props[j].getClass() != Property.class) {
					continue;
				}
				openTest(props[j], consCFG);
				Object[] compProperties = propertyWorkbenchAdapter.getChildren(props[j]);
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
			Object[] compProperties = propertyWorkbenchAdapter.getChildren(compositeProperty);
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
}
