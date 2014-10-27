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

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IPersistentClass;

public class ConfigurationWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		IConfiguration cfg = (IConfiguration) o;
		Iterator<IPersistentClass> classMappings = cfg.getClassMappings();
		return toArray(classMappings, Object.class, new Comparator<Object>() {

			public int compare(Object p0, Object p1) {
				String label0 = HibernateWorkbenchHelper.getLabelForClassName(getEntityName(p0));
				String label1 = HibernateWorkbenchHelper.getLabelForClassName(getEntityName(p1));
				return label0.compareTo(label1);
			}
			
			private String getEntityName(Object o) {
				String result = null;
				try {
					Method m = o.getClass().getMethod("getEntityName", new Class[] {}); //$NON-NLS-1$
					result = (String)m.invoke(o, new Object[] {});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return result;
			}

		});
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.CONFIGURATION);
	}

	public String getLabel(Object o) {
		return HibernateConsoleMessages.ConfigurationWorkbenchAdapter_configuration;
	}

	public Object getParent(Object o) {
		return KnownConfigurations.getInstance();
	}

	public boolean isContainer() {
		return true;
	}

	

}
