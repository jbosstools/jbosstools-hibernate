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

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class QueryPagePropertySource implements IPropertySource2
{
	private final QueryPage page;

	static IPropertyDescriptor[] descriptors;

	private static final String QUERY_TEXT = "QueryPage.queryString"; //$NON-NLS-1$

	private static final Object CONFIGURATION_TEXT = "QueryPage.consoleConfiguration"; //$NON-NLS-1$

	private static final String QUERY_SIZE = "QueryPage.size"; //$NON-NLS-1$

	private static final String QUERY_TIME = "QueryPage.time"; //$NON-NLS-1$

	private static final String TAB_NAME = "QueryPage.tabName"; //$NON-NLS-1$

	static {
		descriptors = new IPropertyDescriptor[5];
        PropertyDescriptor descriptor;

        // query string
        descriptor = new TextPropertyDescriptor(QUERY_TEXT,
                HibernateConsoleMessages.QueryPagePropertySource_query_string);
        descriptor.setAlwaysIncompatible(false);
        //descriptor.setCategory(IResourcePropertyConstants.P_FILE_SYSTEM_CATEGORY);
        descriptors[0] = descriptor;

        // configuration name
        descriptor = new TextPropertyDescriptor(CONFIGURATION_TEXT,
                HibernateConsoleMessages.QueryPagePropertySource_console_configuration);
        descriptor.setAlwaysIncompatible(false);
        //descriptor.setCategory(IResourcePropertyConstants.P_FILE_SYSTEM_CATEGORY);
        descriptors[1] = descriptor;

        // number of items
        descriptor = new TextPropertyDescriptor(QUERY_SIZE,
                HibernateConsoleMessages.QueryPagePropertySource_query_size);
        descriptor.setAlwaysIncompatible(false);
        //descriptor.setCategory(IResourcePropertyConstants.P_FILE_SYSTEM_CATEGORY);
        descriptors[2] = descriptor;

        // time of query running
        descriptor = new TextPropertyDescriptor(QUERY_TIME,
                HibernateConsoleMessages.QueryPagePropertySource_query_run_time);
        descriptor.setAlwaysIncompatible(false);
        descriptors[3] = descriptor;

        // time of query running
        descriptor = new TextPropertyDescriptor(TAB_NAME,
        		HibernateConsoleMessages.QueryPagePropertySource_tab_name);
        descriptor.setAlwaysIncompatible(false);
        descriptors[4] = descriptor;
    }

	public QueryPagePropertySource (QueryPage page) {
		this.page = page;
	}

	public boolean isPropertyResettable(Object id) {
		return false;
	}

	public Object getEditableValue() {
		return ""; //$NON-NLS-1$
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
		if(QUERY_SIZE.equals(id) ) {
			int resultSize = page.getResultSize();
			return resultSize==-1?HibernateConsoleMessages.QueryPagePropertySource_unknown:String.valueOf(resultSize);
		}
		if(QUERY_TIME.equals(id) ) {
			long resultTime = page.getQueryTime();
			if (resultTime==-1) return HibernateConsoleMessages.QueryPagePropertySource_unknown;
			if (resultTime > 1000) { 
				return NLS.bind(
						HibernateConsoleMessages.QueryPagePropertySource_sec,
						(resultTime / 1000) + "." + (resultTime / 100) % 10); //$NON-NLS-1$
			}
			return NLS.bind(HibernateConsoleMessages.QueryPagePropertySource_millisec, resultTime);
		}
		if(TAB_NAME.equals(id) ) {
			return page.getTabName();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
		if (TAB_NAME.equals(id)) {
			page.setTabName(page.getQueryString().replace('\n', ' ').replace('\r', ' '));
		}
	}

	public void setPropertyValue(Object id, Object value) {
		if (TAB_NAME.equals(id)) {
			page.setTabName(value == null ? "<null>" : value.toString()); //$NON-NLS-1$
		}
	}
}