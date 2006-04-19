/**
 * 
 */
package org.hibernate.eclipse.console.model.impl;

public class ExporterProperty
{
	private static ExporterProperty[] globalProperties = new ExporterProperty[] {
		new ExporterProperty ("jdk5", "Use Java 5 syntax", "false", false),
		new ExporterProperty ("ejb3", "Generate EJB3 annotations", "false", false)
	};
	
	private String defaultValue;
	private String description;
	private String name;
	private boolean required;
	
	public ExporterProperty () { }
	public ExporterProperty (String name, String description, String defaultValue, boolean required)
	{
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.required = required;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static ExporterProperty[] getGlobalProperties ()
	{
		return globalProperties;
	}
	
	public boolean equals(Object object) {
		if (object instanceof ExporterProperty)
		{
			ExporterProperty property = (ExporterProperty) object;
			return property.getName().equals(getName());
		}
		return false;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
}