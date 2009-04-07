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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

public class PropertiesFigure extends Figure {

	public PropertiesFigure(Color bgColor, Color fgColor) {
		FlowLayout layout = new FlowLayout();
		layout.setMinorAlignment( FlowLayout.ALIGN_LEFTTOP );
		layout.setStretchMinorAxis( true );		
		layout.setHorizontal( false );
		setLayoutManager( layout );
		setBorder( new PropertiesFigureBorder() );
		setBackgroundColor( bgColor );
		setForegroundColor( fgColor );
		setOpaque( true );
	}

	static class PropertiesFigureBorder extends AbstractBorder {

		public Insets getInsets(IFigure figure) {
			return new Insets( 5, 3, 3, 1 );
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.drawLine(
					getPaintRectangle( figure, insets ).getTopLeft(), tempRect
							.getTopRight() );
		}
	}
}
