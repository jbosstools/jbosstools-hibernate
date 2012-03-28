package org.jboss.tools.hb.ui.bot.test.view;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withLabel;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForWidget;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.hb.ui.bot.common.ProjectExplorer;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotTwistie;
import org.junit.Test;

public class PackageInfoTest extends HibernateBaseTest {
	final String prj = "jpatest40";
	final String pkg = "org.packageinfo";
	final String pkginfo = "package-info.java";
	final String entity = "Table.java";
	
	@Test
	public void jpaDetailsViewTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/jpatest40");
		
		checkGeneratorInPackageInJPADetailsView();
	}


	private void checkGeneratorInPackageInJPADetailsView() {
		
		SWTBotView jd = open.viewOpen(ActionItem.View.JPAJPADetails.LABEL);		
		ProjectExplorer.open(prj, "src", pkg, pkginfo);
		jd.show();
		
		SWTBotEditor editor = bot.editorByTitle(pkginfo);
		StringHelper sh = new StringHelper(editor.toTextEditor().getText());
		String str = "@GenericGenerator";
		Point pos = sh.getPositionBefore(str);
		editor.setFocus();
		editor.toTextEditor().selectRange(pos.y, pos.x, 0);
					
		String label = "Details are not available for the current selection.";
		jd.bot().waitWhile(waitForWidget(withLabel(label)));

		SWTBotTwistie twistie = bot.twistieByLabel("Generic Generators");
		while (!twistie.isExpanded()) {
			twistie.toggle();
		}
		
		final String genname = "myuuidgen";
		final String strategy = "uuid";
		SWTBotTable table = jd.bot().table(0);
		assertTrue(table.containsItem(genname));
		table.getTableItem(genname).click();

		assertTrue(bot.textWithLabel("Name:").getText().equals(genname));
		assertTrue(bot.textWithLabel("Stragegy:").getText().equals(strategy));				
	}
	
}
