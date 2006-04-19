package org.hibernate.eclipse.console.model.impl;

import java.util.HashMap;
import java.util.Iterator;

public class ExporterInstance implements Comparable {
	private ExporterDefinition definition;
	private HashMap properties;
	private String id;
	
	public ExporterInstance (ExporterDefinition definition, String id)
	{
		this.definition = definition;
		this.id = id;
		
		// load the default property values (these will/can be overridden by the ILaunchConfiguration properties)
		properties = new HashMap(definition.getProperties());		
	}

	public ExporterDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ExporterDefinition definition) {
		this.definition = definition;
	}

	public HashMap getProperties() {
		return properties;
	}
	
	public void setProperty (ExporterProperty property, String value)
	{
		properties.put(property, value);
	}
	
	public String getProperty (ExporterProperty property)
	{
		if (!properties.containsKey(property)) return null;
		
		return (String) properties.get(property);
	}

	public String getId() {
		return id;
	}
    
    public int compareTo (Object other)
    {
       if (other instanceof ExporterInstance)
       {
          ExporterInstance otherExporter = (ExporterInstance) other;
          
          return getId().compareTo(otherExporter.getId());
       }
       else return -1;
    }
    
    public ExporterProperty findOrCreateProperty (String propertyName)
    {
       for (Iterator propertyIter = definition.getProperties().keySet().iterator(); propertyIter.hasNext(); )
        {
           ExporterProperty property = (ExporterProperty) propertyIter.next();
           
           if (property.getName().equals(propertyName))  
           {
               return property;
           }
        }

        // if we've reached this point an exporter property wasn't found, should just create a wrapper.
        // (this allows us to specify non-standard properties)
        return new ExporterProperty(propertyName, propertyName, "", false);
    }
}
