/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.l2fprod.common.propertysheet.Property;

public class HibernatePropertySource implements IPropertySource
{	
	private Property property;
	
	public HibernatePropertySource (Object property)
	{
		this.property = (Property) property;
	}

	public Object getEditableValue() {			
		if (property.getType().equals(Set.class) || property.getType().equals(List.class))
		{
			return "";
		}
		
		if (property.getValue() == null) return "";
		
		return property.getValue().toString();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList descriptors = new ArrayList();
		
		if (property.getType().equals(Set.class) || property.getType().equals(List.class))
		{
			Collection collection = (Collection) property.getValue();
			int i = 0;
			
			for (Iterator iter = collection.iterator(); iter.hasNext(); i++)	
			{
				Object object = iter.next();
				descriptors.add( HibernatePropertyPage.getClassDescriptor(object.hashCode() + "", object.getClass().getName() + "[" + i + "]", object.getClass()) );
			}
		}
		
		return (IPropertyDescriptor[]) descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
	}
	
	public Object getPropertyValue(Object id) {
		if (property.getType().equals(Set.class) || property.getType().equals(List.class))
		{
			Collection collection = (Collection) property.getValue();
			
			for (Iterator iter = collection.iterator(); iter.hasNext();)	
			{
				Object object = iter.next();
				if (id.equals(object.hashCode() + ""))
				{
					return object;
				}
			}
		}
		
		return "";
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		
	}

	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub
	}
	
	
}