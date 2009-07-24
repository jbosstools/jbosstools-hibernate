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

/**
 * 
 */
public class ShapeEditPart extends OrmEditPart implements PropertyChangeListener, NodeEditPart {

	protected OrmLabelProvider ormLabelProvider = new OrmLabelProvider();
	protected ChopboxAnchorNearestSide sourceAnchor = null;
	protected ChopboxAnchorNearestSide targetAnchor = null;

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
			TopLineBorder border = new TopLineBorder(1, 2 + getCastedModel().getIndent(), 1, 2);
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
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
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
		if (sourceAnchor == null) {
			sourceAnchor = createConnectionAnchor(connection, true);
		} else if (sourceAnchor.getConnection() == null) {
			sourceAnchor.setConnection(connection);
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		if (targetAnchor == null) {
			targetAnchor = createConnectionAnchor(connection, false);
		} else if (targetAnchor.getConnection() == null) {
			targetAnchor.setConnection(connection);
		}
		return targetAnchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (sourceAnchor == null) {
			sourceAnchor = createConnectionAnchor(null, true);
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (targetAnchor == null) {
			targetAnchor = createConnectionAnchor(null, false);
		}
		return targetAnchor;
	}

	protected ChopboxAnchorNearestSide createConnectionAnchor(ConnectionEditPart connection, boolean flagSource) {
		return new ChopboxAnchorNearestSide(connection, flagSource, getFigure());
	}
	
	public class ChopboxAnchorNearestSide extends ChopboxAnchor {
		
		protected ConnectionEditPart base;
		protected boolean flagSource;
		
		public ChopboxAnchorNearestSide(ConnectionEditPart base, boolean flagSource, IFigure owner) {
			super(owner);
			this.base = base;
			this.flagSource = flagSource;
		}

		public IFigure getOwner() {
			IFigure ownerFigure = super.getOwner();
			if (ownerFigure instanceof TitleFigure && ownerFigure.getChildren().size() > 0) {
				ownerFigure = (IFigure)ownerFigure.getChildren().get(0);
			}
			return ownerFigure;
		}
		
		public Point getLocation(Point reference) {
			Rectangle r = Rectangle.SINGLETON;
			IFigure ownerFigure = getOwner();
			r.setBounds(ownerFigure.getBounds());
			ownerFigure.translateToAbsolute(r);
			Point result = r.getCenter();
			boolean bPreferLeft = (reference.x < result.x);
			/** /
			// this strategy is necessary for right selection of nearest side
			// not tested cause: https://bugs.eclipse.org/bugs/show_bug.cgi?id=284153
			// TODO: test and fix this after eclipse-gef bugfix
			IFigure figureSource = null;
			IFigure figureTarget = null;
			AbstractGraphicalEditPart sepSource = null;
			AbstractGraphicalEditPart sepTarget = null;
			if (base != null) {
				sepSource = (AbstractGraphicalEditPart)base.getSource();
				sepTarget = (AbstractGraphicalEditPart)base.getTarget();
				figureSource = sepSource != null ? sepSource.getFigure() : null;
				figureTarget = sepTarget != null ? sepTarget.getFigure() : null;
			}
			if (figureSource != null && figureTarget != null) {
				Rectangle rcSource = figureSource.getBounds();
				figureSource.translateToAbsolute(rcSource);
				Rectangle rcTarget = figureTarget.getBounds();
				figureTarget.translateToAbsolute(rcTarget);
				int delta1 = Math.abs(rcSource.x - rcTarget.x);
				int delta2 = Math.abs(rcSource.x - rcTarget.x - rcTarget.width);
				int delta3 = Math.abs(rcSource.x + rcSource.width - rcTarget.x);
				int delta4 = Math.abs(rcSource.x + rcSource.width - rcTarget.x - rcTarget.width);
				if (delta1 < delta2 && delta1 < delta3 && delta1 < delta4) {
					bPreferLeft = true;
				} else if (delta4 < delta1 && delta4 < delta2 && delta4 < delta3) {
					bPreferLeft = false;
				} else if (delta2 < delta1 && delta2 < delta3 && delta2 < delta4) {
					bPreferLeft = (flagSource) ? true : false;
				} else if (delta3 < delta1 && delta3 < delta2 && delta3 < delta4) {
					bPreferLeft = (flagSource) ? false : true;
				}
			}
			/**/
			if (bPreferLeft) {
				result.x = r.x;
			} else {
				result.x = r.x + r.width;
			}
			return result;
		}

		public ConnectionEditPart getConnection() {
			return base;
		}

		public void setConnection(ConnectionEditPart connection) {
			this.base = connection;
		}
	};

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
