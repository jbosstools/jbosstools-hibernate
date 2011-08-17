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
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

@Require(clearProjects = false,  perspective="Hibernate")
public class HibernatePerspectiveTest extends HibernateTest {

	/**
	 * TC 17 - Check presence of basic hibernate views
	 */
	@Test
	public void openPerspectiveElements() {
		eclipse.closeView(IDELabel.View.WELCOME);
		
		open.perspective(ActionItem.Perspective.HIBERNATE.LABEL);
		bot.sleep(TIME_1S);		
		open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);		
		bot.sleep(TIME_1S);
		open.viewOpen(ActionItem.View.HibernateHibernateDynamicSQLPreview.LABEL);
		bot.sleep(TIME_1S);
		open.viewOpen(ActionItem.View.HibernateHibernateQueryResult.LABEL);
		bot.sleep(TIME_1S);
	}
	
	@Test 
	public void checkHibernateTree() {
		open.perspective(ActionItem.Perspective.HIBERNATE.LABEL);
		bot.sleep(TIME_1S);
		
		open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);		
	}
}
