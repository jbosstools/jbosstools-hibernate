package org.jboss.tools.hb.ui.bot.test.perspective;

import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.junit.Test;

/**
 * Hibernate perspective ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false, perspective = "JPA")
public class JPAPerspectiveTest extends HibernateBaseTest {

	@Test
	public void openPerspectiveElements() {
		open.perspective(ActionItem.Perspective.JPA.LABEL);
		open.viewOpen(ActionItem.View.JPAJPAStructure.LABEL);
		open.viewOpen(ActionItem.View.JPAJPADetails.LABEL);
		open.viewOpen(ActionItem.View.DataManagementDataSourceExplorer.LABEL);
	}
}
