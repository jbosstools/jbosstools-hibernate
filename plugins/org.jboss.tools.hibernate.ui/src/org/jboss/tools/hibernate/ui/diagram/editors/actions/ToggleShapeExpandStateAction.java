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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPart;

/**
 * Toggle expand state of selected shapes.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeExpandStateAction extends DiagramBaseAction {

	public static final String ACTION_ID = "toggleShapeExpandStateId"; //$NON-NLS-1$

	public ToggleShapeExpandStateAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state);
		setToolTipText(DiagramViewerMessages.ToggleShapeExpandStateAction_toggle_expand_state);
		setImageDescriptor(ImageDescriptor.createFromFile(
				DiagramViewer.class, "icons/toggleshapeexpandstate.png")); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public void run() {
		ISelection selection = getDiagramViewer().getEditPartViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) {
			return;
		}
		List<ExpandableShape> selectedShape = new ArrayList<ExpandableShape>();
		IStructuredSelection structedSelection = (IStructuredSelection)selection;
		if (structedSelection != null) {
			Iterator it = structedSelection.iterator();
			while (it.hasNext()) {
				Object firstElement = it.next();
				Object obj = null;
				if (firstElement instanceof OrmEditPart) {
					obj = ((OrmEditPart)firstElement).getModel();
				} else if (firstElement instanceof AbstractTreeEditPart) {
					obj = ((AbstractTreeEditPart)firstElement).getModel();
				}
				if (null != obj && obj instanceof ExpandableShape) {
					selectedShape.add((ExpandableShape)obj);
				} 
			}
		}
		for (ExpandableShape shape : selectedShape) {
			shape.setExpanded(!shape.isExpanded());
		}
	}
}
