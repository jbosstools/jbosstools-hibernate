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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleFigure;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;

/**
 * @author some modifications from Vitali
 */
public class OrmShapeEditPart extends ExpandableShapeEditPart{

	public OrmShape getModelOrmShape() {
		return (OrmShape)getModel();
	}

	public TitleFigure getTitleFigure() {
		if (getFigure() instanceof TitleFigure) {
			return (TitleFigure)getFigure();
		}
		return null;
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		getTitleFigure().refresh();
		refresh();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Object element = getElement();
		TitleFigure figure = new TitleFigure();
		figure.setLayoutManager(new ToolbarLayout());
		String text = getOrmDiagram().getLabelProvider().getText(element);
		figure.createTitle(text, getOrmDiagram().getLabelProvider().getImage(element), getColor());
		figure.setBackgroundColor(getBackgroundColor());
		return figure;
	}

	/**
	 * @see AbstractEditPart#performRequest(Request)
	 */
	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			// double click on "class" or "table" header will toggle it's expand state 
			OrmShape shape = getModelOrmShape();
			if (shape.isExpanded()) {
				shape.collapse();
			} else {
				shape.expand();
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
		if (OrmShape.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
			getOrmDiagram().setDirty(true);
		} else if (ExpandableShape.EXPANDED.equals(prop)) {
			boolean expanded = (Boolean)evt.getNewValue();
			if (getTitleFigure() != null) {
				getTitleFigure().setExpanded(expanded);
			}
			getOrmDiagram().updateDirty(evt.getNewValue() != evt.getOldValue());
		} else if (BaseElement.REFRESH.equals(prop)) {
			if (getTitleFigure() != null) {
				getTitleFigure().setExpanded(getModelExpandableShape().isExpanded());
			}
			super.propertyChange(evt);
		} else {
			super.propertyChange(evt);
		}
		refresh();
	}

	protected void refreshVisuals() {
		Rectangle bounds = new Rectangle(getModelOrmShape().getLocation(), getFigure().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	}

	protected Color getBackgroundColor() {
		Object element = getElement();
		if (element instanceof PersistentClass || element instanceof Component) {
			return ResourceManager.getInstance().getColor(new RGB(0, 0, 0));
		} else if (element instanceof Table || element instanceof Property) {
			return ResourceManager.getInstance().getColor(new RGB(
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnR),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnG),
					Integer.parseInt(ColorConstants.Colors_DatabaseColumnB)));
		} else {
			throw new IllegalArgumentException();
		}
	}

}