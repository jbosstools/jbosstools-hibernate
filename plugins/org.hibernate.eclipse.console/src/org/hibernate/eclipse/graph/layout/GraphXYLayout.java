package org.hibernate.eclipse.graph.layout;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

/**
 * Subclass of XYLayout which can use the child figures actual bounds as a
 * constraint when doing manual layout (XYLayout)
 * 
 * @author Phil Zoio
 */
public class GraphXYLayout extends FreeformLayout {

	private ConfigurationEditPart diagram;

	public GraphXYLayout(ConfigurationEditPart diagram) {
		this.diagram = diagram;
	}

	public void layout(IFigure container) {		
		diagram.resetModelBounds(this);
		super.layout( container );				
	}

	public Object getConstraint(IFigure child) {
		Object constraint = constraints.get( child );
		if ( constraint != null || constraint instanceof Rectangle ) {
			return (Rectangle) constraint;
		}
		else {
			Rectangle currentBounds = child.getBounds();
			return new Rectangle( currentBounds.x, currentBounds.y, -1, -1 );
		}
	}

	public void setConstraint(IFigure figure, Object newConstraint) {
		if(newConstraint!=null && newConstraint instanceof Rectangle) {
			figure.setBounds((Rectangle) newConstraint);
		}
		super.setConstraint( figure, newConstraint );
	}
}
