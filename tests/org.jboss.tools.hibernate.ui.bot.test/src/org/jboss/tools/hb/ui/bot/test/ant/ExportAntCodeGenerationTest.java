package org.jboss.tools.hb.ui.bot.test.ant;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.hb.ui.bot.common.ProjectExplorer;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Hibernate export ant code generation ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(db = @DB, clearProjects = true, perspective = "Hibernate")
public class ExportAntCodeGenerationTest extends HibernateBaseTest {
	final String prjName = "hibernate35";
	final String genCfg = "hb35hsqldb";
	final String antCfg = "build.hb.xml";

	@Test
	public void hibernateCodeGeneration() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/hibernate35");
		exportAntCodeGeneration();
		checkGeneratedAntcode();
	}

	private void exportAntCodeGeneration() {
		SWTBotExt bot = new SWTBotExt();
		SWTOpenExt open = new SWTOpenExt(bot);
		SWTBotView view = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(view.bot(), prjName);
		ContextMenuHelper.clickContextMenu(view.bot().tree(), "Export...");

		String st1 = "Export";
		bot.waitUntil(shellIsActive(st1));
		SWTBotShell shell = bot.shell(st1);
		Tree.select(shell.bot(), "Hibernate", "Ant Code Generation");
		shell.bot().button(IDELabel.Button.NEXT).click();

		String st2 = "Export Hibernate Code Generation Configuration to Ant Script";
		bot.waitUntil(shellIsActive(st2));
		SWTBotShell shell2 = bot.shell(st2);

		shell2.bot()
				.comboBoxWithLabel("Hibernate Code Generation Configurations:")
				.setSelection(genCfg);
		shell2.bot().textWithLabel("File name:").setText(antCfg);
		shell2.bot().button(IDELabel.Button.FINISH).click();

		bot.waitForNumberOfShells(1);
	}

	private void checkGeneratedAntcode() {
		ProjectExplorer.open(prjName, antCfg);		
	}

}
