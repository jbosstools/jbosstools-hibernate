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
package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.NodeFactory;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class LazySessionFactoryAdapter extends BasicWorkbenchAdapter {



	public Object[] getChildren(Object o) {
		LazySessionFactory lazySessionFactory = getLazySessionFactory(o);
		String label = HibernateConsoleMessages.LazySessionFactoryAdapter_session_factory;
		if(lazySessionFactory.getCfgNode()==null) {
			if(lazySessionFactory.getConsoleConfiguration().getSessionFactory()==null) {
				try {
					lazySessionFactory.getConsoleConfiguration().buildSessionFactory();
				} catch(Throwable t) {
					label = NLS.bind(HibernateConsoleMessages.LazySessionFactoryAdapter_sessionfactory_error, t.getMessage());
					HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.LazySessionFactoryAdapter_problems_while_creating_sessionfactory, t);
				}
			}
			if(lazySessionFactory.getConsoleConfiguration().isSessionFactoryCreated()) {
				NodeFactory fac = new NodeFactory(lazySessionFactory.getConsoleConfiguration());
				lazySessionFactory.setCfgNode( fac.createConfigurationEntitiesNode(label) );
			}
		}
		if(lazySessionFactory.getCfgNode()!=null) {
			return toArray(lazySessionFactory.getCfgNode().children(),BaseNode.class);
		} else {
			return new Object[] { label };
		}
	}

	private LazySessionFactory getLazySessionFactory(Object o) {
		return (LazySessionFactory)o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor( ImageConstants.TYPES );
	}

	public String getLabel(Object o) {
		return HibernateConsoleMessages.LazySessionFactoryAdapter_session_factory;
	}

	public Object getParent(Object o) {
		return getLazySessionFactory( o ).getConsoleConfiguration();
	}

}
