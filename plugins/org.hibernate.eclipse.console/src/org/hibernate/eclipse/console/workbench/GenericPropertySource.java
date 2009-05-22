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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

public class GenericPropertySource implements IPropertySource2 {

	private final Object real;
	private IPropertyDescriptor[] descriptors;
	private HashMap<Object, IPropertyDescriptor> map;

	public GenericPropertySource(Object real) {
		this.real = real;
		this.descriptors = buildPropertyDescriptors();
		this.map = new HashMap<Object, IPropertyDescriptor>();
		for (int i = 0; i < descriptors.length; i++) {
			IPropertyDescriptor desc = descriptors[i];
			map.put(desc.getId(), desc);			
		}
	}
	
	public boolean isPropertyResettable(Object id) {
		return false;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public Object getEditableValue() {
		return real;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
			
	}

	private IPropertyDescriptor[] buildPropertyDescriptors() {
		if (real==null) return new IPropertyDescriptor[0];
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo( real.getClass(), Object.class );
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			IPropertyDescriptor[] result = new IPropertyDescriptor[propertyDescriptors.length];
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				result[i]=new BeanPropertyDescriptor(descriptor);
			}
			return result;
		}
		catch (IntrospectionException e) {
			return new IPropertyDescriptor[0];	
		}
	}

	public Object getPropertyValue(Object id) {
		BeanPropertyDescriptor desc = (BeanPropertyDescriptor) map.get(id);
		Object value = desc.getValue(real);
		return value;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}

}
