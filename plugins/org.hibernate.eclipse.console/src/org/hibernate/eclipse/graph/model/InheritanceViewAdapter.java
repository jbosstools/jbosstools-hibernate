package org.hibernate.eclipse.graph.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;

public class InheritanceViewAdapter extends AssociationViewAdapter {

	private final PersistentClassViewAdapter subclass;
	private final PersistentClassViewAdapter superclass;

	public InheritanceViewAdapter(PersistentClassViewAdapter subclass, PersistentClassViewAdapter superclass) {
		if(subclass==superclass) throw new IllegalArgumentException("subclass must not be equal to superclass");
		if(subclass==null) throw new IllegalArgumentException("subclass must not null");
		if(superclass==null) throw new IllegalArgumentException("superclass must not null");
		this.subclass = subclass;
		this.superclass = superclass;
	}
	
	public RotatableDecoration getTargetDecoration() {
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0,0);
		decorationPointList.addPoint(-2,2);
		decorationPointList.addPoint(-2,-2);
		decoration.setTemplate(decorationPointList);
		decoration.setFill(true);
		decoration.setBackgroundColor(ColorConstants.white);
		return decoration;
	}

	public ConnectionRouter getConnectionRouter() {
		return new ManhattanConnectionRouter();
	}
	
	public String toString() {
		return "Inheritance " + super.toString(); 
	}

	public String getSourceName() {
		return subclass.getPersistentClass().getEntityName();
	}

	public String getTargetName() {
		return superclass.getPersistentClass().getEntityName();
	}
	
}