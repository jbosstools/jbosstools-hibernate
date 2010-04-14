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

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ConsolePerspectiveTest extends HibernateTest {

	/**
	 * TC 17 - Check presence of basic hibernate views
	 */
	@Test
	public void openPerspectiveElements() {
		bot.viewByTitle("Welcome").close();
		
		open.perspective(ActionItem.Perspective.HIBERNATE.LABEL);
		bot.sleep(TIME_1S);
		
		open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		bot.sleep(TIME_1S);
		open.viewOpen(ActionItem.View.HibernateHibernateDynamicSQLPreview.LABEL);
		bot.sleep(TIME_1S);
		open.viewOpen(ActionItem.View.HibernateHibernateQueryResult.LABEL);
		bot.sleep(TIME_1S);
	}

}
