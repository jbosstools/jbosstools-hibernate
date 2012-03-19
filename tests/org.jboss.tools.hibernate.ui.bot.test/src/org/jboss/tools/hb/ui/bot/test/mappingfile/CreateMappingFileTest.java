package org.jboss.tools.hb.ui.bot.test.mappingfile;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Hibernate create mapping files ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false)
public class CreateMappingFileTest extends HibernateBaseTest {
	
	final String prj = "hibernate35";
	final String pkg = "org.mapping";
	
	@Test
	public void createMappingFileTest() {
		importTestProject("/resources/prj/hibernate35");
		createMappingFilesFromPackage();
	}

	private void createMappingFilesFromPackage() {
		// Select Both classes
		SWTBotView pe =  open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(pe.bot(), prj,"src",pkg);
						
		// Create mapping files 
		eclipse.createNew(EntityType.HIBERNATE_MAPPING_FILE);

		bot.waitUntil(shellIsActive("New Hibernate XML Mapping files (hbm.xml)"));		
		SWTBotShell shell =  bot.shell("New Hibernate XML Mapping files (hbm.xml)");
		shell.setFocus();		
		
		bot.waitUntil(widgetIsEnabled(bot.button("Next >")));
		bot.button(IDELabel.Button.NEXT).click();
		bot.waitUntil(widgetIsEnabled(bot.button("Finish")));
		bot.button(IDELabel.Button.FINISH).click();		
	}
}
