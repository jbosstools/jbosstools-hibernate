/*
 * Created on 22-Mar-2005
 *
 */
package org.hibernate.eclipse.console.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.hibernate.console.QueryPage;

public class QueryPagePropertySource implements IPropertySource2
{	
	private final QueryPage page;

	IPropertyDescriptor[] descriptors;
	public QueryPagePropertySource (QueryPage page) {
		this.page = page;			
	}

	public boolean isPropertyResettable(Object id) {
		
		return false;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors==null) {
			descriptors = new PropertyDescriptor[2];
			PropertyDescriptor pd = new PropertyDescriptor(new Long(0), "Query string");
			pd.setDescription("The query string used on this querypage.");
			descriptors[0] = pd;
			pd = new PropertyDescriptor(new Long(1), "Configuration name");
			descriptors[1] = pd;
		}
		
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		Long l = (Long) id;
		
		switch(l.intValue()) {
			case 0: return page.getQueryString();
			case 1: return page.getConsoleConfiguration().getName();
		}
		
		return null;		}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {			
		
	}

	public void setPropertyValue(Object id, Object value) {
		
		
	}
}