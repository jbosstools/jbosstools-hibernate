package org.jboss.tools.hb.ui.bot.test.hql;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.ConfigurationFile;
import org.jboss.tools.hb.ui.bot.common.ConsoleConfiguration;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.junit.Test;

public class HQLEditorTest extends HibernateBaseTest {
	final String console = "pre-hibernate40";
	final String clazz = "Customer";
	
	@Test
	public void jpaDetailsViewTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/hibernate40");
		
		ConfigurationFile.create(new String[]{"hibernate40", "src"}, "hibernate.cfg.xml",false);
		
		executeHQLQuery();
	}

	private void executeHQLQuery() {
		SWTBotTreeItem item = ConsoleConfiguration.selectNode("pre-hibernate40", "Configuration", clazz);
		item.contextMenu("HQL Editor").click();
		SWTBotEditor editor = bot.editorByTitle(console);
		editor.setFocus();
		bot.toolbarButtonWithTooltip("Run HQL").click();
		
		bot.sleep(TIME_10S);
	}


	
}
