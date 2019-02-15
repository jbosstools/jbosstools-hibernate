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
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IDiagramInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmShapeEditPart;

/**
 * @author some modifications from Vitali
 */
public class DiagramInfo implements IDiagramInfo {

	protected List<IItemInfo> items = new ArrayList<IItemInfo>();
	protected OrmDiagram diagram;
	protected EditPartViewer viewer;

	public DiagramInfo(EditPartViewer viewer, OrmDiagram diagram) {
		this.viewer = viewer;
		this.diagram = diagram;
		Iterator<Shape> it = diagram.getChildrenIterator();
		while (it.hasNext()) {
			Shape child = it.next();
			if (!(child instanceof OrmShape)) {
				continue;
			}
			OrmShape ormShape = (OrmShape)child;
			if (child.isVisible()) {
				OrmShapeEditPart part = (OrmShapeEditPart)viewer
					.getEditPartRegistry().get(ormShape);
				// default values - indicate refresh model error
				int cxFigure = 6000;
				int cyFigure = 1000;
				if (part != null) {
					IFigure fig = part.getFigure();
					//cxFigure = fig.getSize().width;
					//cyFigure = fig.getSize().height;
					// use preferred size for correct autolayout
					cxFigure = fig.getPreferredSize().width;
					cyFigure = fig.getPreferredSize().height;
				}
				IItemInfo item = new DiagramElementInfo(ormShape, cxFigure, cyFigure);
				addItem(item);
			} else {
				// move invisible shapes to upper-left corner
				ormShape.setLocation(new Point(0, 0));
			}
		}
	}

	/**
	 * 
	 */
	public IItemInfo[] getItems() {
		return items.toArray(new IItemInfo[0]);
	}

	/**
	 * 
	 * @param item
	 */
	public void addItem(IItemInfo item) {
		items.add(item);
	}
}
