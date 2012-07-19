package org.jboss.tools.hb.ui.bot.test.diagram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.junit.Test;

/**
 * Hibernate mapping diagram ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = true, perspective = "Hibernate")
public class MappingDiagramTest extends HibernateBaseTest {
	
	final String hc = "hibernate35";

	/**
	 * Test imports projects and check if mapping diagram can be opened
	 */
	@Test
	public void showMappingDiagram() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/hibernate35");
		util.waitForAll();
		openDiagram();
		bot.sleep(TIME_10S);
	}

	private void openDiagram() {
		SWTBotView hcv = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		SWTBotTreeItem item = Tree.select(hcv.bot(), hc, "Configuration");
		item.contextMenu("Mapping Diagram").click();
		String title = bot.activeEditor().getTitle();
		
		Pattern pattern = Pattern.compile(hc + ".*");
		Matcher matcher = pattern.matcher(title);
		assertTrue("Mapping diagram editor must be found",matcher.matches());
	}
}
