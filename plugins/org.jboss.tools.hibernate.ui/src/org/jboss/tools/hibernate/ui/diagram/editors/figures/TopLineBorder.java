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
package org.jboss.tools.hibernate.ui.diagram.editors.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class TopLineBorder extends MarginBorder {

	private Color color;
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public TopLineBorder(int t, int l, int b, int r) {
		super(t+1, l, b, r);
	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		Rectangle r = getPaintRectangle(figure, insets);
		if (getColor() != null) {
			graphics.setForegroundColor(getColor());
		}
		graphics.drawLine(r.x, r.y, r.right(), r.y);
	}

}
