/*
 * Created on Feb 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.Session;
import org.hibernate.console.HibernateIdentifierProperty;
import org.hibernate.console.PropertyUtil;
import org.hibernate.console.QueryPage;

import com.l2fprod.common.propertysheet.Property;

/**
 * @author Marshall
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HibernatePropertyPage extends PropertySheetPage {

	private static Session session;
	
	public HibernatePropertyPage ()
	{
		super ();
		
		setPropertySourceProvider(new HibernatePropertySourceProvider());
	}
	
	private class HibernatePropertySourceProvider implements IPropertySourceProvider
	{
		public IPropertySource getPropertySource(Object object) {
			if (object instanceof QueryPage)
			{
				if (session == null)
					session = ((QueryPage)object).getSession();
				
				return new QueryPagePropertySource((QueryPage)object);
			}
			else if (object instanceof Property)
			{
				return new HibernatePropertySource(object);
			}
			else return new EntityPropertySource(object);
			
		}
	}
	
	private String getPropertyId (Property property)
	{
		return property.getType().getName() + "_" + property.getName();
	}
	
	private IPropertyDescriptor getPropertyDescriptor (Property property)
	{
		return  getClassDescriptor (getPropertyId(property), property.getName(), property.getType());
	}
	
	private IPropertyDescriptor getClassDescriptor (String propertyId, String propertyName, Class clazz)
	{
		try {
			
			if (clazz.equals(Boolean.class))
			{
				return new ComboBoxPropertyDescriptor(propertyId, propertyName, new String[] { "true", "false" });
			}
			
			return new TextPropertyDescriptor(propertyId, propertyName);
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
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
					descriptors.add( getClassDescriptor(object.hashCode() + "", object.getClass().getSimpleName() + "[" + i + "]", object.getClass()) );
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
			// TODO Auto-generated method stub
			return false;
		}

		public void resetPropertyValue(Object id) {
			// TODO Auto-generated method stub
			
		}

		public void setPropertyValue(Object id, Object value) {
			// TODO Auto-generated method stub
		}
		
		
	}
	
	public class QueryPagePropertySource extends EntityPropertySource
	{	
		public QueryPagePropertySource (QueryPage page)
		{
			super (page.getActive());
		}
	}
	
	public class EntityPropertySource implements IPropertySource2
	{
		private Object reflectedObject;
		private Hashtable descriptors, properties;
		
		public EntityPropertySource (Object object)
		{
			descriptors = new Hashtable();
			properties = new Hashtable();
			reflectedObject = object;
			
			try {
				Property objectProperties[] = PropertyUtil.extractProperties(object, session);
				for (int i = 0; i < objectProperties.length; i++)
				{
					String propertyId = getPropertyId(objectProperties[i]);
					IPropertyDescriptor descriptor = getPropertyDescriptor(objectProperties[i]);
					descriptors.put(propertyId, descriptor);
					properties.put(propertyId, objectProperties[i]);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			Property p = getProperty(id);
			if (p != null)
			{
				return p.getValue() != null;
			}
			return false;
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
	
}
