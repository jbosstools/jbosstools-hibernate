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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.RoundLineBorder;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleFigure;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.TitleLabel;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;

/**
 * 
 */
public class OrmShapeEditPart extends ExpandeableShapeEditPart{

	public void addNotify() {
		super.addNotify();
		if (((OrmShape)getModel()).isHiden()) {
			int i = figure.getPreferredSize().width;
			((TitleFigure)figure).setHidden(true);
			((TitleLabel)figure.getChildren().get(0)).setHidden(true);
			figure.setSize(i,-1);
			refresh();
		}
	}

	protected IFigure createFigure() {
		if (getModel() instanceof OrmShape) {
			TitleFigure figure = new TitleFigure();
			figure.setLayoutManager(new ToolbarLayout());
			TitleLabel label = new TitleLabel();
			String text = ""; //$NON-NLS-1$
			Object element = getCastedModel().getOrmElement();
			if (element instanceof RootClass) {
				text = ormLabelProvider.getText((PersistentClass)element);
			} else if (element instanceof Table) {
				text = ormLabelProvider.getText((Table)element);
			} else if (element instanceof Subclass) {
				text = ormLabelProvider.getText((Subclass)element);
			}
			label.setText(text);
			label.setIcon(ormLabelProvider.getImage(getCastedModel().getOrmElement()));
			FontData fontData[] = Display.getCurrent().getSystemFont().getFontData();
			fontData[0].setStyle(SWT.BOLD);
			//fontData[0].height++;
			label.setFont(ResourceManager.getInstance().getFont(fontData[0]));
			label.setBackgroundColor(getColor());
			label.setIcon(ormLabelProvider.getImage(getCastedModel().getOrmElement()));
			label.setLabelAlignment(PositionConstants.LEFT);
			label.setBorder(new MarginBorder(1,2,1,2));
			figure.add(label,-2);
			label.setOpaque(true);
			figure.setBackgroundColor(getBackgroundColor());
			RoundLineBorder border = new RoundLineBorder();
			border.setColor(ResourceManager.getInstance().getColor(new RGB(160, 160, 160)));
			figure.setBorder(border);
			figure.setSize(-1,-1);
			return figure;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType()) && getModel() instanceof OrmShape) {
			((OrmShape)getModel()).refreshHiden();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (OrmShape.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
			((OrmDiagram)getParent().getModel()).setDirty(true);
		} else if (OrmShape.SET_HIDEN.equals(prop)) {
			int i = figure.getPreferredSize().width;
			((TitleFigure)figure).setHidden(((Boolean)evt.getNewValue()).booleanValue());
			((TitleLabel)figure.getChildren().get(0)).setHidden(((Boolean)evt.getNewValue()).booleanValue());
			if (((Boolean)evt.getNewValue()).booleanValue()) {
				figure.setSize(i,-1);
			} else {
				figure.setSize(-1,-1);
			}
			refresh();
			((OrmDiagram)getParent().getModel()).setDirty(true);
		} else {
			super.propertyChange(evt);
		}
	}

	protected void refreshVisuals() {
		Rectangle bounds = null;
		if (getModel() instanceof OrmShape) {
			bounds = new Rectangle(((OrmShape)getModel()).getLocation(), getFigure().getSize());
		}
		if (bounds != null) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
		}
	}

	protected Color getBackgroundColor() {
		Object element = getCastedModel().getOrmElement();
		if (element instanceof PersistentClass || element instanceof Component) {
			return ResourceManager.getInstance().getColor(new RGB(0,0,0));
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