package org.jboss.tools.hb.ui.bot.test.console;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.ConfigurationFile;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Create Hibernate Console UI Bot Test
 * @author jpeterka
 *
 */
@Require(db = @DB, clearProjects = true)
public class CreateConsoleConfigurationTest extends HibernateBaseTest {

	final String prjName = "configurationtest";
	
	@Test
	public void createConsoleConfigurationTest() {
		emptyErrorLog();
		importTestProject("/resources/prj/" + prjName);
		createConfigurationFile();
		createHibernateConsole();
		expandDatabaseInConsole();
		checkErrorLog();
	}
	
	private void createConfigurationFile() {
		ConfigurationFile.create(new String[]{prjName,"src"},"hibernate.cfg.xml",false);		
	}

	private void createHibernateConsole() {
		open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		eclipse.createNew(EntityType.HIBERNATE_CONSOLE);		

		// Hibernate Console Dialog has no title
		SWTBotShell shell = bot.waitForShell("");
				
		createMainTab(shell);
		createOptionTab(shell);
		createClasspathTab(shell);
		createMappingsTab(shell);
		createCommonTab(shell);
		
		bot.button(IDELabel.Button.FINISH).click();
		bot.waitUntil(shellCloses(shell));
		util.waitForNonIgnoredJobs();			
	}
	
	private void createMainTab(SWTBotShell shell) {
		bot.cTabItem(IDELabel.HBConsoleWizard.MAIN_TAB).activate();
		bot.textWithLabel("Name:").setText(prjName);
		bot.textWithLabelInGroup("","Configuration file:").setText(prjName + "/src/hibernate.cfg.xml");						
	}

	private void createOptionTab(SWTBotShell shell) {
		shell.setFocus();
		bot.cTabItem(IDELabel.HBConsoleWizard.OPTIONS_TAB).activate();
				
		String dialect = DatabaseHelper.getDialect(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabelInGroup("", IDELabel.HBConsoleWizard.DATABASE_DIALECT).setSelection(dialect);
	}

	private void createClasspathTab(SWTBotShell shell) {
		shell.setFocus();
		bot.cTabItem(IDELabel.HBConsoleWizard.CLASSPATH_TAB).activate();
	}

	private void createMappingsTab(SWTBotShell shell) {
		shell.setFocus();
		bot.cTabItem(IDELabel.HBConsoleWizard.MAPPINGS_TAB).activate();
	}

	private void createCommonTab(SWTBotShell shell) {
		shell.setFocus();
		bot.cTabItem(IDELabel.HBConsoleWizard.COMMON_TAB).activate();
	}

	private void expandDatabaseInConsole() {
		SWTBot viewBot = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL).bot();
		SWTBotTreeItem db = Tree.select(viewBot, prjName,"Database");
		db.expand();

		// expand PUBLIC node or whatever
		SWTBotTreeItem pub = db.getItems()[0];
		final int limit = 10; // 10s
		final int sleep = 1000;
		int counter = 0;
				
		while(counter < limit) {
			if (pub.widget.isDisposed()) {
				pub = db.getItems()[0];
				bot.sleep(sleep);
			}
			if (pub.getText().equals("<Reading schema error: Getting database metadata >")) {
				fail("Can't load data, DB not accessible");
			}		
			if (pub.getText().equals("Pending...") &&  counter < limit) {
				bot.sleep(sleep);
				counter++;
				log.info("Waiting for database loading...");
			}
			else
			{
				log.info("DB loaded");
				break;
			}
		}
		
		List<String> tables = Tree.getSubNodes(viewBot, pub);
		assertTrue("Table must contain tables", tables.size()> 0);
		for (String s : tables) {
			log.info("Table:" + s);
		}
	}
}
