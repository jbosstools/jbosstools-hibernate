/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.views.HQLEditorView;



/**
 * @author max
 *
 */
public class ExecuteHQLAction extends Action implements IMenuCreator {

	private static final String LAST_USED_CONFIGURATION_PREFERENCE = "lastusedconfig";
	private final HQLEditorView view;

	public ExecuteHQLAction(HQLEditorView view) {
		
		this.view = view;
		setMenuCreator(this);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.EXECUTE));
		initTextAndToolTip();
	}

	public void dispose() {
	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		/**
		 * Add listener to repopulate the menu each time do keep uptodate with configurationis
		 */
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu)e.widget;
				MenuItem[] items = menu.getItems();
				for (int i=0; i < items.length; i++) {
					items[i].dispose();
				}
				fillMenu(menu);
			}
		});
		return menu;
	}

	protected void fillMenu(Menu menu) {
		ConsoleConfiguration lastUsed = getLastUsedConfiguration();
		
		if (lastUsed != null) {
			createSubAction(menu, lastUsed);
			Separator separator = new Separator();
			separator.fill(menu, -1);
		}
		
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		//Arrays.sort(bookmarks, new DisplayableComparator());
		for (int i = 0, length = configs == null ? 0 : configs.length; i < length; i++) {
			final ConsoleConfiguration bookmark = configs[i];
			createSubAction(menu, bookmark);
		}
	}

	private void createSubAction(Menu menu, final ConsoleConfiguration lastUsed) {
		Action action = new Action() {
			public void run() {
				ExecuteHQLAction.this.execute(lastUsed);
			}
		};
		//action.setImageDescriptor(ImageStore.getImageDescriptor(ImageStore.BOOKMARK));
		
		action.setText(lastUsed.getName());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	public void run() {
		execute(getLastUsedConfiguration());
	}
	protected void execute(ConsoleConfiguration lastUsed) {
		try {	
			if(lastUsed!=null && lastUsed.isSessionFactoryCreated()) {
				lastUsed.executeHQLQuery(view.getQuery());
			}
		}
		finally {
			if(lastUsed!=null) {
			HibernateConsolePlugin.getDefault().getPreferenceStore().setValue(
					LAST_USED_CONFIGURATION_PREFERENCE, lastUsed.getName());
			}
			initTextAndToolTip();
		}
		
	}

	private void initTextAndToolTip() {
		ConsoleConfiguration lastUsed = getLastUsedConfiguration();
		if (lastUsed == null) {
			setText("");
			setToolTipText("");
		} else {
			setText(lastUsed.getName());
			setToolTipText(lastUsed.getName());
		}
		
	}

	private ConsoleConfiguration getLastUsedConfiguration() {
		String lastUsedName = HibernateConsolePlugin.getDefault().getPreferenceStore().getString(
				LAST_USED_CONFIGURATION_PREFERENCE);
		ConsoleConfiguration lastUsed = lastUsedName == null 
				? null 
				: KnownConfigurations.getInstance().find(lastUsedName);
		return lastUsed;
	}

	public Menu getMenu(Menu parent) {
		throw new IllegalStateException("should not be called!");
		//return null;
	}

}
