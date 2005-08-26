/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import java.util.Collection;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;



public class EntityPropertySource implements IPropertySource2
{
	private Object reflectedObject;
	private IPropertyDescriptor[] propertyDescriptors;
	
	private final ExecutionContextHolder currentConfiguration;
	private final Session currentSession;
	private ClassMetadata classMetadata;
	
	public EntityPropertySource (final Object object, final Session currentSession, ExecutionContextHolder currentConfiguration)
	{
		this.currentSession = currentSession;
		this.currentConfiguration = currentConfiguration;
		reflectedObject = object;
		classMetadata = currentSession.getSessionFactory().getClassMetadata( currentSession.getEntityName(reflectedObject) );
							
	}
	
	
	public Object getEditableValue() {
		return "";
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(propertyDescriptors==null) {
			currentConfiguration.getExecutionContext().execute(new Command() {
			
				public Object execute() {
					
					propertyDescriptors = initializePropertyDescriptors(classMetadata);
					return null;
				}
			
			});
		}
		return propertyDescriptors;
	}
		
	static protected IPropertyDescriptor[] initializePropertyDescriptors(ClassMetadata classMetadata) {
		
		String[] propertyNames = classMetadata.getPropertyNames();
		int length = propertyNames.length;
		
		PropertyDescriptor identifier = null;
		
		if(classMetadata.hasIdentifierProperty() ) {			
			identifier = new PropertyDescriptor(classMetadata.getIdentifierPropertyName(), classMetadata.getIdentifierPropertyName());
			identifier.setCategory("Identifier");
			length++;
		}
		
		PropertyDescriptor[] properties = new PropertyDescriptor[length];
		
		int idx = 0;
		if(identifier!=null) {
			properties[idx++] = identifier; 
		}
		
		for (int i = 0; i < propertyNames.length; i++) {
			 PropertyDescriptor prop = new PropertyDescriptor(propertyNames[i],propertyNames[i]);
			 prop.setCategory("Properties");
			 properties[i+idx] = prop;			
		}
		
		return properties;
	}


	public Object getPropertyValue(Object id) {
		Object propertyValue;
		
		if(id.equals(classMetadata.getIdentifierPropertyName())) {
			propertyValue = classMetadata.getIdentifier(reflectedObject, EntityMode.POJO);			
		} else {
			propertyValue = classMetadata.getPropertyValue(reflectedObject, (String)id, EntityMode.POJO);	
		} 
		
		if (propertyValue instanceof Collection) {
			CollectionMetadata collectionMetadata = currentSession.getSessionFactory().getCollectionMetadata(classMetadata.getEntityName() + "." + id);
			if(collectionMetadata!=null) {
				propertyValue = new CollectionPropertySource((Collection) propertyValue,currentSession,currentConfiguration, collectionMetadata);
			}
		}
		return propertyValue;
	}
	
	public boolean isPropertySet(Object id) {		
		return false; // we can not decide this at the given point.
	}
	
	public void resetPropertyValue(Object id) {
		
	}
	
	public void setPropertyValue(Object id, Object value) {
		// lets not support editing in the raw properties view - to flakey ui.
		//classMetadata.setPropertyValue(reflectedObject, (String) id, value, EntityMode.POJO);
	}
	
	public boolean isPropertyResettable(Object id) {
		return false;
	}
	
}