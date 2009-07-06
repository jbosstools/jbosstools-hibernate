/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class OrmEditPart extends AbstractGraphicalEditPart implements EditPartListener {

	public void setModel(Object model) {
		super.setModel(model);
		addEditPartListener(this);
	}

	public void childAdded(EditPart child, int index) {
	}

	public void partActivated(EditPart editpart) {
	}

	public void partDeactivated(EditPart editpart) {
	}

	public void removingChild(EditPart child, int index) {
	}

	public void selectedStateChanged(EditPart editpart) {
		if (this.getSelected() == EditPart.SELECTED_PRIMARY && OrmEditPart.this.getParent() instanceof DiagramEditPart) {
			((DiagramEditPart)OrmEditPart.this.getParent()).setToFront(this);
		}
	}

	protected IFigure createFigure() {
		return null;
	}

	protected void createEditPolicies() {
	}

	@SuppressWarnings("unchecked")
	protected void refreshSourceConnections() {
		int i;
		org.eclipse.gef.ConnectionEditPart editPart;
		Object model;

		Map<Object, ConnectionEditPart> modelToEditPart = new HashMap<Object, ConnectionEditPart>();
		List<ConnectionEditPart> editParts = getSourceConnections();

		for (i = 0; i < editParts.size(); i++) {
			editPart = editParts.get(i);
			modelToEditPart.put(editPart.getModel(), editPart);
		}

		List<Object> modelObjects = getModelSourceConnections();
		if (modelObjects == null)
			modelObjects = new ArrayList<Object>();

		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < editParts.size()) {
				editPart = editParts.get(i);
				if (editPart.getModel() == model) {
					if (editPart.getSource() != this) {
						editPart.setSource(this);
					}
					continue;
				}
			}

			editPart = modelToEditPart.get(model);
			if (editPart != null) {
				reorderSourceConnection(editPart, i);
			} else {
				editPart = createOrFindConnection(model);
				addSourceConnection(editPart, i);
			}
		}

		// Remove the remaining EditParts
		List<ConnectionEditPart> trash = new ArrayList<ConnectionEditPart>();
		for (; i < editParts.size(); i++) {
			trash.add(editParts.get(i));
		}
		for (i = 0; i < trash.size(); i++) {
			removeSourceConnection(trash.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	protected void refreshTargetConnections() {
		int i;
		org.eclipse.gef.ConnectionEditPart editPart;
		Object model;

		Map<Object, ConnectionEditPart> mapModelToEditPart = new HashMap<Object, ConnectionEditPart>();
		List<ConnectionEditPart> connections = getTargetConnections();

		for (i = 0; i < connections.size(); i++) {
			editPart = connections.get(i);
			mapModelToEditPart.put(editPart.getModel(), editPart);
		}
		List<?> modelObjects = getModelTargetConnections();
		if (modelObjects == null) {
			modelObjects = new ArrayList<Object>();
		}
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < connections.size()) {
				editPart = connections.get(i);
				if (editPart.getModel() == model) {
					if (editPart.getTarget() != this) {
						editPart.setTarget(this);
					}
					continue;
				}
			}

			editPart = mapModelToEditPart.get(model);
			if (editPart != null) {
				reorderTargetConnection(editPart, i);
			} else {
				editPart = createOrFindConnection(model);
				addTargetConnection(editPart, i);
			}
		}

		// Remove the remaining Connection EditParts
		List<ConnectionEditPart> trash = new ArrayList<ConnectionEditPart>();
		for (; i < connections.size(); i++) {
			trash.add(connections.get(i));
		}
		for (i = 0; i < trash.size(); i++) {
			removeTargetConnection(trash.get(i));
		}
	}

	protected void removeSourceConnection(ConnectionEditPart connection) {
		if (connection.getSource() != this) {
			return;
		}
		fireRemovingSourceConnection(connection, getSourceConnections()
				.indexOf(connection));
		connection.deactivate();
		connection.setSource(null);
		primRemoveSourceConnection(connection);
	}

	protected void removeTargetConnection(ConnectionEditPart connection) {
		if (connection.getTarget() != this) {
			return;
		}
		fireRemovingTargetConnection(connection, getTargetConnections()
				.indexOf(connection));
		connection.setTarget(null);
		primRemoveTargetConnection(connection);
	}

}
