package org.jboss.tools.hb.ui.bot.test.hql;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.ConfigurationFile;
import org.jboss.tools.hb.ui.bot.common.ConsoleConfiguration;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.junit.Test;

@Require(db = @DB, clearProjects = true)
public class HQLEditorTest extends HibernateBaseTest {
	final String console = "pre-hibernate40";
	final String clazz = "Customer";
	
	@Test
	public void jpaDetailsViewTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/hibernate40");
		
		ConfigurationFile.create(new String[]{"hibernate40", "src"}, "hibernate.cfg.xml",false);
		
		executeHQLQuery();
		checkHQLQueryResult();
	}

	private void executeHQLQuery() {
		SWTBotTreeItem item = ConsoleConfiguration.selectNode("pre-hibernate40", "Configuration");

		item.contextMenu("HQL Editor").click();
		SWTBotEditor editor = bot.editorByTitle(console);
		editor.setFocus();
		editor.toTextEditor().setText("from " + clazz);
		editor.save();
		bot.toolbarButtonWithTooltip("Run HQL").click();
		
		String osf= "Open Session factory";
		bot.waitForShell(osf);
		SWTBotShell shell = bot.shell(osf);
		shell.bot().button("Yes").click();		
	}
	
	private void checkHQLQueryResult() {
		SWTBotView qr = open.viewOpen(ActionItem.View.HibernateHibernateQueryResult.LABEL);
		SWTBotTable table = qr.bot().table();
		assertTrue(table.rowCount() == 0);		
	}

}
