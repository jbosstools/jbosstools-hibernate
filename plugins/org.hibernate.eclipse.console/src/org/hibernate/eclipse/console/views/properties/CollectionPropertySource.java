package org.hibernate.eclipse.console.views.properties;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.hibernate.Session;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.metadata.CollectionMetadata;

public class CollectionPropertySource implements IPropertySource {

	private Collection collection;

	IPropertyDescriptor[] descriptors = null;

	private final Session currentSession;

	private final CollectionMetadata collectionMetadata;

	private final ExecutionContextHolder currentConfiguration;
	
	Map values = new WeakHashMap();
	
	public CollectionPropertySource(Collection propertyValue, Session currentSession, ExecutionContextHolder currentConfiguration, CollectionMetadata collectionMetadata) {
		collection = propertyValue;
		this.currentSession = currentSession;
		this.currentConfiguration = currentConfiguration;
		this.collectionMetadata = collectionMetadata;		
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors==null) {
			PropertyDescriptor[] properties = new PropertyDescriptor[collection.size()];
			for (int i = 0; i < properties.length; i++) {
				properties[i] = new PropertyDescriptor(new Integer(i),"#" + i);				
			}	
			descriptors = properties;
		}
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		Object value = values.get(id);
		if(value==null) {
			Integer i = (Integer) id;
			Iterator iterator = collection.iterator();
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
