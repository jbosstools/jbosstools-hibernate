package org.hibernate.eclipse.console.utils;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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
			map = new EclipseImageMap();
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
