package org.jboss.tools.hb.ui.bot.test.jpa;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.hb.ui.bot.common.JPAEntity;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.junit.Test;

/**
 * Create JPA Entity ui bot test
 */
@Require(db = @DB, clearProjects = true)
public class CreateJPAEntityTest extends HibernateBaseTest {
	
	final String prj = "jpatest35";
	final String pkg = "org.newentity";
	final String ent = "NewEntity"; 
	final String ext = ".java";
	
	@Test
	public void createJPAProject() {
		emptyErrorLog();
		importTestProject("/resources/prj/" + prj);
		importTestProject("/resources/prj/" + "hibernatelib");
		createJPAEntity();
		addIdIntoEntity();
		checkErrorLog();
	}

	private void addIdIntoEntity() {
		SWTBotEditor editor = bot.editorByTitle(ent + ext);
		StringHelper sh = new StringHelper(editor.toTextEditor().getText());
		Point p = sh.getPositionBefore("private static final long serialVersionUID");
		editor.toTextEditor().selectRange(p.y, p.x, 0);
		editor.toTextEditor().insertText("@Id\nprivate long id;\n");
		editor.save();
		editor.toTextEditor().selectRange(0, 0, editor.toTextEditor().getText().length());
		bot.menu("Source").menu("Format").click();
		editor.toTextEditor().selectRange(0, 0, 0);
		editor.save();		
	}

	private void createJPAEntity() {
		SWTBotView view = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.select(view.bot(), prj);
		JPAEntity.create(pkg,ent);		
		Tree.open(view.bot(),prj,"src", pkg,ent + ext);  
	}
}
