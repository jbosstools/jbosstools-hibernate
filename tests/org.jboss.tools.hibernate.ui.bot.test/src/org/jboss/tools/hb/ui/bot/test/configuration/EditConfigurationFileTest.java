package org.jboss.tools.hb.ui.bot.test.configuration;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.parts.ObjectMultiPageEditorBot;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Hibernate edit configuration ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearWorkspace = true, clearProjects = true)
public class EditConfigurationFileTest extends HibernateBaseTest {

	private final String hbcfg = "hibernate.cfg.xml";

	@Test
	public void emptyTest() {
		assertTrue(true);
	}

	@Test
	public void configurationFileTest() {
		emptyErrorLog();
		importTestProject("/resources/prj/hibernate35");
		openHBConfiguration();
		editHBConfiguration();
		checkErrorLog();
	}

	private void openHBConfiguration() {

		SWTBotView pv = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.open(pv.bot(), "hibernate35", "src", hbcfg);
		bot.editorByTitle(hbcfg).show();

	}

	private void editHBConfiguration() {
		SWTBotEditor editor = bot.editorByTitle(hbcfg);
		ObjectMultiPageEditorBot multiBot = new ObjectMultiPageEditorBot(hbcfg);

		// Tabs availability
		String[] pages = { "Session Factory", "Security", "Source" };
		for (String page : pages) {
			multiBot.selectPage(page);
		}

		// Create new security element
		SWTBot localBot = editor.bot();
		multiBot.selectPage(pages[1]);
		SWTBotTree secTree = localBot.tree().select("Security");

		ContextMenuHelper.clickContextMenu(secTree, "New", "Grant...");

		// Filling Role, Entity-Name and Check action All *
		String roleName = "role1";
		String entityName = "entity1";

		bot.textWithLabel("Role:*").setText("role1");
		bot.textWithLabel("Entity-Name:*").setText("entity1");
		SWTBotShell shell = bot.shell("Add Grant");

		assertNotNull(shell);
		SWTBot shellBot = new SWTBot(shell.widget);

		SWTBotTree tree = shellBot.tree();
		SWTBotTreeItem[] items = tree.getAllItems();
		items[0].check();

		shellBot.button(IDELabel.Button.FINISH).click();

		// Click on Source tab for check
		multiBot.selectPage(pages[2]);
		List<String> lines = editor.toTextEditor().getLines();

		boolean found = false;
		String wanted = "<grant actions=\"*\" entity-name=\"" + entityName
				+ "\" role=\"" + roleName + "\"/>";
		System.out.println("Looking for:" + wanted);

		for (String line : lines) {
			System.out.println(line);
			if (line.trim().equals(wanted)) {
				found = true;
				System.out.println("Found");
			}
		}

		editor.saveAndClose();
		assertTrue("Security element not found in xml", found);
	}

}