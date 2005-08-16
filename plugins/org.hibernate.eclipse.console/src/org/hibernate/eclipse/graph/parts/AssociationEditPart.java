package org.hibernate.eclipse.graph.parts;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.hibernate.eclipse.graph.model.AssociationViewAdapter;

public class AssociationEditPart extends AbstractConnectionEditPart {

	public AssociationEditPart(AssociationViewAdapter adapter) {
		setModel(adapter);
	}

	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	protected IFigure createFigure() {
		PolylineConnection polylineConnection = new PolylineConnection();
		
		AssociationViewAdapter association = (AssociationViewAdapter) getModel();
		polylineConnection.setTargetDecoration(association.getTargetDecoration());
		ConnectionRouter connectionRouter = association.getConnectionRouter();
		if(connectionRouter!=null) {
			polylineConnection.setConnectionRouter(connectionRouter);	
		}
		
		return polylineConnection;
	}
	
}
