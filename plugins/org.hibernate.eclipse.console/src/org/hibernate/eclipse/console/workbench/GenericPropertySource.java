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
	private HashMap map;

	public GenericPropertySource(Object real) {
		this.real = real;
		this.descriptors = buildPropertyDescriptors();
		this.map = new HashMap();
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
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
			
	}

	private IPropertyDescriptor[] buildPropertyDescriptors() {
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
