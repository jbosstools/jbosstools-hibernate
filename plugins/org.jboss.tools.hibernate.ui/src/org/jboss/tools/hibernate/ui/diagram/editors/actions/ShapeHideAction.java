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
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.command.ToggleShapeVisibleStateCommand;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPart;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ShapeHideAction extends SelectionAction {

	public static final String ACTION_ID = "shapeHideId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/shapehide.png"); //$NON-NLS-1$

	public ShapeHideAction(IWorkbenchPart editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ShapeHideAction_shape_hide);
		setToolTipText(DiagramViewerMessages.ShapeHideAction_shape_hide_tooltip);
		setImageDescriptor(img);
	}
	
	protected DiagramViewer getDiagramViewer() {
		return (DiagramViewer)getWorkbenchPart();
	}

	public void run() {
		execute(getCommand());
		if (getDiagramViewer() != null) {
			getDiagramViewer().updateSelectionActions();
		}
	}

	@SuppressWarnings("unchecked")
	public Command getCommand() {
		CompoundCommand cc = new CompoundCommand();
		if (getSelectedObjects().isEmpty()) {
			return cc;
		}
		List<OrmShape> selectedShape = new ArrayList<OrmShape>();
		Iterator it = getSelectedObjects().iterator();
		while (it.hasNext()) {
			Object firstElement = it.next();
			Object obj = null;
			if (firstElement instanceof OrmEditPart) {
				obj = ((OrmEditPart)firstElement).getModel();
			} else if (firstElement instanceof AbstractTreeEditPart) {
				obj = ((AbstractTreeEditPart)firstElement).getModel();
			}
			if (null != obj && obj instanceof OrmShape) {
				OrmShape ormShape = (OrmShape)obj;
				if (ormShape.isVisible()) {
					selectedShape.add(ormShape);
				}
			} 
		}
		if (selectedShape.size() > 0) {
			cc.add(new ToggleShapeVisibleStateCommand(selectedShape, null));
		}
		return cc;
	}

	@Override
	protected boolean calculateEnabled() {
		return canPerformAction();
	}

	@SuppressWarnings("unchecked")
	private boolean canPerformAction() {
		boolean res = false;
		if (getSelectedObjects().isEmpty()) {
			return res;
		}
		Iterator it = getSelectedObjects().iterator();
		while (it.hasNext() && !res) {
			Object firstElement = it.next();
			Object obj = null;
			if (firstElement instanceof OrmEditPart) {
				obj = ((OrmEditPart)firstElement).getModel();
			} else if (firstElement instanceof AbstractTreeEditPart) {
				obj = ((AbstractTreeEditPart)firstElement).getModel();
			}
			if (null != obj && obj instanceof OrmShape) {
				OrmShape ormShape = (OrmShape)obj;
				Object ormElement = ormShape.getOrmElement();
				if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
					if (ormShape.isVisible()) {
						res = true;
					}
				}
			} 
		}
		return res;
	}
}
