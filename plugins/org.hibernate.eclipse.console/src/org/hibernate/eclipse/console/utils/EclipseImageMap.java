/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

	public EclipseImageMap() {

		declareImages();
	}
	
	/** Declare Common paths */
	protected static URL ICON_BASE_URL = null;

	static {
		String pathSuffix = "icons/";//$NON-NLS-1$

		try {
			ICON_BASE_URL = new URL(HibernateConsolePlugin.getDefault().getBundle().getEntry("/"), pathSuffix);//$NON-NLS-1$
		} catch (MalformedURLException e) {
			// do nothing
		}
	}

	/** A table of all the <code>ImageDescriptor</code>s. */
	Map imageDescriptors = new HashMap();
	
	/** The image registry containing <code>Image</code>s. */
	ImageRegistry imageRegistry = new ImageRegistry();
	
	protected void declareRegistryImage(String key, String path) {
		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
		try {
			desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
		} catch (MalformedURLException me) {
			HibernateConsolePlugin.log(me);
		}
		imageRegistry.put(key, desc);
		imageDescriptors.put(key, desc);
	}
	
	protected static URL makeIconFileURL(String iconPath)
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
		
		return (ImageDescriptor) imageDescriptors.get(key);
	}

}
