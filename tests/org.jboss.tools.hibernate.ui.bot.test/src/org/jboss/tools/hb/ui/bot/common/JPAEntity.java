package org.jboss.tools.hb.ui.bot.common;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;

/*
 * JPA entity bot common class
 */
public class JPAEntity {
		
		static SWTBotExt bot = new SWTBotExt();
		static SWTOpenExt open = new SWTOpenExt(bot);
		
		public static void create(String packageName, String entityName) {
			SWTBot bot = open.newObject(ActionItem.NewObject.JPAEntity.LABEL);
			String title = "New JPA Entity";
			bot.waitUntil(shellIsActive(title));
			SWTBotShell shell = bot.shell(title);
			bot.textWithLabel("Java package:").setText(packageName);
			bot.textWithLabel("Class name:").setText(entityName);
			bot.button("Finish").click();		
			bot.waitUntil(shellCloses(shell));
		}
}
