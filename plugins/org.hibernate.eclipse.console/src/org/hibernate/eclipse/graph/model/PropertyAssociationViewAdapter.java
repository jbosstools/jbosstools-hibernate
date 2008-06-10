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
package org.hibernate.eclipse.graph.model;

import org.hibernate.util.StringHelper;

public class PropertyAssociationViewAdapter extends AssociationViewAdapter {

	private final PropertyViewAdapter property;
	private final PersistentClassViewAdapter clazz;
	private final PersistentClassViewAdapter target;

	public PropertyAssociationViewAdapter(PersistentClassViewAdapter clazz, PropertyViewAdapter property, PersistentClassViewAdapter target) {
		this.clazz = clazz;
		this.property = property;
		this.target = target;		
	}

	public String getSourceName() {
		return StringHelper.qualify(clazz.getPersistentClass().getEntityName(), property.getProperty().getName());
	}

	public String getTargetName() {
		return target.getPersistentClass().getEntityName();
	}
	
	public String toString() {
		return "Property " + property.getProperty().getName() + " " + super.toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getAssociationName() {
		return property.getProperty().getName();
	}
}
