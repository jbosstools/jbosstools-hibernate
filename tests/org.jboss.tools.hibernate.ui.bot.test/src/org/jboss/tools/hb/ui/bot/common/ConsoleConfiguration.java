package org.jboss.tools.hb.ui.bot.common;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

public class ConsoleConfiguration {
 
	public static void open(String consoleName) {
		SWTBotExt bot = new SWTBotExt();
		SWTOpenExt open = new SWTOpenExt(bot);
		
		SWTBotView view = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		
		Tree.select(view.bot(), consoleName);	
	}
	
	public static SWTBotTreeItem selectNode(String... nodes) {
		SWTBotExt bot = new SWTBotExt();
		SWTOpenExt open = new SWTOpenExt(bot);
		
		SWTBotView view = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);
		
		SWTBotTreeItem item = Tree.select(view.bot(), nodes);
		return item;
	}
}
