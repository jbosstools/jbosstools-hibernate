package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class PersistentClassFigure extends Figure {

	public static Color classColor = new Color(null, 188, 174, 121);
	static Font classHeaderFont = new Font(null, "Arial", 12, SWT.BOLD);
	
	private Label label;

	private PropertiesFigure propertiesFigure;

	public PersistentClassFigure(Label name) {
		label = name;		
		label.setFont(classHeaderFont);
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setForegroundColor(ColorConstants.black);
		setOpaque(true);

		name.setForegroundColor(ColorConstants.black);
		
		propertiesFigure = new PropertiesFigure(classColor, ColorConstants.black);
		add(name);
		add(propertiesFigure);
	}

	public Label getLabel() {
		return label;
	}

	public PropertiesFigure getPropertiesFigure() {
		return propertiesFigure;
	}
	
}
