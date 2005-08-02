/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.editors.HQLEditor;
import org.hibernate.eclipse.console.utils.EclipseImages;



/**
 * @author max
 *
 */
public class ExecuteHQLAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2 {

	private static final String LAST_USED_CONFIGURATION_PREFERENCE = "lastusedconfig";
	private HQLEditor hqlEditor;

	public ExecuteHQLAction() {
		
		setMenuCreator(this);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.EXECUTE) );
		initTextAndToolTip();
	}


	public ExecuteHQLAction(HQLEditor editor) {
		this.hqlEditor=editor;
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
		//Arrays.sort(bookmarks, new DisplayableComparator() );
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
		//action.setImageDescriptor(ImageStore.getImageDescriptor(ImageStore.BOOKMARK) );
		
		action.setText(lastUsed.getName());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	public void run() {
		execute(getLastUsedConfiguration() );
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent( event );
	}
	protected void execute(ConsoleConfiguration lastUsed) {
		try {	
			if(lastUsed!=null && lastUsed.isSessionFactoryCreated() ) {				
				lastUsed.executeHQLQuery(hqlEditor.getQuery() );
			}
		}
		finally {
			if(lastUsed!=null) {
			HibernateConsolePlugin.getDefault().getPreferenceStore().setValue(
					LAST_USED_CONFIGURATION_PREFERENCE, lastUsed.getName() );
			} else {
                HibernateConsolePlugin.getDefault().getPreferenceStore().setValue(
                        LAST_USED_CONFIGURATION_PREFERENCE, "");         
            }
			initTextAndToolTip();
		}
		
	}

	private void initTextAndToolTip() {
		ConsoleConfiguration lastUsed = getLastUsedConfiguration();
		if (lastUsed == null) {
			setText("Run HQL");
			setToolTipText("Run HQL");
		} else {
			setText(lastUsed.getName() );
			setToolTipText(lastUsed.getName() );
		}
		
	}

	private ConsoleConfiguration getLastUsedConfiguration() {
		String lastUsedName = HibernateConsolePlugin.getDefault().getPreferenceStore().getString(
				LAST_USED_CONFIGURATION_PREFERENCE);
		ConsoleConfiguration lastUsed = (lastUsedName == null || lastUsedName.trim().length()==0) 
				? null 
				: KnownConfigurations.getInstance().find(lastUsedName);
        
        if(lastUsed==null && KnownConfigurations.getInstance().getConfigurations().length==1) {
            lastUsed = KnownConfigurations.getInstance().getConfigurations()[0];
        }
        
		return lastUsed;
	}

	public Menu getMenu(Menu parent) {
		throw new IllegalStateException("should not be called!");
		//return null;
	}

	public void setHQLEditor(HQLEditor hqlEditor) {
		this.hqlEditor = hqlEditor;		
	}

	public void setText(String text) {
			super.setText( text );
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	/*private IDocument getDocument() {
		
		ITextEditor editor= getTextEditor();
		if (editor != null) {
			
			IDocumentProvider provider= editor.getDocumentProvider();
			IEditorInput input= editor.getEditorInput();
			if (provider != null && input != null)
				return provider.getDocument(input);
			
		}
		return null;
	}*/
}
