package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

public class PersistentClassFigure extends Figure {

	public static Color classColor = new Color(null, 255, 255, 206);

	private Label label;

	private PropertiesFigure propertiesFigure;

	public PersistentClassFigure(Label name) {
		label = name;
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setForegroundColor(ColorConstants.black);
		setOpaque(true);

		name.setForegroundColor(ColorConstants.black);
		
		propertiesFigure = new PropertiesFigure();
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
