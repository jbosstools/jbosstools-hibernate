package org.hibernate.eclipse.graph.model;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;

public abstract class AssociationViewAdapter {
	
	public abstract String getSourceName();
	
	public abstract String getTargetName();

	public RotatableDecoration getTargetDecoration() {
		return new PolygonDecoration();
	}

	public ConnectionRouter getConnectionRouter() {
		return null;
	}

	public String toString() {
		return "Association target: " + getSourceName() + " source: " + getTargetName(); 
	}

	public String getAssociationName() {
		return null;
	}
}
