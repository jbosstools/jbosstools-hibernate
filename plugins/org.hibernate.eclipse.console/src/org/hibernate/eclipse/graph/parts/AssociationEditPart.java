package org.hibernate.eclipse.graph.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
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
		
		AssociationViewAdapter ava = (AssociationViewAdapter) getModel();
		if(ava.getAssociationName()!=null) {
			MidpointLocator mpl = new MidpointLocator(polylineConnection, 0);
			polylineConnection.add(new Label(ava.getAssociationName()), mpl);	
		}
		
		polylineConnection.setForegroundColor(ColorConstants.gray);
		
		return polylineConnection;
	}
	
}
