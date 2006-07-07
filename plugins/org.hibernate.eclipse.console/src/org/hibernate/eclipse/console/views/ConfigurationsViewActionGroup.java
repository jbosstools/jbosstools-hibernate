/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.console.actions.BuildSessionFactoryAction;
import org.hibernate.eclipse.console.actions.CriteriaEditorAction;
import org.hibernate.eclipse.console.actions.DeleteConfigurationAction;
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.actions.HQLScratchpadAction;
import org.hibernate.eclipse.console.actions.RefreshAction;

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
	private final StructuredViewer selectionProvider;
	private SelectionListenerAction scratchpadAction;
	private CriteriaEditorAction criteriaEditorAction;

	public ConfigurationsViewActionGroup(IViewPart part, StructuredViewer selectionProvider) {
		
		this.selectionProvider = selectionProvider;
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
		
		scratchpadAction = new HQLScratchpadAction();
		selectionProvider.addSelectionChangedListener(scratchpadAction);
		
		criteriaEditorAction = new CriteriaEditorAction();
		selectionProvider.addSelectionChangedListener(criteriaEditorAction);
	}

	public void dispose() {
		super.dispose();
		selectionProvider.removeSelectionChangedListener(deleteConfigurationAction);
		selectionProvider.removeSelectionChangedListener(refreshAction);
		selectionProvider.removeSelectionChangedListener(connectAction);
		selectionProvider.removeSelectionChangedListener(schemaExportAction);
		selectionProvider.removeSelectionChangedListener(editConfigurationAction);
		selectionProvider.removeSelectionChangedListener(scratchpadAction);
		selectionProvider.removeSelectionChangedListener(criteriaEditorAction);
	}
	
	public void fillContextMenu(IMenuManager menu) {
	
		menu.add(connectAction);
		menu.add(scratchpadAction);
		menu.add(criteriaEditorAction);
		menu.add(new Separator() );
		menu.add(addConfigurationAction);
		menu.add(editConfigurationAction);
		menu.add(deleteConfigurationAction);
		menu.add(new Separator() );
		menu.add(refreshAction);
		menu.add(schemaExportAction);
		
	}
	
	public void fillActionBars(IActionBars actionBars) {
		
		actionBars.getToolBarManager().add(addConfigurationAction);
		actionBars.getToolBarManager().add(scratchpadAction);
		actionBars.getToolBarManager().add(criteriaEditorAction);
	}
	
	
}
