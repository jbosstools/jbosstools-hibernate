/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import java.util.Hashtable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.hibernate.Session;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.PropertyUtil;

import com.l2fprod.common.propertysheet.Property;

public class EntityPropertySource implements IPropertySource2
{
	private Object reflectedObject;
	private Hashtable descriptors, properties;
	private final ConsoleConfiguration currentConfiguration;
	private final Session currentSession;
	
	public EntityPropertySource (final Object object, final Session currentSession, ConsoleConfiguration currentConfiguration)
	{
		this.currentSession = currentSession;
		this.currentConfiguration = currentConfiguration;
		descriptors = new Hashtable();
		properties = new Hashtable();
		reflectedObject = object;
		
		currentConfiguration.execute(new ConsoleConfiguration.Command() {

			public Object execute() {
				try {
					Property objectProperties[] = PropertyUtil.extractProperties(object, currentSession);
					for (int i = 0; i < objectProperties.length; i++)
					{
						String propertyId = HibernatePropertyPage.getPropertyId(objectProperties[i]);
						IPropertyDescriptor descriptor = HibernatePropertyPage.getPropertyDescriptor(objectProperties[i]);
						descriptors.put(propertyId, descriptor);
						properties.put(propertyId, objectProperties[i]);
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;	
			}		
		});
				
	}
	
	
	public Object getEditableValue() {
		return "";
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[]) descriptors.values().toArray(new IPropertyDescriptor[descriptors.values().size()]);
	}
	
	private Property getProperty (Object id)
	{
		return (Property) properties.get(id);
	}
	
	public Object getPropertyValue(Object id) {
		Property property = getProperty(id);
		if (property != null)
		{
			try {
				property.readFromObject(reflectedObject);					
				return property;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}
	
	public boolean isPropertySet(Object id) {		
		return false; // we can not decide this at the given point.
	}
	
	public void resetPropertyValue(Object id) {
		
	}
	
	public void setPropertyValue(Object id, Object value) {
		Property property = getProperty(id);
		if (property != null)
		{
			try {
				property.setValue(value);					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isPropertyResettable(Object id) {
		return false;
	}
	
}