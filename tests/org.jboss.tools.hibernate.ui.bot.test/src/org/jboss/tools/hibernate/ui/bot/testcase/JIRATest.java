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
package org.jboss.tools.hibernate.ui.bot.testcase;

import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.junit.Test;

@SWTBotTestRequires( clearProjects = false,  db=@DB, perspective="Hibernate")
public class JIRATest extends HibernateTest {

	/**
	 * TC 99
	 */
	@Test
	public void issue1() {
		// TODO
	}

}
