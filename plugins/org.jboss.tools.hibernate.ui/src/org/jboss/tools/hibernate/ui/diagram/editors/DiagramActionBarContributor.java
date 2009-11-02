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
import org.eclipse.jface.action.Action;
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
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ActionMenu;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ConnectionRouterFanAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ConnectionRouterManhattanAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleAssociationAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleClassMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleConnectionsAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.DiagramBaseRetargetAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleForeignKeyConstraintAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.TogglePropertyMappingAction;
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
		DiagramBaseRetargetAction diagramAction;
		Action[] act;
		//
		diagramAction = new DiagramBaseRetargetAction(
				AutoLayoutAction.ACTION_ID, 
				DiagramViewerMessages.AutoLayoutAction_auto_layout,
				DiagramViewerMessages.AutoLayoutAction_auto_layout,
				AutoLayoutAction.img);
		addRetargetAction(diagramAction);
		//
		DiagramBaseRetargetAction diagramAction1 = new DiagramBaseRetargetAction(
				ToggleConnectionsAction.ACTION_ID, 
				DiagramViewerMessages.ToggleConnectionsAction_toggle_connections,
				DiagramViewerMessages.ToggleConnectionsAction_toggle_connections,
				ToggleConnectionsAction.img);
		addRetargetAction(diagramAction1);
		//
		DiagramBaseRetargetAction diagramAction2 = new DiagramBaseRetargetAction(
				ToggleShapeExpandStateAction.ACTION_ID, 
				DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state,
				DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state_tooltip,
				ToggleShapeExpandStateAction.img);
		addRetargetAction(diagramAction2);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ToggleShapeVisibleStateAction.ACTION_ID, 
				DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state,
				DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state_tooltip,
				ToggleShapeVisibleStateAction.img);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ToggleClassMappingAction.ACTION_ID, 
				DiagramViewerMessages.ToggleClassMappingAction_class_mappings,
				DiagramViewerMessages.ToggleClassMappingAction_class_mappings,
				ToggleClassMappingAction.img, IAction.AS_CHECK_BOX);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				TogglePropertyMappingAction.ACTION_ID, 
				DiagramViewerMessages.TogglePropertyMappingAction_property_mappings,
				DiagramViewerMessages.TogglePropertyMappingAction_property_mappings,
				TogglePropertyMappingAction.img, IAction.AS_CHECK_BOX);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ToggleAssociationAction.ACTION_ID, 
				DiagramViewerMessages.ToggleAssociationAction_associations,
				DiagramViewerMessages.ToggleAssociationAction_associations,
				ToggleAssociationAction.img, IAction.AS_CHECK_BOX);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ToggleForeignKeyConstraintAction.ACTION_ID, 
				DiagramViewerMessages.ToggleForeignKeyConstraintAction_foreign_key_constraints,
				DiagramViewerMessages.ToggleForeignKeyConstraintAction_foreign_key_constraints,
				ToggleForeignKeyConstraintAction.img, IAction.AS_CHECK_BOX);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ConnectionRouterManhattanAction.ACTION_ID, 
				DiagramViewerMessages.ConnectionRouterManhattanAction_select_manhattan_connection_router,
				DiagramViewerMessages.ConnectionRouterManhattanAction_select_manhattan_connection_router,
				ConnectionRouterManhattanAction.img, IAction.AS_RADIO_BUTTON);
		addRetargetAction(diagramAction);
		//
		diagramAction = new DiagramBaseRetargetAction(
				ConnectionRouterFanAction.ACTION_ID, 
				DiagramViewerMessages.ConnectionRouterFanAction_select_fan_connection_router,
				DiagramViewerMessages.ConnectionRouterFanAction_select_fan_connection_router,
				ConnectionRouterFanAction.img, IAction.AS_RADIO_BUTTON);
		addRetargetAction(diagramAction);
		//
		act = new Action[7];
		act[0] = (Action)getAction(TogglePropertyMappingAction.ACTION_ID);
		act[1] = (Action)getAction(ToggleClassMappingAction.ACTION_ID);
		act[2] = (Action)getAction(ToggleAssociationAction.ACTION_ID);
		act[3] = (Action)getAction(ToggleForeignKeyConstraintAction.ACTION_ID);
		act[4] = null;
		act[5] = (Action)getAction(ConnectionRouterManhattanAction.ACTION_ID);
		act[6] = (Action)getAction(ConnectionRouterFanAction.ACTION_ID);
		diagramAction1.setMenuCreator(new ActionMenu(act));
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
		//tbm.add(getAction(ActionFactory.UNDO.getId()));
		//tbm.add(getAction(ActionFactory.REDO.getId()));
		//tbm.add(new Separator());
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