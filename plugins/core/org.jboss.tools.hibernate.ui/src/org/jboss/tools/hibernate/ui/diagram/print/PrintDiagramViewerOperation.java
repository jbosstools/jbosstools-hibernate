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
package org.jboss.tools.hibernate.ui.diagram.print;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

/**
 * @author Vitali Yemialyanchyk
 */
public class PrintDiagramViewerOperation extends PrintGraphicalViewerOperation {
	
	private double zoom = 1.0;

	public PrintDiagramViewerOperation(Printer p, GraphicalViewer g) {
		super(p, g);
	}

	/**
	 * @see org.eclipse.gef.print.PrintGraphicalViewerOperation#setupPrinterGraphicsFor(Graphics graphics, IFigure figure)
	 */
	protected void setupPrinterGraphicsFor(Graphics graphics, IFigure figure) {
		double dpiScale = (double)getPrinter().getDPI().x / Display.getCurrent().getDPI().x;
		
		Rectangle printRegion = getPrintRegion();
		// put the print region in display coordinates
		printRegion.width /= dpiScale;
		printRegion.height /= dpiScale;
		
		Rectangle bounds = figure.getBounds();
		double xScale = (double)printRegion.width / bounds.width;
		double yScale = (double)printRegion.height / bounds.height;
		switch (getPrintMode()) {
			case FIT_PAGE:
				graphics.scale(Math.min(xScale, yScale) * dpiScale);
				break;
			case FIT_WIDTH:
				graphics.scale(xScale * dpiScale);
				break;
			case FIT_HEIGHT:
				graphics.scale(yScale * dpiScale);
				break;
			default:
				graphics.scale(getZoom() * dpiScale);
		}
		graphics.setForegroundColor(figure.getForegroundColor());
		graphics.setBackgroundColor(figure.getBackgroundColor());
		graphics.setFont(figure.getFont());
	}


	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
}
