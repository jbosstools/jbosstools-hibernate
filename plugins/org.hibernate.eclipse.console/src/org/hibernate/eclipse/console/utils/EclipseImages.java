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

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class EclipseImages {

	static EclipseImageMap map = null;
	
	/** Declare Common paths */
	protected static URL ICON_BASE_URL = null;

	/**
	 * Returns the <code>Image<code> identified by the given key,
	 * or <code>null</code> if it does not exist.
	 *
	 * @param key  Description of the Parameter
	 * @return     The image value
	 */
	public static Image getImage(String key) {
		return getMap().getImage(key);
	}

	private static EclipseImageMap getMap() {
		if(map==null) {
			map = new EclipseImageMap(HibernateConsolePlugin.getDefault());
		}
		return map;
	}

	/**
	 * Returns the <code>ImageDescriptor<code> identified by the given key,
	 * or <code>null</code> if it does not exist.
	 *
	 * @param key  Description of the Parameter
	 * @return     The imageDescriptor value
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return getMap().getImageDescriptor(key);
	}


}
