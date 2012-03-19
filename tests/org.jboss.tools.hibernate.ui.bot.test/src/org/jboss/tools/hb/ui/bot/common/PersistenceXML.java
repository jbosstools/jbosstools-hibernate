package org.jboss.tools.hb.ui.bot.common;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

public class PersistenceXML {
	
	static SWTBotExt bot = new SWTBotExt();
	static SWTOpenExt open = new SWTOpenExt(bot);
	
	public static void openPersistenceXML(String prj) {
		SWTBotView pe = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.open(pe.bot(), prj,"JPA Content","persistence.xml");
	}
	
	public static void setHibernateConfiguration(String path) {
		SWTBotEditor editor = 	bot.editorByTitle("persistence.xml");
		editor.show();
		SWTBotMultiPageEditor mpe = new SWTBotMultiPageEditor(editor.getReference(), bot);
		mpe.activatePage("Hibernate");

		bot.textWithLabel("Configuration file:").setText(path);
		editor.save();
	}
}
