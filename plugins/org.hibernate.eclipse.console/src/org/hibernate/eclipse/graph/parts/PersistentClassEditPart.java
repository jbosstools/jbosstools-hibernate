/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.hibernate.eclipse.console.workbench.HibernateWorkbenchHelper;
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

	@SuppressWarnings("unchecked")
	protected List<PropertyViewAdapter> getModelChildren() {	
		
		List<PropertyViewAdapter> list = new ArrayList<PropertyViewAdapter>();
		
		Property identifierProperty = getPersistentClass().getIdentifierProperty();
		if(identifierProperty!=null) {
			list.add( new PropertyViewAdapter(getPersistentClassViewAdapter(), identifierProperty ));
		}
		
		Iterator<Property> propertyIterator = getPersistentClass().getPropertyIterator();
		
		while ( propertyIterator.hasNext() ) {
			list.add( new PropertyViewAdapter(getPersistentClassViewAdapter(), propertyIterator.next()) );
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