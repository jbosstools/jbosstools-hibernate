package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.wizards.EclipseConsoleConfigurationPreferences;

public class ConsoleConfigurationPropertySource implements IPropertySource {

	private final ConsoleConfiguration cfg;

	
	static IPropertyDescriptor[] pd;
	static {
		List l = new ArrayList();
		l.add(new TextPropertyDescriptor("name", "Name"));
		l.add(new PropertyDescriptor("hibernate.cfg.xml", "Configuration file"));
		l.add(new PropertyDescriptor("hibernate.properties", "Properties file"));
		l.add(new PropertyDescriptor("mapping.files", "Additonal mapping files"));
		
		
		pd = (IPropertyDescriptor[]) l.toArray( new IPropertyDescriptor[l.size()] );
	}
	
	public ConsoleConfigurationPropertySource(ConsoleConfiguration cfg) {
		this.cfg = cfg;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pd;
	}

	public Object getPropertyValue(Object id) {
		if("name".equals(id)) {
			return cfg.getName();
		}
		// TODO: not nice that we need to downcast to get more friendly file names
		EclipseConsoleConfigurationPreferences preferences = (EclipseConsoleConfigurationPreferences) cfg.getPreferences();
		
		if("hibernate.cfg.xml".equals(id)) {
			return preferences.getCfgFile();
		}
		if("hibernate.properties".equals(id)) {
			return preferences.getPropertyFilename();
		}
		if("mapping.files".equals(id)) {
			return new Integer(preferences.getMappings().length);
		}
		
		return null;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		}

}
