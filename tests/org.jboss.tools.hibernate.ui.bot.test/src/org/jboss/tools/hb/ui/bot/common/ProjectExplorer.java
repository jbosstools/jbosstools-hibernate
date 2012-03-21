package org.jboss.tools.hb.ui.bot.common;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

public class ProjectExplorer {

	public static SWTBotTreeItem open(String... items) {
		SWTBotExt bot = new SWTBotExt();
		SWTOpenExt open = new SWTOpenExt(bot);
		SWTBotView view = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		SWTBotTreeItem item = Tree.open(view.bot(), items);
		return item;
	}
}
