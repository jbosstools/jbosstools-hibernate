/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.veditor.editors.model.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import junit.framework.TestCase;

/**
 * for OrmDiagram class functionality test
 * 
 * @author Vitali Yemialyanchyk
 */
public class OrmDiagramTest extends TestCase {
	
	public Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	public void testLoadAndSave() {
		
		final ConsoleConfiguration consoleConfiguration = context.mock(ConsoleConfiguration.class);
		final Configuration configuration = context.mock(Configuration.class);
		final RootClass ioe = context.mock(RootClass.class);
		final IJavaProject javaProject = context.mock(IJavaProject.class);
		final List<Object> emptyList = new ArrayList<Object>();
		final Iterator<Object> emptyListIterator = emptyList.iterator();
		final IProject project = context.mock(IProject.class);

		context.checking(new Expectations() {
			{
				oneOf(consoleConfiguration).getConfiguration();
				will(returnValue(configuration));

				oneOf(ioe).getEntityName();
				will(returnValue("testEntityName")); //$NON-NLS-1$

				oneOf(ioe).getEntityName();
				will(returnValue("")); //$NON-NLS-1$

				oneOf(ioe).getEntityName();
				will(returnValue("")); //$NON-NLS-1$

				oneOf(ioe).getIdentifierProperty();
				will(returnValue(null));

				oneOf(ioe).getIdentifier();
				will(returnValue(null));

				oneOf(ioe).getPropertyIterator();
				will(returnValue(emptyListIterator));

				oneOf(ioe).getTable();
				will(returnValue(null));

				oneOf(ioe).getSubclassIterator();
				will(returnValue(emptyListIterator));

				oneOf(ioe).getIdentifier();
				will(returnValue(null));

				oneOf(ioe).getJoinIterator();
				will(returnValue(emptyListIterator));
				
				allowing(javaProject).getProject();
				will(returnValue(project));

				allowing(project).getLocation();
				will(returnValue(Path.fromOSString(""))); //$NON-NLS-1$
				
				allowing(ioe).getClassName();
				will(returnValue("ClassName")); //$NON-NLS-1$
				
				allowing(consoleConfiguration).getName();
				will(returnValue("CCName")); //$NON-NLS-1$

				allowing(ioe).getEntityName();
				will(returnValue("")); //$NON-NLS-1$
			}
		});
		final OrmDiagram ormDiagram = new OrmDiagram(consoleConfiguration, ioe, javaProject);
		ormDiagram.save();
		// test is the folder created
		File folder = new File(ormDiagram.getStoreFolderPath().toOSString());
		assertTrue(folder.exists() && folder.isDirectory());
		// test is the file created
		File file = new File(ormDiagram.getStoreFilePath().toOSString());
		assertTrue(file.exists() && file.isFile());
		//
		boolean res = file.delete();
		assertTrue(res);
		//
		res = folder.delete();
		assertTrue(res);
		// GENERAL TEST:
		// check for all expectations
		context.assertIsSatisfied();
	}

}
