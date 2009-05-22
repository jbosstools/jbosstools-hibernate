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
package org.hibernate.eclipse.console.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageMap;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
public class EclipseImageMap extends ImageMap {

	protected URL ICON_BASE_URL = null;
	
	public EclipseImageMap(Plugin plugin) {
		String pathSuffix = "icons/";//$NON-NLS-1$

		try {
			ICON_BASE_URL = new URL(plugin.getBundle().getEntry("/"), pathSuffix);//$NON-NLS-1$
		} catch (MalformedURLException e) {
			// do nothing
		}
		
		declareImages();
	}
	
	/** A table of all the <code>ImageDescriptor</code>s. */
	Map<String, ImageDescriptor> imageDescriptors = new HashMap<String, ImageDescriptor>();
	
	/** The image registry containing <code>Image</code>s. */
	ImageRegistry imageRegistry = new ImageRegistry();
	
	protected void declareRegistryImage(String key, String path) {
		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
		try {
			desc = ImageDescriptor.createFromURL(makeIconFileURL(path) );
		} catch (MalformedURLException me) {
			HibernateConsolePlugin.getDefault().log(me);
		}
		imageRegistry.put(key, desc);
		imageDescriptors.put(key, desc);
	}
	
	protected URL makeIconFileURL(String iconPath)
			throws MalformedURLException {
		if (ICON_BASE_URL == null) {
			throw new MalformedURLException();
		}

		return new URL(ICON_BASE_URL, iconPath);
	}

	public Image getImage(String key) {
		return imageRegistry.get(key);
	}

	public ImageDescriptor getImageDescriptor(String key) {
		
		return imageDescriptors.get(key);
	}

}
