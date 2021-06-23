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

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.common.ConsoleExtension;
import org.hibernate.eclipse.console.common.HibernateExtension;
import org.hibernate.eclipse.console.views.QueryPageTabView;

public class HibernatePropertySourceProvider implements IPropertySourceProvider {	

	private final QueryPageTabView view;

	public HibernatePropertySourceProvider(QueryPageTabView view) {
		this.view = view;
	}

	public IPropertySource getPropertySource(Object object) {
		IPropertySource result = null;
		if (object==null) {
			return null;
		}
		else if (object instanceof QueryPage)
		{
			return new QueryPagePropertySource( (QueryPage)object);
		}
		else if (object instanceof IPropertySource) {
			return (IPropertySource) object;
		}
		else {
			QueryPage selectedQueryPage = view.getSelectedQueryPage();
			if (selectedQueryPage != null) {
				HibernateExtension hibernateExtension = selectedQueryPage.getHibernateExtension();
				if (hibernateExtension != null) {
					ConsoleExtension consoleExtension = hibernateExtension.getConsoleExtension();
					if (consoleExtension != null) {
						result = consoleExtension.getPropertySource(object, selectedQueryPage);
					}
				}
			}
			return result;
		}		
	}

}