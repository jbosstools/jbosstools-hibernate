/*
 * Created on Feb 21, 2005
 * 
 */
package org.hibernate.eclipse.console.views.properties;


import java.util.Collection;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.l2fprod.common.propertysheet.Property;


/**
 * @author Marshall
 *
 * TODO: remove this class since it just contains helper stuff. 
 */
public class HibernatePropertyHelper {
	
	static String getPropertyId (Property property)
	{
		return property.getType().getName() + "_" + property.getName();
	}
	
	static IPropertyDescriptor getPropertyDescriptor (Property property)
	{
		return  getClassDescriptor (getPropertyId(property), property.getName(), property.getType() );
	}
	
	static IPropertyDescriptor getClassDescriptor (String propertyId, String propertyName, Class clazz)
	{
		try {
			
			if (clazz.equals(Boolean.class) )
			{
				return new ComboBoxPropertyDescriptor(propertyId, propertyName, new String[] { "true", "false" });
			}
			
			if(clazz.isAssignableFrom(Collection.class) ) {
				return new org.eclipse.ui.views.properties.PropertyDescriptor(propertyId, propertyName);
			}
			return new TextPropertyDescriptor(propertyId, propertyName);
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
}
