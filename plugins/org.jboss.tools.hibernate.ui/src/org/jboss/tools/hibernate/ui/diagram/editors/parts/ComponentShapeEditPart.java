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

import org.eclipse.draw2d.FocusBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.ComponentFigure;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ComponentShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;

/**
 * 
 * @author some modifications from Vitali
 */
public class ComponentShapeEditPart extends ExpandableShapeEditPart {

	public ComponentFigure getComponentFigure() {
		if (getFigure() instanceof ComponentFigure) {
			return (ComponentFigure)getFigure();
		}
		return null;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		ComponentFigure figure = new ComponentFigure();
		figure.createTitle(getOrmDiagram().getLabelProvider().getText(getElement()), 
				getOrmDiagram().getLabelProvider().getImage(getElement()), getColor());
		figure.setBorder(new FocusBorder());
		figure.setSize(-1, -1);
		return figure;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
	}
	
	public ComponentShape getModelComponentShape() {
		return (ComponentShape)getModel();
	}
	
	/**
	 * @see AbstractEditPart#performRequest(Request)
	 */
	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			ExpandableShape es = getModelExpandableShape();
			if (es.isExpanded()) {
				es.collapse();
			} else {
				es.expand();
			}
		} else {
			super.performRequest(req);
		}
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ExpandableShape.EXPANDED.equals(prop)) {
			boolean expanded = (Boolean)evt.getNewValue();
			if (getComponentFigure() != null) {
				getComponentFigure().setExpanded(expanded);
			}
			BaseElement parent = getModelParent();
			if (parent.getParent() != null) {
				// refresh only parent which has a parent! so we exclude OrmDiagram here
				// refresh only basic properties
				parent.refreshBasic();
			}
			getOrmDiagram().updateDirty(evt.getNewValue() != evt.getOldValue());
		} else if (BaseElement.REFRESH.equals(prop)) {
			if (getComponentFigure() != null) {
				getComponentFigure().setExpanded(getModelExpandableShape().isExpanded());
			}
			super.propertyChange(evt);
		} else {
			super.propertyChange(evt);
		}
		refresh();
	}

	protected void refreshVisuals() {
		Rectangle bounds = null;
		if (getModel() instanceof ComponentShape) {
			bounds = new Rectangle(new Point(0, 0), getFigure().getSize());
		}
		if (bounds != null) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);		
		}
	}
}
