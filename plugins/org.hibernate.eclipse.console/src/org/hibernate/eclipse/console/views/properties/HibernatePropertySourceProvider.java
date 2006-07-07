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
import org.hibernate.Session;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.hibernate.proxy.HibernateProxyHelper;

public class HibernatePropertySourceProvider implements IPropertySourceProvider
{	
	// TODO: refactor to be some interface that can provide currentsession and currentconfiguration
	private final QueryPageTabView view;

	public HibernatePropertySourceProvider(QueryPageTabView view) {
		this.view = view;
	}

	public IPropertySource getPropertySource(Object object) {
		if (object==null) {
			return null;
		}
		else if (object instanceof QueryPage)
		{
			return new QueryPagePropertySource( (QueryPage)object);
		}
		else if (object instanceof CollectionPropertySource) {
			return (IPropertySource) object;
		}
		else {
			//			 maybe we should be hooked up with the queryview to get this ?
			Session currentSession = view.getSelectedQueryPage().getSession();
			ExecutionContextHolder currentConfiguration = view.getSelectedQueryPage().getConsoleConfiguration();
			if((currentSession.isOpen() && currentSession.contains(object)) || hasMetaData( object, currentSession) ) {
				return new EntityPropertySource(object, currentSession, currentConfiguration);	
			} else {
				return null;
			}
			
		}
		
	}

	private boolean hasMetaData(Object object, Session currentSession) {
		return currentSession.getSessionFactory().getClassMetadata(HibernateProxyHelper.getClassWithoutInitializingProxy(object))!=null;
	}
}