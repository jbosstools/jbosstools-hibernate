package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

/**
 * Draws a Rectangle border whose corners are rounded in appearance. The size of the rectangle is 
 * determined by the bounds set to it.
 * 
 */
public class RoundedLineBorder extends LineBorder {

	/** The width and height radii applied to each corner. */
	protected Dimension corner = new Dimension(8, 8);

	public RoundedLineBorder() { }

	public RoundedLineBorder(Dimension corner) {
		this.corner = corner;
	}

	public RoundedLineBorder(Color color, int width) {
		super( color, width );
	}

	public RoundedLineBorder(Color color) {
		super( color );
	}

	public RoundedLineBorder(int width) {
		super( width );
	}
	
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		tempRect.setBounds(getPaintRectangle(figure, insets));
		if (getWidth() % 2 == 1) {
			tempRect.width--;
			tempRect.height--;
		}
		tempRect.shrink(getWidth() / 2, getWidth() / 2);
		graphics.setLineWidth(getWidth());
		if (getColor() != null) {
			graphics.setForegroundColor(getColor());
		}
		// the only difference between this and a lineborder is the call to drawRoundRectangle
		graphics.drawRoundRectangle(tempRect, corner.width, corner.height);
	}

	/**
	 * Sets the dimensions of each corner. This will form the radii of the arcs
	 * which form the corners.
	 *
	 * @param d the dimensions of the corner
	 */
	public void setCornerDimensions(Dimension d) {
		corner.width = d.width;
		corner.height = d.height;
	}
	
}