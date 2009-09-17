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

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPart;

/**
 * Toggle visible state of selected shapes.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeVisibleStateAction extends SelectionAction {

	public static final String ACTION_ID = "toggleShapeVisibleStateId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/toggleshapevisiblestate.png"); //$NON-NLS-1$

	public ToggleShapeVisibleStateAction(IWorkbenchPart editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state);
		setToolTipText(DiagramViewerMessages.ToggleShapeVisibleStateAction_toggle_visible_state);
		setImageDescriptor(img);
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if (getSelectedObjects().isEmpty()) {
			return;
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
				selectedShape.add((OrmShape)obj);
			} 
		}
		for (OrmShape shape : selectedShape) {
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
				shape.setVisible(!shape.isVisible());
			}
		}
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
				Object ormElement = ((OrmShape)obj).getOrmElement();
				if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
					res = true;
				}
			} 
		}
		return res;
	}
}
