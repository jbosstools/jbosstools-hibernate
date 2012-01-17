package org.jboss.tools.hibernate.helper;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

public class ConsoleHelper {
	
	static Logger log = Logger.getLogger(ConsoleHelper.class);

	public static boolean consoleExists(String console) {
		SWTOpenExt open = new SWTOpenExt(new SWTBotExt());
		SWTBotView view = open.viewOpen(ActionItem.View.HibernateHibernateConfigurations.LABEL);		

		SWTBotTreeItem[] items = view.bot().tree().getAllItems();
		
		for (SWTBotTreeItem item : items) {
			log.info("Console found:" + item.getText());
			if (item.getText().equals(console)) return true;
		}
		return false;
	}
	
}
