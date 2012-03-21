package org.jboss.tools.hb.ui.bot.test.view;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withLabel;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForWidget;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.hb.ui.bot.common.ProjectExplorer;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotTwistie;
import org.junit.Test;

/**
 * JPA View ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false, perspective = "Hibernate")
public class JPADetailViewTest extends HibernateBaseTest {
		

	final String prj = "jpatest40";
	final String pkg = "org.jpadetails";
	final String entity = "Machine.java";
	
	@Test
	public void jpaDetailsViewTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/jpatest40");
		
		checkGenericGeneratorValidation();
	}

	private void checkGenericGeneratorValidation() {
		
		SWTBotView jd = open.viewOpen(ActionItem.View.JPAJPADetails.LABEL);		
		ProjectExplorer.open(prj, "src", pkg, entity);
		jd.show();
		
		SWTBotEditor editor = bot.editorByTitle(entity);
		StringHelper sh = new StringHelper(editor.toTextEditor().getText());
		String str = "@Entity";
		Point pos = sh.getPositionBefore(str);
		editor.setFocus();
		editor.toTextEditor().selectRange(pos.y, pos.x, 0);
					
		String label = "Details are not available for the current selection.";
		jd.bot().waitWhile(waitForWidget(withLabel(label)));

		bot.sleep(TIME_5S);
		SWTBotTwistie twistieBot = bot.twistieByLabel("Entity");
		while (!twistieBot.isExpanded()) {
			twistieBot.toggle();
		}
		
	}
}
