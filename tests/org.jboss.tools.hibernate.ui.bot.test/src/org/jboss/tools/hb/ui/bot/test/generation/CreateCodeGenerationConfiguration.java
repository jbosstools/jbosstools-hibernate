package org.jboss.tools.hb.ui.bot.test.generation;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Matcher;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Hibernate code generation configuration ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(db = @DB, clearProjects = false, perspective = "Hibernate")
public class CreateCodeGenerationConfiguration extends HibernateBaseTest {

	final String prjName = "hibernate35";
	
	@Test
	public void hibernateCodeGeneration() {
		importTestProject("/resources/prj/hibernate35");
		SWTBotShell dlg = openCodeGenerationDlg();
		fillMainTab();
		fillExportersTab(dlg);
		fillRefreshTab(dlg);
		fillCommonTab(dlg);
		runCodeGeneration();
		
	}

	private SWTBotShell openCodeGenerationDlg() {
		SWTBotShell ret = null;
		SWTBotMenu menu,menu1 = null;
		menu = bot.menu("Run");
		menu1 = menu.menu(IDELabel.Menu.HIBERNATE_CODE_GENERATION);
		menu1.menu(IDELabel.Menu.HIBERNATE_CODE_GENERATION_CONF).click();		
		ret = bot.activeShell();
		return ret;
	}

	private void runCodeGeneration() {
		bot.button(IDELabel.Button.RUN).click();
	}

	private void fillMainTab() {

		bot.tree().expandNode("Hibernate Code Generation").select();
		bot.toolbarButtonWithTooltip("New launch configuration").click();

		eclipse.selectTreeLocation("Hibernate Code Generation",
				"New_configuration");
		bot.textWithLabel("Name:").setText("HSQL Configuration");

		// Console Configuration
		bot.comboBoxWithLabel("Console configuration:").setSelection(
				prjName);

		// Output directory
		bot.button("Browse...").click();
		bot.shell("Select output directory").activate();
		eclipse.selectTreeLocation(prjName);
		bot.button("Create New Folder...").click();
		bot.shell("New Folder").activate();
		bot.textWithLabel("Folder name:").setText("gen");
		bot.button(IDELabel.Button.OK).click();
		eclipse.selectTreeLocation(prjName, "gen");
		bot.button(IDELabel.Button.OK).click();

		// Create console configuration
		Matcher<Button> matcher = WidgetMatcherFactory
				.withText("Reverse engineer from JDBC Connection");
		Button button = bot.widget(matcher);
		SWTBotCheckBox cb = new SWTBotCheckBox(button);

		if (!cb.isChecked())
			cb.click();

		bot.textWithLabel("Package:").setText("org.test");
		log.info("HB Code Generation Main tab DONE");
		bot.sleep(TIME_1S);
	}

	private void fillExportersTab(SWTBotShell mainShell) {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.EXPORTERS_TAB)
				.activate();
		bot.table().select("Domain code (.java)");
		bot.table().getTableItem(0).check();
		log.info("HB Code Generation Exporters tab DONE");
		bot.sleep(TIME_1S);
	}

	private void fillRefreshTab(SWTBotShell mainShell) {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.REFRESH_TAB)
				.activate();
	}

	private void fillCommonTab(SWTBotShell mainShell) {
		mainShell.activate();
		bot.cTabItem(IDELabel.HBLaunchConfigurationDialog.COMMON_TAB)
				.activate();
	}
}
