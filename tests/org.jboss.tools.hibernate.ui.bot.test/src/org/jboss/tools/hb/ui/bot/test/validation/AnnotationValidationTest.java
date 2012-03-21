package org.jboss.tools.hb.ui.bot.test.validation;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.ProjectExplorer;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Test;

/**
 * Hibernate annotation validation test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false, perspective = "JPA")
public class AnnotationValidationTest extends HibernateBaseTest {

	final String prj = "jpatest40";
	final String pkg = "org.validation";
	
	@Test
	public void annotationValidationTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/jpatest40");
		
		checkGenericGeneratorValidation();
	}

	private void checkGenericGeneratorValidation() {
		String resource = "GeneratorValidationEntity.java";
		ProjectExplorer.open(prj, "src", pkg, resource);
		
		String desc = "No generator named \"mygen\" is defined in the persistence unit";
		String path = "/" + prj + "/src/org/validation";
		String type = "JPA Problem";
		
		SWTBotTreeItem[] items = null;
		items = ProblemsView.getFilteredErrorsTreeItems(bot, desc, path, resource, type);				
		assertTrue(items.length == 1);
		
		// fix 
		SWTBotEditor editor = bot.editorByTitle(resource);
		editor.show();
		StringHelper sh = new StringHelper(editor.toTextEditor().getText());
		Point pos = sh.getPositionAfter("\"mygen\"");
		editor.toTextEditor().selectRange(pos.y, pos.x, 0);
		editor.toTextEditor().insertText("erator");
		editor.save();
		util.waitForAll();
		
		// check
		items = ProblemsView.getFilteredErrorsTreeItems(bot, desc, path, resource, type);				
		assertTrue(items.length == 0);
	}
}