package org.jboss.tools.hb.ui.bot.test.generation;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Run schema export from hibernate configuration context menu test
 * 
 * @author jpeterka
 * 
 */
@Require(db = @DB, clearProjects = true, perspective = "Hibernate")
public class RunSchemaExportTest extends HibernateBaseTest {


	final String hc = "pre-hibernate40";
	
	@Test
	public void showMappingDiagram() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/hibernate40");
		util.waitForAll();
		runShemaExport();
	}

	private void runShemaExport() {
		SWTBotView hcv = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		Tree.select(hcv.bot(), hc, "Configuration");
		SWTBotTreeItem item = Tree.select(hcv.bot(), hc);
		item.contextMenu("Run SchemaExport").click();
		
		String title = "Run SchemaExport";
		bot.waitUntil(shellIsActive(title ));
		SWTBotShell shell = bot.shell(title);
		shell.bot().button(IDELabel.Button.YES).click();
		bot.waitUntil(shellCloses(shell));
		
		//SWTBotTreeItem selectNode = ConsoleConfiguration.selectNode(hc,"Configuration","Database","Public");
		//assertTrue(selectNode.getNodes().size() != 0);
	}
}
