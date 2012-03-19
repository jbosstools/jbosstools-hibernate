package org.jboss.tools.hb.ui.bot.test.mappingfile;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.parts.ObjectMultiPageEditorBot;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.junit.Test;

/**
 * Hibernate perspective ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = true)
public class EditMappingFileTest extends HibernateBaseTest {
		
	final String prj = "hibernate35";
	final String pkg = "org.mapping.edit";
	final String file1 = "Customer.hbm.xml";
	
	@Test
	public void editMappingFile() {
		importTestProject("/resources/prj/hibernate35");
		editFile();
	}

	private void editFile() {
		SWTBotView view = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.open(view.bot(), prj, "src", pkg, file1 );
		
		ObjectMultiPageEditorBot pageBot = new ObjectMultiPageEditorBot(file1);
		pageBot.selectPage("Source");
		SWTBotEditorExt editor = bot.swtBotEditorExtByTitle(file1);
		
		String search = "</id>";  
		List<String> lines = editor.getLines();
		
		int index = 0;
		for (String line : lines ) {
			index++;
			if (line.trim().equals(search)) break;
		}

		log.info("Line index: " + index);

		// Insert tag for cc check
		String newLine = "<property name=\"\"> ";
		int col = newLine.indexOf("\"\"");
		editor.selectRange(index, 0, 0);
		editor.insertText("\n");
		editor.insertText(newLine);
		editor.selectRange(index, col + 1, 0);
		
		editor.save();			
	}
}
