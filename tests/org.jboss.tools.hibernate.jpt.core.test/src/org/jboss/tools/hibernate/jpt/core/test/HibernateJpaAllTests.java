/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaAllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(
			"Test for org.jboss.tools.hibernate.jpt.core.test"); //$NON-NLS-1$
		suite.addTestSuite(HibernateJpaModelTests.class);
		suite.addTestSuite(HibernateJpaModelTests.class);
		return suite;
	}

}
