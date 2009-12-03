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
import org.eclipse.jface.action.GroupMarker;
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
import org.hibernate.eclipse.console.actions.CloseConfigAction;
import org.hibernate.eclipse.console.actions.CriteriaEditorAction;
import org.hibernate.eclipse.console.actions.DeleteConfigurationAction;
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.actions.HQLScratchpadAction;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.actions.OpenSourceAction;
import org.hibernate.eclipse.console.actions.RefreshAction;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author max
 *
 */
public class ConfigurationsViewActionGroup extends ActionGroup {

	/**
	 * menu group for open primal editors related actions.
	 * Value: <code>"group.primalEditors"</code>
	 */
	public static final String GROUP_PRIMAL_EDITORS = "group.primalEditors"; //$NON-NLS-1$
	public static final String GROUP_PRIMAL_EDITORS_LAST = "group.primalEditors.last"; //$NON-NLS-1$
	/**
	 * menu group for Hibernate Console Configuration related actions.
	 * Value: <code>"group.consoleConfig"</code>
	 */
	public static final String GROUP_CONSOLE_CONFIG = "group.consoleConfig"; //$NON-NLS-1$
	public static final String GROUP_CONSOLE_CONFIG_LAST = "group.consoleConfig.last"; //$NON-NLS-1$
	/**
	 * menu group for addition Hibernate related actions.
	 * Value: <code>"group.addition"</code>
	 */
	public static final String GROUP_ADDITION = "group.addition"; //$NON-NLS-1$
	public static final String GROUP_ADDITION_LAST = "group.addition.last"; //$NON-NLS-1$
	/**
	 * menu group for open other editors related actions.
	 * Value: <code>"group.otherEditors"</code>
	 */
	public static final String GROUP_OTHER_EDITORS = "group.otherEditors"; //$NON-NLS-1$
	public static final String GROUP_OTHER_EDITORS_LAST = "group.otherEditors.last"; //$NON-NLS-1$

	private Action addConfigurationAction;
	private SelectionListenerAction closeConfigAction;
	private SelectionListenerAction deleteConfigurationAction;
	private SelectionListenerAction refreshAction;
	//private SelectionListenerAction connectAction;
	private SelectionListenerAction reloadConfigurationAction;
	private SelectionListenerAction schemaExportAction;
	private EditConsoleConfiguration editConfigurationAction;
	private final StructuredViewer selectionProvider;
	private SelectionListenerAction hqlEditorAction;
	private CriteriaEditorAction criteriaEditorAction;
	private SelectionListenerAction openMappingAction;
	private SelectionListenerAction openSourceAction;

	public ConfigurationsViewActionGroup(IViewPart part, StructuredViewer selectionProvider) {
		
		this.selectionProvider = selectionProvider;
		addConfigurationAction = new AddConfigurationAction(part);
		
		closeConfigAction = new CloseConfigAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(closeConfigAction);
		
		deleteConfigurationAction = new DeleteConfigurationAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(deleteConfigurationAction);
		IActionBars actionBars= part.getViewSite().getActionBars();
		   actionBars.setGlobalActionHandler(
		      ActionFactory.DELETE.getId(),
		      deleteConfigurationAction);
		   
		refreshAction = new RefreshAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(refreshAction);

		reloadConfigurationAction = new ReloadConfigurationAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(reloadConfigurationAction);

		//connectAction = new BuildSessionFactoryAction(selectionProvider);
		//selectionProvider.addSelectionChangedListener(connectAction);
		/*IMenuManager manager = part.getViewSite().getActionBars().getMenuManager();
		manager.add(addConfigurationAction);*/
		
		schemaExportAction = new SchemaExportAction(selectionProvider);
		selectionProvider.addSelectionChangedListener(schemaExportAction);
				
		editConfigurationAction = new EditConsoleConfiguration();
		selectionProvider.addSelectionChangedListener(editConfigurationAction);
		
		hqlEditorAction = new HQLScratchpadAction();
		selectionProvider.addSelectionChangedListener(hqlEditorAction);
		
		criteriaEditorAction = new CriteriaEditorAction();
		selectionProvider.addSelectionChangedListener(criteriaEditorAction);
		
		openMappingAction = new OpenMappingAction();
		selectionProvider.addSelectionChangedListener(openMappingAction);
		
		openSourceAction = new OpenSourceAction();
		selectionProvider.addSelectionChangedListener(openSourceAction);
		
	}

	public void dispose() {
		super.dispose();
		selectionProvider.removeSelectionChangedListener(closeConfigAction);
		selectionProvider.removeSelectionChangedListener(deleteConfigurationAction);
		selectionProvider.removeSelectionChangedListener(refreshAction);
		selectionProvider.removeSelectionChangedListener(reloadConfigurationAction);
		selectionProvider.removeSelectionChangedListener(schemaExportAction);
		selectionProvider.removeSelectionChangedListener(editConfigurationAction);
		selectionProvider.removeSelectionChangedListener(hqlEditorAction);
		selectionProvider.removeSelectionChangedListener(criteriaEditorAction);
	}
	
	public void fillContextMenu(IMenuManager menu) {
		if (getContext() == null) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		if (selection == null) {
			return;
		}
		Object first = selection.getFirstElement();
		menu.add(new Separator(GROUP_PRIMAL_EDITORS));
		menu.appendToGroup(GROUP_PRIMAL_EDITORS, hqlEditorAction);
		menu.appendToGroup(GROUP_PRIMAL_EDITORS, criteriaEditorAction);
		menu.add(new GroupMarker(GROUP_PRIMAL_EDITORS_LAST));
		menu.add(new Separator(GROUP_CONSOLE_CONFIG));
		menu.appendToGroup(GROUP_CONSOLE_CONFIG, addConfigurationAction);
		if (first instanceof ConsoleConfiguration) {
			menu.appendToGroup(GROUP_CONSOLE_CONFIG, reloadConfigurationAction);
			menu.appendToGroup(GROUP_CONSOLE_CONFIG, editConfigurationAction);
			menu.appendToGroup(GROUP_CONSOLE_CONFIG, closeConfigAction);
			menu.appendToGroup(GROUP_CONSOLE_CONFIG, deleteConfigurationAction);
		}		
		menu.add(new GroupMarker(GROUP_CONSOLE_CONFIG_LAST));
		menu.add(new Separator(GROUP_ADDITION));
		menu.appendToGroup(GROUP_ADDITION, refreshAction);
		if (first instanceof ConsoleConfiguration){
			menu.appendToGroup(GROUP_ADDITION, schemaExportAction);
		}
		menu.add(new GroupMarker(GROUP_ADDITION_LAST));
		menu.add(new Separator(GROUP_OTHER_EDITORS));
		// TODO: shouldn't these and maybe the others not be defined via menu extension points ?
		if (first != null && (first instanceof PersistentClass || first.getClass() == Property.class)) {			
			menu.appendToGroup(GROUP_OTHER_EDITORS, openSourceAction);
			menu.appendToGroup(GROUP_OTHER_EDITORS, openMappingAction);
		}
		menu.add(new GroupMarker(GROUP_OTHER_EDITORS_LAST));
	}
	
	public void fillActionBars(IActionBars actionBars) {
		
		actionBars.getToolBarManager().add(reloadConfigurationAction);
		actionBars.getToolBarManager().add(addConfigurationAction);
		actionBars.getToolBarManager().add(hqlEditorAction);
		actionBars.getToolBarManager().add(criteriaEditorAction);
	}
	
	
}
