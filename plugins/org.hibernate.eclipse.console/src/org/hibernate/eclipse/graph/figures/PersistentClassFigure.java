package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.SimpleLoweredBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class PersistentClassFigure extends Figure {

	//public static Color classColor = new Color(null, 188, 174, 121); // hibernate brown
	//public static Color classColor = new Color(null, 179, 202, 227); // opera tab blue
	public static Color classColor = new Color(null, 212, 229, 254); // skype says	
	static Font classHeaderFont = new Font(null, "Arial", 12, SWT.BOLD);
	
	
	private PropertiesFigure propertiesFigure;
	private NodeHeaderFigure header;

	public PersistentClassFigure(String name) {
		header = new NodeHeaderFigure(name, "Class", ImageConstants.MAPPEDCLASS, false);
		
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
	
}
