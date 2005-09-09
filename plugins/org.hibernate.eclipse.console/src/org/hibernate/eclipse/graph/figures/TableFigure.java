package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class TableFigure extends Figure {

	public static Color tableColor = new Color(null, 0xff, 0xff, 0xaa);
	static Font tableHeaderFont = new Font(null, "Arial", 12, SWT.BOLD);
	
	private Label label;

	private PropertiesFigure propertiesFigure;

	public TableFigure(Label name) {
		label = name;		
		label.setFont(tableHeaderFont);
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(tableColor);
		setForegroundColor(ColorConstants.black);
		setOpaque(true);

		name.setForegroundColor(ColorConstants.black);
		
		propertiesFigure = new PropertiesFigure(tableColor, ColorConstants.black);
		add(name);
		add(propertiesFigure);
	}

	public Label getLabel() {
		return label;
	}

	public PropertiesFigure getColumnsFigure() {
		return propertiesFigure;
	}
	

}
