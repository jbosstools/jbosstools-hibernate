/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleConnectionsAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.DiagramBaseRetargetAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeExpandStateAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeVisibleStateAction;
import org.jboss.tools.hibernate.ui.view.ImageBundle;

@SuppressWarnings("restriction")
public class DiagramActionBarContributor extends ActionBarContributor {

	/**
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		IWorkbenchAction workbenchAction = ActionFactory.REFRESH.create(getPage().getWorkbenchWindow());
		workbenchAction.setImageDescriptor(UiPlugin.getImageDescriptor2(ImageBundle.getString("Explorer.refreshOrmGef"))); //$NON-NLS-1$
		workbenchAction.setToolTipText(DiagramViewerMessages.EditorActionContributor_refresh_visual_mapping);
		addAction(workbenchAction);
		//
		addRetargetAction(new DiagramBaseRetargetAction(
				AutoLayoutAction.ACTION_ID, 
				DiagramViewerMessages.AutoLayoutAction_auto_layout,
				DiagramViewerMessages.AutoLayoutAction_auto_layout,
				AutoLayoutAction.img));
		//
		addRetargetAction(new DiagramBaseRetargetAction(
				ToggleConnectionsAction.ACTION_ID, 
				DiagramViewerMessages.ToggleConnectionsAction_toggle_connections,
				DiagramViewerMessages.ToggleConnectionsAction_toggle_connections,
				ToggleConnectionsAction.img));
		addRetargetAction(new DiagramBaseRetargetAction(
				ToggleShapeExpandStateAction.ACTION_ID, 
				DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state,
				DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state,
				ToggleShapeExpandStateAction.img));
		addRetargetAction(new DiagramBaseRetargetAction(
				ToggleShapeVisibleStateAction.ACTION_ID, 
				DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state,
				DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state,
				ToggleShapeVisibleStateAction.img));
		//
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_RULER_VISIBILITY, 
				GEFMessages.ToggleRulerVisibility_Label, IAction.AS_CHECK_BOX));
		
		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, 
				GEFMessages.ToggleSnapToGeometry_Label, IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, 
				GEFMessages.ToggleGrid_Label, IAction.AS_CHECK_BOX));
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager tbm) {
		tbm.add(getAction(ActionFactory.REFRESH.getId()));
		tbm.add(new Separator());	
		tbm.add(getAction(ActionFactory.UNDO.getId()));
		tbm.add(getAction(ActionFactory.REDO.getId()));
		tbm.add(new Separator());	
		//tbm.add(getAction(GEFActionConstants.ZOOM_IN));
		//tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
		String[] zoomStrings = new String[] {
			ZoomManager.FIT_ALL, 
			ZoomManager.FIT_HEIGHT, 
			ZoomManager.FIT_WIDTH
		};
		tbm.add(new ZoomComboContributionItem(getPage(), zoomStrings));
		tbm.add(new Separator());	
		tbm.add(getAction(AutoLayoutAction.ACTION_ID));
		tbm.add(new Separator());	
		tbm.add(getAction(ToggleConnectionsAction.ACTION_ID));
		tbm.add(getAction(ToggleShapeExpandStateAction.ACTION_ID));
		tbm.add(getAction(ToggleShapeVisibleStateAction.ACTION_ID));
	}

	/**
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.PRINT.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menubar) {
		super.contributeToMenu(menubar);
		MenuManager viewMenu = new MenuManager(DiagramViewerMessages.ViewMenu_label_text);
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		viewMenu.add(new Separator());
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		menubar.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	}
}