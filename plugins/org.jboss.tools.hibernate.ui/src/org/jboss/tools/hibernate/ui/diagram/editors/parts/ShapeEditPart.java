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
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
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
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
public class ShapeEditPart extends OrmEditPart implements NodeEditPart {

	protected ChopboxAnchorNearestSide sourceAnchor = null;
	protected ChopboxAnchorNearestSide targetAnchor = null;

	public void setModel(Object model) {
		super.setModel(model);
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesSelectionEditPolicy());
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Label label = new Label();
		FontData fontData[] = Display.getCurrent().getSystemFont().getFontData();
		fontData[0].height = getOrmDiagram().getFontHeight();
		label.setFont(ResourceManager.getInstance().getFont(fontData[0]));
		label.setText(getOrmDiagram().getLabelProvider().getText(getElement()));
		label.setBackgroundColor(getColor());
		label.setIcon(getOrmDiagram().getLabelProvider().getImage(getElement()));
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setOpaque(true);
		TopLineBorder border = new TopLineBorder(1, 2 + getModelShape().getIndent(), 1, 2);
		final OrmShapeEditPart osep = getOrmShapeEditPart();
		border.setColor(osep != null ? osep.getColor() : ResourceManager.getInstance().getColor(new RGB(0, 0, 0)));
		label.setBorder(border);
		return label;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (BaseElement.SELECTED.equals(prop)) {
			updateSelected((Boolean)evt.getNewValue());
		} else if (BaseElement.VISIBLE.equals(prop)) {
			getFigure().setVisible(((Boolean)evt.getNewValue()).booleanValue());
		} else if (BaseElement.REFRESH.equals(prop)) {
			getFigure().setVisible(getModelShape().isVisible());
			updateSelected(getModelShape().isSelected());
		} else if (Shape.SET_FOCUS.equals(prop)) {
			getViewer().select(this);
			getViewer().reveal(this);
		}
		refresh();
	}
	
	protected void updateSelected(boolean selected) {
		//setSelected(selected ? EditPart.SELECTED : EditPart.SELECTED_NONE);
		if (!selected) {
			setSelected(EditPart.SELECTED_NONE);
		}
		getFigure().setBackgroundColor(selected ? getSelectionColor() : getColor());
		getFigure().setForegroundColor(ResourceManager.getInstance().getColor(
				selected ? new RGB(255, 255, 255) : new RGB(0, 0, 0)));
	}

	/**
	 * @see AbstractEditPart#performRequest(Request)
	 */
	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			if (getModelShape().getOrmElement() instanceof Column) {
				if (getModelShape().getTargetConnections().size() > 0) {
					getModelShape().getTargetConnections().get(0).getSource().setFocus();
				}
			} else {
				if (getModelShape().getSourceConnections().size() > 0) {
					getModelShape().getSourceConnections().get(0).getTarget().setFocus();
				}
			}
		} else {
			super.performRequest(req);
		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getModelShape().addPropertyChangeListener(this);
		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getModelShape().removePropertyChangeListener(this);
		}
	}

	protected Shape getModelShape() {
		return (Shape) getModel();
	}

	protected List<Connection> getModelSourceConnections() {
		return getModelShape().getSourceConnections();
	}

	protected List<Connection> getModelTargetConnections() {
		return getModelShape().getTargetConnections();
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
		while (part != null && !(part instanceof OrmShapeEditPart)) {
			part = part.getParent();
			if (i++ > 4) {
				throw new RuntimeException();
			}
		}
		return (OrmShapeEditPart)part;
	}

	protected Color getColor() {
		final Object el = getElement();
		if (el instanceof PersistentClass || el instanceof Component)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentClassR),
					Integer.parseInt(ColorConstants.Colors_PersistentClassG),
					Integer.parseInt(ColorConstants.Colors_PersistentClassB)));
		else if (el instanceof Property || el instanceof SimpleValue)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentFieldR),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldG),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldB)));
		else if (el instanceof Column)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnR),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnG),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnB)));
		else if (el instanceof Table)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseTableR),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableG),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableB)));
		else if (el instanceof DependantValue)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseTableR),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableG),
					Integer.parseInt(ColorConstants.Colors_DatabaseTableB)));
		else if (el instanceof OneToMany)
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_PersistentFieldR),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldG),
					Integer.parseInt(ColorConstants.Colors_PersistentFieldB)));
		else
			return ResourceManager.getInstance().getColor(new RGB(255, 0, 0));
	}

	protected Color getSelectionColor() {
		final Object el = getElement();
		if (el instanceof PersistentClass || el instanceof Property ||
				el instanceof SimpleValue || el instanceof OneToMany) {
			return ResourceManager.getInstance().getColor(new RGB(112, 161, 99));
		} else if (el instanceof Table || el instanceof Column) {
			return ResourceManager.getInstance().getColor(new RGB(66, 173, 247));
		}
		return ResourceManager.getInstance().getColor(new RGB(255, 0, 0));
	}

	private class ShapesSelectionEditPolicy extends SelectionEditPolicy {

		protected void hideSelection() {
			getModelShape().setSelected(false);
			Iterator<Connection> iter = getModelShape().getSourceConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.setSelected(false);
			}
			iter = getModelShape().getTargetConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.setSelected(false);
			}
		}

		protected void showSelection() {
			getModelShape().setSelected(true);
			Iterator<Connection> iter  = getModelShape().getSourceConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.setSelected(true);
			}
			iter = getModelShape().getTargetConnections().iterator();
			while (iter.hasNext()) {
				Connection element = iter.next();
				element.setSelected(true);
			}
		}

	}

	protected Object getElement() {
		return getModelShape().getOrmElement();
	}
}
