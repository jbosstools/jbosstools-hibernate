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
package org.jboss.tools.hibernate.veditor.editors.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class OrmEditPart extends AbstractGraphicalEditPart implements EditPartListener{

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
		if(this.getSelected() == EditPart.SELECTED_PRIMARY && OrmEditPart.this.getParent() instanceof DiagramEditPart) {
			((DiagramEditPart)OrmEditPart.this.getParent()).setToFront(this);
		}
	}

	protected IFigure createFigure() {
		return null;
	}

	protected void createEditPolicies() {
	}

	protected void refreshSourceConnections() {
		int i;
		org.eclipse.gef.ConnectionEditPart editPart;
		Object model;

		Map modelToEditPart = new HashMap();
		List editParts = getSourceConnections();

		for (i = 0; i < editParts.size(); i++) {
			editPart = (ConnectionEditPart) editParts.get(i);
			modelToEditPart.put(editPart.getModel(), editPart);
		}

		List modelObjects = getModelSourceConnections();
		if (modelObjects == null)
			modelObjects = new ArrayList();

		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < editParts.size()) {
				editPart = (ConnectionEditPart) editParts.get(i);
				if (editPart.getModel() == model) {
					if (editPart.getSource() != this)
						editPart.setSource(this);
					continue;
				}
			}

			editPart = (ConnectionEditPart) modelToEditPart.get(model);
			if (editPart != null)
				reorderSourceConnection(editPart, i);
			else {
				editPart = createOrFindConnection(model);
				addSourceConnection(editPart, i);
			}
		}

		// Remove the remaining EditParts
		List trash = new ArrayList();
		for (; i < editParts.size(); i++)
			trash.add(editParts.get(i));
		for (i = 0; i < trash.size(); i++)
			removeSourceConnection((ConnectionEditPart) trash.get(i));
	}

	protected void refreshTargetConnections() {
		int i;
		org.eclipse.gef.ConnectionEditPart editPart;
		Object model;

		Map mapModelToEditPart = new HashMap();
		List connections = getTargetConnections();

		for (i = 0; i < connections.size(); i++) {
			editPart = (ConnectionEditPart) connections.get(i);
			mapModelToEditPart.put(editPart.getModel(), editPart);
		}

		List modelObjects = getModelTargetConnections();
		if (modelObjects == null)
			modelObjects = new ArrayList();

		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < connections.size()) {
				editPart = (org.eclipse.gef.ConnectionEditPart) connections
						.get(i);
				if (editPart.getModel() == model) {
					if (editPart.getTarget() != this)
						editPart.setTarget(this);
					continue;
				}
			}

			editPart = (org.eclipse.gef.ConnectionEditPart) mapModelToEditPart
					.get(model);
			if (editPart != null)
				reorderTargetConnection(editPart, i);
			else {
				editPart = createOrFindConnection(model);
				addTargetConnection(editPart, i);
			}
		}

		// Remove the remaining Connection EditParts
		List trash = new ArrayList();
		for (; i < connections.size(); i++)
			trash.add(connections.get(i));
		for (i = 0; i < trash.size(); i++)
			removeTargetConnection((ConnectionEditPart) trash.get(i));
	}

	protected void removeSourceConnection(ConnectionEditPart connection) {
		if (connection.getSource() != this)
			return;
		fireRemovingSourceConnection(connection, getSourceConnections()
				.indexOf(connection));
		connection.deactivate();
		connection.setSource(null);
		primRemoveSourceConnection(connection);
	}

	protected void removeTargetConnection(ConnectionEditPart connection) {
		if (connection.getTarget() != this)
			return;
		fireRemovingTargetConnection(connection, getTargetConnections()
				.indexOf(connection));
		connection.setTarget(null);
		primRemoveTargetConnection(connection);
	}

}
