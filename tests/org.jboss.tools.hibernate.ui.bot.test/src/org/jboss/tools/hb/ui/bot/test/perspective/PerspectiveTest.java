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
@Require(clearProjects = false, perspective = "Hibernate")
public class PerspectiveTest extends HibernateBaseTest {
		
	@Test
	public void openPerspectiveElements() {
		open.perspective(ActionItem.Perspective.HIBERNATE.LABEL);
		open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		open.viewOpen(ActionItem.View.HibernateHibernateDynamicSQLPreview.LABEL);
		open.viewOpen(ActionItem.View.HibernateHibernateQueryResult.LABEL);
	}
}
