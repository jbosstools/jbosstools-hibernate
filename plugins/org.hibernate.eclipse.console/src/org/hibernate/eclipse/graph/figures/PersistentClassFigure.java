/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.hibernate.console.ImageConstants;

public class PersistentClassFigure extends Figure {

	//public static Color classColor = new Color(null, 188, 174, 121); // hibernate brown
	//public static Color classColor = new Color(null, 179, 202, 227); // opera tab blue
	public static final Color classColor = new Color(null, 212, 229, 254); // skype says	
	static Font classHeaderFont = new Font(null, "Arial", 12, SWT.BOLD); //$NON-NLS-1$
	
	
	private PropertiesFigure propertiesFigure;
	private NodeHeaderFigure header;

	public PersistentClassFigure(String name) {
		header = new NodeHeaderFigure(name, "Class", ImageConstants.MAPPEDCLASS, false); //$NON-NLS-1$
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		//setBorder(new LineBorder(FiguresConstants.veryLightGray, 1));		
		setBackgroundColor(classColor);
		setForegroundColor(ColorConstants.black);
		setOpaque(true);

		propertiesFigure = new PropertiesFigure(classColor, ColorConstants.black);
		add(header);
		add(propertiesFigure);
	}

	public PropertiesFigure getPropertiesFigure() {
		return propertiesFigure;
	}

	public void refreshLabel(String headerName) {
		header.setNodeName(headerName);		
	}
	
	protected void paintBorder(Graphics graphics) {
		/*Rectangle bounds = getBounds().getCopy();
		Point origin = bounds.getLocation();
		int height = bounds.height;
		int width = bounds.width;
		graphics.translate(origin);
		graphics.setForegroundColor(ColorConstants.lightGray);
		graphics.drawLine(0, 0, width - 2, 0);
		graphics.drawLine(width - 2, 0, width - 2, height - 2);
		graphics.drawLine(width - 2, height - 2, 0, height - 2);
		graphics.drawLine(0, height - 2, 0, 0);
		graphics.setForegroundColor(FiguresConstants.veryLightGray);
		graphics.drawLine(width - 1, 1, width - 1, height - 1);
		graphics.drawLine(width - 1, height - 1, 1, height - 1);*/
		super.paintBorder(graphics);
	}
	
	public void setBounds(Rectangle rect) {		
		super.setBounds( rect );
	}
}
