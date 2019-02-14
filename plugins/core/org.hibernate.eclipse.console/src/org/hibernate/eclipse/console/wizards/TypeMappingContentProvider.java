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
package org.hibernate.eclipse.console.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class TypeMappingContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	private final Viewer tv;

	public TypeMappingContentProvider(Viewer tv) {
		this.tv = tv;			
	}
	
	public Object[] getElements(Object inputElement) {
		return (getReverseEngineeringDef( inputElement )).getTypeMappings();
	}

	private IReverseEngineeringDefinition getReverseEngineeringDef(Object inputElement) {
		return (IReverseEngineeringDefinition)inputElement;
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null) {
			(getReverseEngineeringDef(oldInput)).removePropertyChangeListener(this);
		}
		if (newInput != null) {
			(getReverseEngineeringDef(newInput)).addPropertyChangeListener(this);
		}		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName()==null || evt.getPropertyName().equals(IReverseEngineeringDefinition.TYPEMAPPING_STRUCTURE)) {
			tv.refresh();	
		}							
	}
}