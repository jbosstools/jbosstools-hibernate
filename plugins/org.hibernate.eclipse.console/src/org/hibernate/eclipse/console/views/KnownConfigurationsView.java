/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurations.IConsoleConfigurationListener;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.ConfigurationNode;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;


/**
 * @author max
 *
 */
public class KnownConfigurationsView extends ViewPart {

	TreeViewer viewer;
	
	
	private ActionGroup actionGroup;


	private Action doubleAction;
	
	/**
	 * 
	 */
	public KnownConfigurationsView() {
		super();
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(new ConfigurationsContentProvider());
		
		viewer.setLabelProvider(new ConfigurationsLabelProvider());
		
		viewer.setInput(KnownConfigurations.getInstance().getRootNode());
		
		KnownConfigurations.getInstance().addConsoleConfigurationListener(new IConsoleConfigurationListener() {
			public void configurationAdded(ConsoleConfiguration root) {
				refreshInUI();				
			}

			private void refreshInUI() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						viewer.refresh();
					}
				});
			}

			public void configurationRemoved(ConsoleConfiguration root) {
				refreshInUI();
			}
		});
		
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

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager dropDownMenu = actionBars.getMenuManager();
		
		actionGroup.fillContextMenu(dropDownMenu);
    }
    
	protected void fillContextMenu(IMenuManager menuMgr) {
		actionGroup.setContext(new ActionContext(viewer.getSelection()));
		actionGroup.fillContextMenu(menuMgr);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
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
				BaseNode node = (BaseNode) ((IStructuredSelection)selection).getFirstElement();
				ConsoleConfiguration consoleConfiguration = node.getConsoleConfiguration();
				if(node instanceof ConfigurationNode) {
					ConsoleConfigurationCreationWizard wizard = new ConsoleConfigurationCreationWizard();
					wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(node.getConsoleConfiguration()));
					WizardDialog dialog = new WizardDialog(getViewSite().getShell(), wizard);
					dialog.open(); // This opens a dialog
				} else if(consoleConfiguration.isSessionFactoryCreated()) {
						consoleConfiguration.executeHQLQuery(node.getHQL());
					}
			}
		};
	}
	

	public void setFocus() {
		viewer.getTree().setFocus();
	}

}
