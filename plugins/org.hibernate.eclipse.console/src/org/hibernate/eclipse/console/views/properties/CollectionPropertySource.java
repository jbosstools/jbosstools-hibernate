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
package org.hibernate.eclipse.console.views.properties;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class CollectionPropertySource implements IPropertySource {

	private Collection<?> collection;

	IPropertyDescriptor[] descriptors = null;
	
	Map<Object, Object> values = new WeakHashMap<Object, Object>();
	
	public CollectionPropertySource(Collection<?> propertyValue) {
		collection = propertyValue;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors==null) {
			PropertyDescriptor[] properties = new PropertyDescriptor[collection.size()];
			for (int i = 0; i < properties.length; i++) {
				properties[i] = new PropertyDescriptor(Integer.valueOf(i),"#" + i);				 //$NON-NLS-1$
			}	
			descriptors = properties;
		}
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		Object value = values.get(id);
		if(value==null) {
			Integer i = (Integer) id;
			Iterator<?> iterator = collection.iterator();
			int base = 0;
			
			while(iterator.hasNext()) {
				
				value = iterator.next();
				
				if(base==i.intValue()) {
					values.put(id, value);
					return value;
				} else {
					value=null;
				}
				base++;
			}
		}
		
		return value;
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		

	}

}
