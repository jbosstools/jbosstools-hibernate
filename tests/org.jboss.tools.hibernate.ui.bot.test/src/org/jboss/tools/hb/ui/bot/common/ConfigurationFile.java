package org.jboss.tools.hb.ui.bot.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

public class ConfigurationFile {
	
	public static void open(String... path) {
		SWTOpenExt open = new SWTOpenExt(new SWTBotExt());
		SWTBotView pv = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		
		Tree.open(pv.bot(), path);
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		bot.editorByTitle(path[path.length - 1]).show();
	}
}
