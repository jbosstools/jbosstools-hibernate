/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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