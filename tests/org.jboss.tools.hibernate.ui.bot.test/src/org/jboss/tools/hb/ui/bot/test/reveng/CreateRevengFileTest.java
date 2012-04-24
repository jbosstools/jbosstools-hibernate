package org.jboss.tools.hb.ui.bot.test.reveng;

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
 * Hibernate create reveng file ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = true)
public class CreateRevengFileTest extends HibernateBaseTest {
	
	final String prj = "hibernate35";
	final String pkg = "org.reveng";
	
	@Test
	public void createRevengFileTest() {
		importTestProject("/resources/prj/hibernate35");
		createRevengFilesFromPackage();
	}

	private void createRevengFilesFromPackage() {
		// Select Both classes
		SWTBotView pe =  open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(pe.bot(), prj,"src",pkg);
						
		// Create mapping files 
		eclipse.createNew(EntityType.HIBERNATE_REVERSE_FILE);
	
		bot.waitForNumberOfShells(2);
		SWTBotShell shell =  bot.shell("");		
		shell.setFocus();		
		
		bot.waitUntil(widgetIsEnabled(bot.button("Next >")));
		bot.button(IDELabel.Button.NEXT).click();
		bot.waitUntil(widgetIsEnabled(bot.button("Finish")));		
		bot.button("Include...").click();		
		bot.button(IDELabel.Button.FINISH).click();		
		bot.waitForNumberOfShells(1);
		
		bot.closeAllEditors();
		
		Tree.open(pe.bot(), prj,"src",pkg,"hibernate.reveng.xml");
	}
}
