package org.jboss.tools.hb.ui.bot.common;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Matcher;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

/**
 * Hibernate Configuration ui bot routines  
 * @author jpeterka
 *
 */
public class ConfigurationFile {

	public static void open(String... path) {
		SWTOpenExt open = new SWTOpenExt(new SWTBotExt());
		SWTBotView pv = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		
		Tree.open(pv.bot(), path);
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		bot.editorByTitle(path[path.length - 1]).show();
	}
	
	public static void create(String[] path, String cfgName) {
		SWTBotExt bot = new SWTBotExt();
		SWTEclipseExt eclipse = new SWTEclipseExt();
		SWTOpenExt open = new SWTOpenExt(bot);
	
		SWTBotView view = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(view.bot(), path);
		
		eclipse.createNew(EntityType.HIBERNATE_CONFIGURATION_FILE);
		bot.textWithLabel(IDELabel.HBConfigurationWizard.FILE_NAME).setText(
				cfgName);
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
		bot.button(IDELabel.Button.FINISH).click();

		bot.waitUntil(shellCloses(shell));
	}
}
