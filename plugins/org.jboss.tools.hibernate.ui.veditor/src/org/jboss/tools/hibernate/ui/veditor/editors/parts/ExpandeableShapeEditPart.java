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
package org.jboss.tools.hibernate.ui.veditor.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Shape;


public class ExpandeableShapeEditPart extends ShapeEditPart {

	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())) {
			((ExpandeableShape)getModel()).refreshReferences(getViewer().getContents().getModel());
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Shape.SHOW_SELECTION.equals(prop)) {
			if(getFigure().getChildren().size() > 0){
				((IFigure)getFigure().getChildren().get(0)).setBackgroundColor(getSelectionColor());	
				((IFigure)getFigure().getChildren().get(0)).setForegroundColor(ResourceManager.getInstance().getColor(new RGB(255,255,255)));
			}
		} else if (Shape.HIDE_SELECTION.equals(prop)) {
			if(getFigure().getChildren().size() > 0){
				((IFigure)getFigure().getChildren().get(0)).setBackgroundColor(getColor());		
				((IFigure)getFigure().getChildren().get(0)).setForegroundColor(ResourceManager.getInstance().getColor(new RGB(0,0,0)));
			}
		}else if (ExpandeableShape.SHOW_REFERENCES.equals(prop)) {
//			((IFigure)getFigure().getChildren().get(0)).setBackgroundColor(getSelectionColor());	
//			((IFigure)getFigure().getChildren().get(0)).setForegroundColor(ResourceManager.getInstance().getColor(new RGB(255,255,255)));
		} else {
			super.propertyChange(evt);
		}
	}
	
	protected List getModelChildren() {
		return ((ExpandeableShape)getModel()).getChildren(); 
	}
}
