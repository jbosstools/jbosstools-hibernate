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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleLabel;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TopLineBorder;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
public class ExpandableShapeEditPart extends ShapeEditPart {
	
	public TitleLabel getTitleLabel() {
		if (getFigure() instanceof TitleLabel) {
			return (TitleLabel)getFigure();
		}
		return null;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Label label = new TitleLabel(getOrmDiagram().getFontHeight());
		label.setText(getOrmDiagram().getLabelProvider().getText(getElement()));	
		label.setBackgroundColor(getColor());
		label.setIcon(getOrmDiagram().getLabelProvider().getImage(getElement()));
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setOpaque(true);
		TopLineBorder border = 
			new TopLineBorder(1, 2 + getModelShape().getIndent(), 1, 2);
		border.setColor(getOrmShapeEditPart().getColor());
		label.setBorder(border);
		return label;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		getModelExpandableShape().refresh();
	}

	protected ExpandableShape getModelExpandableShape() {
		return (ExpandableShape) getModel();
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
			if (getTitleLabel() != null) {
				getTitleLabel().setExpanded(expanded);
			}
			BaseElement parent = getModelParent();
			if (parent.getParent() != null) {
				// refresh only parent which has a parent! so we exclude OrmDiagram here
				// refresh only basic properties
				parent.refreshBasic();
			}
			getOrmDiagram().updateDirty(evt.getNewValue() != evt.getOldValue());
		} else if (BaseElement.REFRESH.equals(prop)) {
			if (getTitleLabel() != null) {
				getTitleLabel().setExpanded(getModelExpandableShape().isExpanded());
			}
			super.propertyChange(evt);
			BaseElement parent = getModelParent();
			if (parent.getParent() != null) {
				// refresh only parent which has a parent! so we exclude OrmDiagram here
				// refresh only basic properties
				parent.refreshBasic();
			}
		} else {
			super.propertyChange(evt);
		}
		refresh();
	}
	
	@Override
	protected void updateSelected(boolean selected) {
		//setSelected(selected ? EditPart.SELECTED : EditPart.SELECTED_NONE);
		if (!selected) {
			setSelected(EditPart.SELECTED_NONE);
		}
		IFigure updateFigure;
		if (getFigure().getChildren().size() > 0) {
			updateFigure = (IFigure)getFigure().getChildren().get(0);	
		} else {
			updateFigure = getFigure();	
		}
		Color background = selected ? getSelectionColor() : getColor();
		Color foreground = ResourceManager.getInstance().getColor(
				selected ? new RGB(255, 255, 255) : new RGB(0, 0, 0));
		updateFigure.setBackgroundColor(background);	
		updateFigure.setForegroundColor(foreground);
	}
	
	/**
	 * Returns a <code>List</code> containing the children model objects.
	 * @return the List of children
	 */
	@Override
	protected List<Shape> getModelChildren() {
		List<Shape> res = new ArrayList<Shape>();
		Iterator<Shape> it = getModelExpandableShape().getChildrenIterator();
		while (it.hasNext()) {
			res.add(it.next());
		}
		return res;
	}
}
