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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.InvalidMappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
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
	
	public void testOpenMappingFileTest() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consCFG = knownConfigurations.find(ProjectUtil.ConsoleCFGName);
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
			fail("Mapping Files for package " + HibernateAllMappingTests.getActivePackage().getElementName()
					+ " can't be opened:\n " + ex.getMessage());
		}
		if (persClasses.length > 0){
			for (int i = 0; i < persClasses.length; i++) {
				assertTrue(persClasses[0] instanceof PersistentClass);
				PersistentClass persClass = (PersistentClass) persClasses[i];
				openTest(persClass, consCFG);
				props =  new PersistentClassWorkbenchAdapter().getChildren(persClass);
				for (int j = 0; j < props.length; j++) {
					if (props.getClass() != Property.class) continue;
					openTest(props[j], consCFG);
					Object[] compProperties = new PropertyWorkbenchAdapter().getChildren(props[j]);
					for (int k = 0; k < compProperties.length; k++) {
						//test Composite properties
						if (compProperties[k].getClass() != Property.class) continue;
						openPropertyTest((Property) props[j], (Property)compProperties[k], consCFG);
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
			editor = OpenMappingAction.run(compositeProperty, parentProperty, consCFG);
			boolean highlighted = ProjectUtil.checkHighlighting(editor);
			if (!highlighted) fail("Highlighted region for " + compositeProperty.getNodeName() + " is empty.");
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
		if (ex == null ) ex = ProjectUtil.getExceptionIfItOccured(editor);
		if (ex != null) fail("Mapping file for " + compositeProperty.getNodeName()
				+ " not opened:\n" + ex.getMessage());
	}
	
	private void openTest(Object selection, ConsoleConfiguration consCFG){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(selection, consCFG);
			boolean highlighted = ProjectUtil.checkHighlighting(editor);
			if (!highlighted) fail("Highlighted region for " + selection + " is empty.");
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		}		
		if (ex == null ) ex = ProjectUtil.getExceptionIfItOccured(editor);
		if (ex != null) fail("Mapping file for " + selection/*.getClassName()*/
				+ " not opened:\n" + ex.getMessage());
	}
	
	


}
