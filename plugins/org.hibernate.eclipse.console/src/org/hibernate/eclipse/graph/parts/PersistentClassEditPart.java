package org.hibernate.eclipse.graph.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.hibernate.eclipse.console.workbench.HibernateWorkbenchHelper;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.figures.PersistentClassFigure;
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.eclipse.graph.policy.PersistentClassLayoutPolicy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class PersistentClassEditPart extends GraphNodeEditPart  {

	public PersistentClassEditPart(PersistentClassViewAdapter model) {		
		setModel( model );				
	}
	
	protected void createEditPolicies() {
		installEditPolicy( EditPolicy.LAYOUT_ROLE,	new PersistentClassLayoutPolicy() );
	}

	public void refreshVisuals() {
		PersistentClassFigure myFigure = (PersistentClassFigure) getFigure();
		
		myFigure.refreshLabel( getHeaderName() );
		
		super.refreshVisuals();		
	}

	protected IFigure createFigure() {
		
		String unqualify = getHeaderName();
		return new PersistentClassFigure(unqualify);
	}

	private String getHeaderName() {
		return HibernateWorkbenchHelper.getLabelForClassName(getPersistentClass().getEntityName());
	}

	protected List getModelChildren() {	
		
		List list = new ArrayList();
		
		Property identifierProperty = getPersistentClass().getIdentifierProperty();
		if(identifierProperty!=null) {
			list.add( new PropertyViewAdapter(getPersistentClassViewAdapter(), identifierProperty ));
		}
		
		Iterator propertyIterator = getPersistentClass().getPropertyIterator();
		
		while ( propertyIterator.hasNext() ) {
			list.add( new PropertyViewAdapter(getPersistentClassViewAdapter(), (Property) propertyIterator.next()) );
		}
		return list;
	}

	private PersistentClass getPersistentClass() {
		return getPersistentClassViewAdapter().getPersistentClass();
	}

	public PersistentClassViewAdapter getPersistentClassViewAdapter() {
		return (PersistentClassViewAdapter)getModel();
	}

	public IFigure getContentPane() {
		PersistentClassFigure figure = (PersistentClassFigure) getFigure();
		return figure.getPropertiesFigure();
	}


}