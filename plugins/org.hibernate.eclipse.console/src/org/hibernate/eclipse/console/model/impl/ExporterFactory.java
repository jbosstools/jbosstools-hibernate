package org.hibernate.eclipse.console.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;


public class ExporterFactory {

	private ExporterDefinition definition;

	final Map inputProperties;

	private boolean enabled = true;
	
	public ExporterFactory(ExporterDefinition definition) {
		this.definition = definition;
		inputProperties = new HashMap();
	}

	public Map getDefaultExporterProperties() {
		return definition.getProperties();
	}
	

	public String setProperty(String key, String value) {
		return (String) inputProperties.put( key, value );		
	}

	public void removeProperty(String propertyName) {
		inputProperties.remove( propertyName );
	}

	public String getPropertyValue(String key) {
		if(inputProperties.containsKey( key )) {
			return (String) inputProperties.get( key );
		} else {
			ExporterProperty ep = (ExporterProperty) definition.getProperties().get( key );
			if(ep!=null) {
				return ep.getDefaultValue();
			} else {
				return null;
			}
		} 
	}

	public boolean isEnabled() {
		return enabled ;
	}

	public void setEnabled(boolean b) {
		enabled = b;		
	}

	public ExporterDefinition getExporterDefinition() {
		return definition;
	}

	public boolean isEnabled(ILaunchConfiguration configuration) {
		boolean enabled = false;

		try {
			// if we put this in some "namespace" we should have a way to either
			// migrate an existing one...
			enabled = configuration.getAttribute( definition.getId(), false );
		}
		catch (CoreException e) {
			e.printStackTrace(); // TODO-marshall: bad!
		}

		setEnabled( enabled );
		return isEnabled();
	}

	public void setEnabled(ILaunchConfigurationWorkingCopy configuration, boolean enabled) {
		setEnabled( enabled );
		configuration.setAttribute( definition.getId(), isEnabled() );		
	}

	public Map getProperties() {
		return inputProperties;
	}

	public String getId() {
		return getExporterDefinition().getId(); // TODO: namespacing
	}

	public void setProperties(Map props) {
		inputProperties.clear();
		inputProperties.putAll( props );				
	}

	public ExporterProperty getExporterProperty(String key) {
		return (ExporterProperty) definition.getProperties().get( key );
	}

	public boolean hasLocalValueFor(String string) {
		return inputProperties.containsKey( string );
	}

}
