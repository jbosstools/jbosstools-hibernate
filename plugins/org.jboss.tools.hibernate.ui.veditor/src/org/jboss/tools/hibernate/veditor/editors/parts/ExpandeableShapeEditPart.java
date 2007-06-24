/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.veditor.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FocusBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.veditor.editors.figures.TitleFigure;
import org.jboss.tools.hibernate.veditor.editors.figures.TitleLabel;
import org.jboss.tools.hibernate.veditor.editors.model.ComponentShape;
import org.jboss.tools.hibernate.veditor.editors.model.Connection;
import org.jboss.tools.hibernate.veditor.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.veditor.editors.model.ExtendedShape;
import org.jboss.tools.hibernate.veditor.editors.model.OrmShape;
import org.jboss.tools.hibernate.veditor.editors.model.Shape;


public class ExpandeableShapeEditPart extends ShapeEditPart {

	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())) {
			((ExpandeableShape)getModel()).refreshReferences(getViewer().getContents().getModel());
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ExpandeableShape.SHOW_REFERENCES.equals(prop)) {
//			((IFigure)getFigure().getChildren().get(0)).setBackgroundColor(getSelectionColor());	
//			((IFigure)getFigure().getChildren().get(0)).setForegroundColor(ResourceManager.getInstance().getColor(new RGB(255,255,255)));
		} else {
			super.propertyChange(evt);
		}
	}
	
}
