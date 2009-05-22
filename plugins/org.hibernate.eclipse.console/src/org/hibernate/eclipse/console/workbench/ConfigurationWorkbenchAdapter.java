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

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.PersistentClass;

public class ConfigurationWorkbenchAdapter extends BasicWorkbenchAdapter {

	@SuppressWarnings("unchecked")
	public Object[] getChildren(Object o) {
		Configuration cfg = (Configuration) o;
		Iterator<? extends PersistentClass> classMappings = cfg.getClassMappings();
		return toArray(classMappings, PersistentClass.class, new Comparator<PersistentClass>() {

			public int compare(PersistentClass p0, PersistentClass p1) {
				String label0 = HibernateWorkbenchHelper.getLabelForClassName(p0.getEntityName());
				String label1 = HibernateWorkbenchHelper.getLabelForClassName(p1.getEntityName());
				return label0.compareTo(label1);
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
