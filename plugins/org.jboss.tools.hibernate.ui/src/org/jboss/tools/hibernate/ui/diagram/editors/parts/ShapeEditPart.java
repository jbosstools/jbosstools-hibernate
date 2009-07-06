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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleFigure;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TopLineBorder;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ModelElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.view.OrmLabelProvider;


public class ShapeEditPart extends OrmEditPart implements PropertyChangeListener,  NodeEditPart {

	protected OrmLabelProvider ormLabelProvider = new OrmLabelProvider();

	public void setModel(Object model) {
		super.setModel(model);
		ModelElement modelTmp = (ModelElement)model;
		while (modelTmp.getParent() != null) {
			modelTmp = modelTmp.getParent();
		}
		if (modelTmp instanceof OrmDiagram) {
			ConsoleConfiguration consoleConfig = ((OrmDiagram)modelTmp).getConsoleConfig();
			ormLabelProvider.setConfig(consoleConfig.getConfiguration());
		}
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesSelectionEditPolicy());
	}


	protected IFigure createFigure() {
		if (getModel() instanceof Shape) {
			Label label = new Label();
			label.setText(ormLabelProvider.getText(getElement()));
			label.setBackgroundColor(getColor());
			label.setIcon(ormLabelProvider.getImage(getElement()));
			label.setLabelAlignment(PositionConstants.LEFT);
			label.setOpaque(true);
			TopLineBorder border = new TopLineBorder(1,2+getCastedModel().getIndent(),1,2);
			border.setColor(getOrmShapeEditPart().getColor());
			label.setBorder(border);
			return label;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Shape.SHOW_SELECTION.equals(prop)) {
			getFigure().setBackgroundColor(getSelectionColor());
			getFigure().setForegroundColor(ResourceManager.getInstance().getColor(new RGB(255,255,255)));
		} else if (Shape.HIDE_SELECTION.equals(prop)) {
			getFigure().setBackgroundColor(getColor());
			getFigure().setForegroundColor(ResourceManager.getInstance().getColor(new RGB(0,0,0)));
		} else if (Shape.SET_FOCUS.equals(prop)) {
			getViewer().select(this);
			getViewer().reveal(this);
		}
		refresh();
	}

	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())) {
			if (getCastedModel().getOrmElement() instanceof Column) {
				if (getCastedModel().getTargetConnections().size() > 0) {
					getCastedModel().getTargetConnections().get(0).getSource().setFocus();
				}
			} else {
				if (getCastedModel().getSourceConnections().size() > 0) {
					getCastedModel().getSourceConnections().get(0).getTarget().setFocus();
				}
			}
		}
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			getCastedModel().addPropertyChangeListener(this);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getCastedModel().removePropertyChangeListener(this);
		}
	}

	protected Shape getCastedModel() {
		return (Shape) getModel();
	}

	protected List<Connection> getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	protected List<Connection> getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	protected ConnectionAnchor getConnectionAnchor() {
		ChopboxAnchor anchor = new ChopboxAnchor(getFigure()){
			public Point getLocation(Point reference) {
				Rectangle r = getOwner().getBounds().getCopy();
				getOwner().translateToAbsolute(r);
				if (getOwner() instanceof TitleFigure) {
					r = ((IFigure)getOwner().getChildren().get(0)).getBounds().getCopy();
					((IFigure)getOwner().getChildren().get(0)).translateToAbsolute(r);
				}
				OrmShapeEditPart part = getOrmShapeEditPart();
				Point p = r.getCenter();
				if (reference.x < p.x) {
					p.x -= part.getFigure().getBounds().width / 2;
				} else {
					p.x += part.getFigure().getBounds().width / 2;
				}
				return p;
			}
		};
		return anchor;
	}

	protected OrmShapeEditPart getOrmShapeEditPart() {
		int i = 0;
		EditPart part = this;
		while (!((part instanceof OrmShapeEditPart))) {
			part = part.getParent();
			if (i++ > 4) {
				throw new RuntimeException();
			}
		}
		return (OrmShapeEditPart)part;
	}

	protected Color getColor() {
		Object element = getCastedModel().getOrmElement();
		if (element instanceof PersistentClass || element instanceof Component)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentClassR),
					Integer.parseInt(ColorConstants.Colors_PersistentClassG),
					Integer.parseInt(ColorConstants.Colors_PersistentClassB)));
		else if (element instanceof Property || element instanceof SimpleValue)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentFieldR),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldG),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldB)));
		else if (element instanceof Column)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnR),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnG),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnB)));
		else if (element instanceof Table)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseTableR),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableG),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableB)));
		else if (element instanceof DependantValue)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseTableR),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableG),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableB)));
		else if (element instanceof OneToMany)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentFieldR),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldG),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldB)));
		else
			return ResourceManager.getInstance().getColor(new RGB(255, 0, 0));
	}

	protected Color getSelectionColor() {
	if (getCastedModel().getOrmElement() instanceof PersistentClass ||
				getCastedModel().getOrmElement() instanceof Property ||
				getCastedModel().getOrmElement() instanceof SimpleValue ||
				getCastedModel().getOrmElement() instanceof OneToMany)
			return ResourceManager.getInstance().getColor(new RGB(112,161,99));
		else if (getCastedModel().getOrmElement() instanceof Table || getCastedModel().getOrmElement() instanceof Column)
			return ResourceManager.getInstance().getColor(new RGB(66,173,247));
		return ResourceManager.getInstance().getColor(new RGB(255,0,0));
	}

	private class ShapesSelectionEditPolicy extends SelectionEditPolicy {

		protected void hideSelection() {
			getCastedModel().hideSelection();
			Iterator<Connection> iter = getCastedModel().getSourceConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.hideSelection();
			}
			iter = getCastedModel().getTargetConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.hideSelection();
			}
		}

		protected void showSelection() {
			getCastedModel().showSelection();
			Iterator<Connection> iter  = getCastedModel().getSourceConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.showSelection();
			}
			iter = getCastedModel().getTargetConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.showSelection();
			}
		}

	}

	protected Object getElement() {
		Object element = getCastedModel().getOrmElement();
		return element;
	}
}
