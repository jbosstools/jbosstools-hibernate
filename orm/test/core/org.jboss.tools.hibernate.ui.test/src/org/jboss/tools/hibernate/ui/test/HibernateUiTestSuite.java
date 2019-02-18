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
package org.jboss.tools.hibernate.ui.test;

import org.jboss.tools.hibernate.ui.diagram.editors.actions.test.ExportImageActionTest;
import org.jboss.tools.hibernate.ui.diagram.editors.model.test.OrmDiagramTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author eskimo
 *
 */
public class HibernateUiTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(ExportImageActionTest.class);
		suite.addTestSuite(OrmDiagramTest.class);
		return suite;
	}
}
