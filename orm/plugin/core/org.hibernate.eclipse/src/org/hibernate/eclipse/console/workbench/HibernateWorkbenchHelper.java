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
package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class HibernateWorkbenchHelper {

	public static ImageDescriptor getImageDescriptor(IProperty property) {
		if(property==null) return null;
		if(property.getPersistentClass()!=null) {
			if(property.getPersistentClass().getIdentifierProperty()==property) {
				return EclipseImages.getImageDescriptor(ImageConstants.IDPROPERTY);
			}
		}
		String iconNameForValue = getIconNameForValue(property.getValue());
		
		return EclipseImages.getImageDescriptor(iconNameForValue);
	}
	
	public static Image getImage(IProperty property) {
		if(property==null) return null;
		if(property.getPersistentClass()!=null) {
			if(property.getPersistentClass().getIdentifierProperty()==property) {
				return EclipseImages.getImage(ImageConstants.IDPROPERTY);
			}
		}
		String iconNameForValue = getIconNameForValue(property.getValue());
		
		return EclipseImages.getImage(iconNameForValue);
	}
	
	public static String getLabelForClassName(String classOrEntityName) {
		if(classOrEntityName.indexOf('.')>=0) {
			classOrEntityName = StringHelper.unqualify(classOrEntityName);
		}
		return classOrEntityName;
	}


	private static String getIconNameForValue(IValue value) {
		if (value.isOneToOne()) {
			return ImageConstants.ONETOONE;
		} else if (value.isManyToOne()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isComponent()) {
			return ImageConstants.COMPONENT;
		} else if (value.isDependantValue()) {
			return ImageConstants.UNKNOWNPROPERTY;
		} else if (value.isAny()) {
			return ImageConstants.PROPERTY;
		} else if (value.isSimpleValue()) {
			return ImageConstants.PROPERTY;
		} else if (value.isSet()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isOneToMany()) {
			return ImageConstants.ONETOMANY;
		} else if (value.isMap()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isPrimitiveArray()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isArray()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isList()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isIdentifierBag()) {
			return ImageConstants.MANYTOONE;
		} else if (value.isBag()) {
			return ImageConstants.MANYTOONE;
		}
		return ImageConstants.UNKNOWNPROPERTY;
	}
	
}
