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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputs;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.hqleditor.HQLEditor;

/**
 * @author max
 *
 */
public class ExecuteHQLAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2 {

	private HQLEditor hqlEditor;

	Menu dropDown = null;

	private IWorkbenchWindow window;
	
	public ExecuteHQLAction() {
		
		setMenuCreator(this);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.EXECUTE) );
		initTextAndToolTip();
	}


	public ExecuteHQLAction(HQLEditor editor) {
		this();
		this.hqlEditor=editor;
	}


	public void dispose() {
		if(dropDown!=null) dropDown.dispose();
	}

	public Menu getMenu(Control parent) {
		if (dropDown != null) dropDown.dispose();
		dropDown = new Menu(parent);
		/**
		 * Add listener to repopulate the menu each time do keep uptodate with configurationis
		 */
		dropDown.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu currentMenu = (Menu)e.widget;
				MenuItem[] items = currentMenu.getItems();
				for (int i=0; i < items.length; i++) {
					items[i].dispose();
				}
				fillMenu(currentMenu);
			}
		});
		return dropDown;
	}

	protected void fillMenu(Menu menu) {
		ConsoleConfiguration lastUsed = HibernateConsolePlugin.getDefault().getLastUsedConfiguration();
		
		if(hqlEditor!=null) {
			lastUsed = hqlEditor.getConsoleConfiguration();
		}
		
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
				IEditorPart activeEditor = window.getActivePage().getActiveEditor();
				if(activeEditor!=null && activeEditor instanceof HQLEditor) {
					ExecuteHQLAction.this.execute(lastUsed, (HQLEditor) activeEditor);
				}
			}
		};
		//action.setImageDescriptor(ImageStore.getImageDescriptor(ImageStore.BOOKMARK) );
		
		action.setText(lastUsed.getName());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	public void run() {
		execute(hqlEditor.getConsoleConfiguration(), hqlEditor );
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent( event );
	}
	protected void execute(ConsoleConfiguration lastUsed, HQLEditor part) {
		try {	
			if(lastUsed!=null) {
				if (!lastUsed.isSessionFactoryCreated()) {
					if (part.askUserForConfiguration(lastUsed.getName())) {
						if(lastUsed.getConfiguration()==null) {
							lastUsed.build();
						}
						lastUsed.buildSessionFactory();
						lastUsed.executeHQLQuery(part.getQuery(), QueryInputs.getInstance().getQueryInputModel().getQueryParametersForQuery() );
					}
				} else {
					lastUsed.executeHQLQuery(part.getQuery(), QueryInputs.getInstance().getQueryInputModel().getQueryParametersForQuery() );
				}
			} 
		}
		finally {
			HibernateConsolePlugin.getDefault().setLastUsedConfiguration(lastUsed);
			initTextAndToolTip();
		}
		
	}

	private void initTextAndToolTip() {
		setText("Run HQL");
		setToolTipText("Run HQL");
	}

	public Menu getMenu(Menu parent) {
		throw new IllegalStateException("should not be called!");
	}

	public void setHQLEditor(HQLEditor hqlEditor) {
		this.hqlEditor = hqlEditor;		
	}

	public void setText(String text) {
			super.setText( text );
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
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
