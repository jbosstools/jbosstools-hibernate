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

import org.eclipse.draw2d.FocusBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.ComponentFigure;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleLabel;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ComponentShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;


public class ComponentShapeEditPart extends ExpandeableShapeEditPart {

	protected IFigure createFigure() {
		if (getModel() instanceof ComponentShape) {
			IFigure figure = new ComponentFigure();
			figure.setLayoutManager(new ToolbarLayout());
			Label label = new TitleLabel();
			label.setText(ormLabelProvider.getText(getCastedModel().getOrmElement()));	
			label.setBackgroundColor(getColor());
			label.setOpaque(true);
			label.setIcon(ormLabelProvider.getImage(getCastedModel().getOrmElement()));
			label.setLabelAlignment(PositionConstants.LEFT);
			label.setBorder(new MarginBorder(1,2,1,2));
			figure.add(label,-2);
			figure.setBorder(new FocusBorder());
			figure.setSize(-1,-1);
			return figure;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public void activate() {
		super.activate();
		if(this.getClass().equals(ComponentShapeEditPart.class) && !((ExpandeableShape)getModel()).isReferenceVisible()){
			((ComponentShape)getModel()).refHide = true;
			((ComponentShape)getModel()).refreshChildsHiden(((OrmDiagram)getViewer().getContents().getModel()));
			((ExpandeableShape)getModel()).getOrmDiagram().setDirty(false);
		}
	}
	
	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType()) && getModel() instanceof ComponentShape) {
			((ComponentShape)getModel()).refreshChildsHiden(((OrmDiagram)getViewer().getContents().getModel()));
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ComponentShape.SET_CHILDS_HIDEN.equals(prop)) {
			int i = figure.getPreferredSize().width;
			((ComponentFigure)figure).setChildsHiden(((Boolean)evt.getNewValue()).booleanValue());
			
			if(((Boolean)evt.getNewValue()).booleanValue()) {
				figure.setSize(i,-1);
			} else {
				figure.setSize(-1,-1);
			}
			
			referenceList.add((OrmShape)getCastedModel().getParent());
			refreshReferences((ExpandeableShape)getCastedModel(), ((ExpandeableShape)getCastedModel()).isReferenceVisible());
			
			((OrmShape)getParent().getModel()).refreshReference();
		} else {
			super.propertyChange(evt);
		}
	}

	protected void refreshVisuals() {
		Rectangle bounds = null;
		if (getModel() instanceof ComponentShape) {
			bounds = new Rectangle(new Point(0,0), getFigure().getSize());
		}
		if (bounds != null) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);		
		}
	}
}
