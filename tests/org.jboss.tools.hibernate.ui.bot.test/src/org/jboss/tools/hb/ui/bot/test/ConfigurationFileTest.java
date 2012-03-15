package org.jboss.tools.hb.ui.bot.test;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Matcher;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Hibernate configuration ui bot test
 * @author jpeterka
 *
 */
@Require
public class ConfigurationFileTest extends HibernateBaseTest {

	@BeforeClass
	public static void beforeClass() {
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.closeView(IDELabel.View.JBOSS_CENTRAL);
		eclipse.closeAllEditors();
		util.waitForAll();
	}

	@Test
	public void emptyTest() {
		assertTrue(true);
	}

	@Test
	public void configurationFileTest() {
		emptyErrorLog();
		importTestProject("/resources/prj/hibernate35");
		createHBConfiguration();
		openHBConfiguration();
		checkErrorLog();
	}

	private void createHBConfiguration() {
		SWTBotView pv = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(pv.bot(), "hibernate35", "cfg");

		eclipse.createNew(EntityType.HIBERNATE_CONFIGURATION_FILE);
		bot.textWithLabel(IDELabel.HBConfigurationWizard.FILE_NAME).setText(
				"hibernate.cfg.xml");
		bot.button(IDELabel.Button.NEXT).click();

		// Create new configuration file
		String dialect = DatabaseHelper
				.getDialect(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DATABASE_DIALECT)
				.setSelection(dialect);
		String drvClass = DatabaseHelper
				.getDriverClass(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DRIVER_CLASS)
				.setSelection(drvClass);
		String jdbc = TestConfigurator.currentConfig.getDB().jdbcString;
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.CONNECTION_URL).setText(
				jdbc);

		// Create console configuration
		Matcher<Button> matcher = WidgetMatcherFactory
				.withText(IDELabel.HBConsoleWizard.CREATE_CONSOLE_CONFIGURATION);
		Button button = bot.widget(matcher);
		SWTBotCheckBox cb = new SWTBotCheckBox(button);

		if (!cb.isChecked())
			cb.click();

		SWTBotShell shell = bot.activeShell();
		log.info("Active shell:" + shell.getText());
		bot.button(IDELabel.Button.FINISH).click();

		bot.waitUntil(shellCloses(shell));
	}

	private void openHBConfiguration() {

		SWTBotView pv = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(pv.bot(), "hibernate35", "cfg", "hibernate.cfg.xml");
		bot.editorByTitle("hibernate.cfg.xml").show();

	}

	@AfterClass
	public static void afterClass() {
		// wait for all jobs
		util.waitForAll();
	}
}