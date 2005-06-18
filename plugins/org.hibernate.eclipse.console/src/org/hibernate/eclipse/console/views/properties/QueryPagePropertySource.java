/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.QueryPage;

public class QueryPagePropertySource implements IPropertySource2
{	
	private final QueryPage page;

	static IPropertyDescriptor[] descriptors;

	private static final String QUERY_TEXT = "QueryPage.queryString";

	private static final Object CONFIGURATION_TEXT = "QueryPage.consoleConfiguration";
	
	static {
		descriptors = new IPropertyDescriptor[2];
        PropertyDescriptor descriptor;

        // query string
        descriptor = new TextPropertyDescriptor(QUERY_TEXT,
                "Query string");
        descriptor.setAlwaysIncompatible(true);
        //descriptor.setCategory(IResourcePropertyConstants.P_FILE_SYSTEM_CATEGORY);
        descriptors[0] = descriptor;
        
        // configuration name
        descriptor = new TextPropertyDescriptor(CONFIGURATION_TEXT,
                "Console configuration");
        descriptor.setAlwaysIncompatible(true);
        //descriptor.setCategory(IResourcePropertyConstants.P_FILE_SYSTEM_CATEGORY);
        descriptors[1] = descriptor;
    }
	
	public QueryPagePropertySource (QueryPage page) {
		this.page = page;			
	}

	public boolean isPropertyResettable(Object id) {		
		return false;
	}

	public Object getEditableValue() {
		return "";
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if(CONFIGURATION_TEXT.equals(id) ) {
			return page.getConsoleConfiguration().getName();
		}
		if(QUERY_TEXT.equals(id) ) {
			return page.getQueryString();
		}
		
		return null;		
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {			
		
	}

	public void setPropertyValue(Object id, Object value) {
		
		
	}
}