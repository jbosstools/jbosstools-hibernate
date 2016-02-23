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
package org.jboss.tools.hibernate.ui.diagram.editors.model.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
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
		
		final ConsoleConfiguration consoleConfig = context.mock(ConsoleConfiguration.class);
		final IConfiguration config = context.mock(IConfiguration.class);
		final IPersistentClass ioe = context.mock(IPersistentClass.class);
		ArrayList<IPersistentClass> rts = new ArrayList<IPersistentClass>();
		rts.add(ioe);
		final List<Object> emptyList = new ArrayList<Object>();
		final Iterator<Object> emptyListIterator = emptyList.iterator();
		//see https://jira.jboss.org/jira/browse/JBIDE-6186
		final String innerIntricateName = "myInner$Id";
		context.checking(new Expectations() {
			{
				oneOf(ioe).getEntityName();
				will(returnValue(innerIntricateName));
				
				allowing(consoleConfig).getConfiguration();
				will(returnValue(config));
				
				allowing(consoleConfig).hasConfiguration();
				will(returnValue(true));

				oneOf(ioe).getEntityName();
				will(returnValue(innerIntricateName));

				oneOf(ioe).getEntityName();
				will(returnValue(innerIntricateName));
				
				oneOf(ioe).isInstanceOfRootClass();
				will(returnValue(true));

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
				
				allowing(ioe).getClassName();
				will(returnValue("ClassName")); //$NON-NLS-1$
				
				allowing(consoleConfig).getName();
				will(returnValue("CCName")); //$NON-NLS-1$

				allowing(ioe).getEntityName();
				will(returnValue(innerIntricateName));
			}
		});
		final OrmDiagram ormDiagram = new OrmDiagram("", rts) { //$NON-NLS-1$
			public ConsoleConfiguration getConsoleConfig() {
				return consoleConfig;
			}
		};
		ormDiagram.saveInXmlFile();
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
