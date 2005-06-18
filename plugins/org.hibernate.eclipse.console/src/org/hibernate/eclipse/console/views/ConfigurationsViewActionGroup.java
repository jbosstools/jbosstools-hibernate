/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.console.actions.BuildSessionFactoryAction;
import org.hibernate.eclipse.console.actions.DeleteConfigurationAction;
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.actions.RefreshAction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 *
 */
public class ConfigurationsViewActionGroup extends ActionGroup {

	private Action addConfigurationAction;
	private SelectionListenerAction deleteConfigurationAction;
	private SelectionListenerAction refreshAction;
	private SelectionListenerAction connectAction;
	private SelectionListenerAction schemaExportAction;
	private EditConsoleConfiguration editConfigurationAction;

	public ConfigurationsViewActionGroup(IViewPart part, StructuredViewer selectionProvider) {
		
		addConfigurationAction = new AddConfigurationAction(part);
		
		deleteConfigurationAction = new DeleteConfigurationAction();
		selectionProvider.addSelectionChangedListener(deleteConfigurationAction);
		IActionBars actionBars= part.getViewSite().getActionBars();
		   actionBars.setGlobalActionHandler(
		      ActionFactory.DELETE.getId(),
		      deleteConfigurationAction);
		   
		refreshAction = new RefreshAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(refreshAction);
		
		connectAction = new BuildSessionFactoryAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(connectAction);
		/*IMenuManager manager = part.getViewSite().getActionBars().getMenuManager();
		manager.add(addConfigurationAction);*/
		
		schemaExportAction = new SchemaExportAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(schemaExportAction);
		
		editConfigurationAction = new EditConsoleConfiguration();
		selectionProvider.addSelectionChangedListener(editConfigurationAction);
		
	}

	public void fillContextMenu(IMenuManager menu) {
	
		menu.add(connectAction);
		menu.add(refreshAction);
		menu.add(new Separator() );
		menu.add(addConfigurationAction);
		menu.add(editConfigurationAction);
		menu.add(deleteConfigurationAction);
		menu.add(new Separator() );
		menu.add(schemaExportAction);
		
	}
	
	public void fillActionBars(IActionBars actionBars) {
		
		actionBars.getToolBarManager().add(addConfigurationAction);
	}
}
