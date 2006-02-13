/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.eclipse.console.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.workbench.xpl.AnyAdaptableLabelProvider;


/**
 * @author max
 *
 */
public class KnownConfigurationsView extends ViewPart {

	public static final String ID = "org.hibernate.eclipse.console.views.KnownConfigurationsView";

	TreeViewer viewer;
	
	private ActionGroup actionGroup;
	private Action doubleAction;
	
	public KnownConfigurationsView() {
		super();
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		/*viewer.setContentProvider(new ConfigurationsContentProvider() );*/
		
		/*viewer.setLabelProvider(new ConfigurationsLabelProvider() );*/
		
		
		viewer.setLabelProvider(new AnyAdaptableLabelProvider());
		
		viewer.setContentProvider(new KnownConfigurationsProvider());
		
		//viewer.setInput(KnownConfigurations.getInstance().getRootNode() );
		viewer.setInput( KnownConfigurations.getInstance() );
		
		makeActions();
		createContextMenu();		
		hookDoubleClick();
		provideSelection();
	}

    private void hookDoubleClick() {
    	viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleAction.run();
			}
		});
	}

	private void provideSelection() {
		getSite().setSelectionProvider(viewer);
	}

	private void createContextMenu ()
    {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl() );
		
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		IActionBars actionBars = getViewSite().getActionBars();
		
		IMenuManager dropDownMenu = actionBars.getMenuManager();
		
		actionGroup.fillContextMenu(dropDownMenu);
		actionGroup.fillActionBars(actionBars);
    }
    
	protected void fillContextMenu(IMenuManager menuMgr) {
		actionGroup.setContext(new ActionContext(viewer.getSelection() ) );
		actionGroup.fillContextMenu(menuMgr);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS) );		
	}

	/**
	 * 
	 */
	private void makeActions() {
		
		this.actionGroup = new ConfigurationsViewActionGroup(this, viewer);
		
		this.doubleAction = new Action() {
			public void run() {
				// TODO: make action dependent on having a connected console configuration!
				ISelection selection = viewer.getSelection();
				Object firstElement = ( (IStructuredSelection)selection).getFirstElement();
				if(firstElement instanceof ConsoleConfiguration) {
					new EditConsoleConfiguration((ConsoleConfiguration)firstElement).run();
				} else if (firstElement instanceof BaseNode){
					BaseNode node = (BaseNode) firstElement;
					ConsoleConfiguration consoleConfiguration = node.getConsoleConfiguration();
					if(consoleConfiguration.isSessionFactoryCreated() ) {
						consoleConfiguration.executeHQLQuery(node.getHQL() );
					}
				}
			}
		};
	}
	
	public void dispose() {
		super.dispose();
		actionGroup.dispose();
		
	}

	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public Object getAdapter(Class adapter) {
		if (adapter.equals(IPropertySheetPage.class) )
		{
			PropertySheetPage page = new PropertySheetPage();
			page.setPropertySourceProvider(new ConsoleConfigurationPropertySourceProvider() );
			return page;
		}
		
		return super.getAdapter( adapter );
	}
}
