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
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.jboss.tools.hibernate.proxy.PropertyProxy;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.spi.IValueVisitor;

public class PropertyWorkbenchAdapter implements IWorkbenchAdapter {
	
	final static Object[] NO_CHILDREN = new Object[0];

	public Object[] getChildren(Object o) {
		IProperty p = (IProperty) o;
		
		Object[] result = (Object[]) p.getValue().accept(new IValueVisitor() {
			
			@Override
			public Object accept(IValue value) {
				if (value.isOneToOne()) {
					return NO_CHILDREN;
				} else if (value.isManyToOne()) {
					return NO_CHILDREN;
				} else if (value.isComponent()) {
					return BasicWorkbenchAdapter.toArray(value.getPropertyIterator(), IProperty.class, null);
				} else if (value.isDependantValue()) {
					return NO_CHILDREN;
				} else if (value.isAny()) {
					return NO_CHILDREN;
				} else if (value.isSimpleValue()) {
					return NO_CHILDREN;
				} else if (value.isSet()) {
					return NO_CHILDREN;
				} else if (value.isOneToMany()) {
					return NO_CHILDREN;
				} else if (value.isMap()) {
					return NO_CHILDREN;
				} else if (value.isPrimitiveArray()) {
					return NO_CHILDREN;
				} else if (value.isArray()) {
					return NO_CHILDREN;
				} else if (value.isList()) {
					return NO_CHILDREN;
				} else if (value.isIdentifierBag()) {
					return NO_CHILDREN;
				} else if (value.isBag()) {
					return NO_CHILDREN;
				}
				return null;
			}
		
		});
		
		return result;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		IProperty property = ((IProperty)object);
		
		return HibernateWorkbenchHelper.getImageDescriptor(((PropertyProxy)property).getTarget());		 
	}

	public String getLabel(Object o) {
		IProperty property = ((IProperty)o);
		IValue value = property.getValue();
		String typeName = (String) value.accept(new TypeNameValueVisitor(true));
		
		if (typeName!=null) {
			return property.getName() + " : " + typeName; //$NON-NLS-1$
		}
		
		return property.getName(); 
	}

	public Object getParent(Object o) {
		IProperty p = (IProperty) o;
		return p.getPersistentClass();
	}

	
}
