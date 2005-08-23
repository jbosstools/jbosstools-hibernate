/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.console;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author max
 *
 */
public class SwingImageMap extends ImageMap {

	public SwingImageMap() {

		declareImages();
	}
	
	Map imageRegistry = new HashMap();
	
	protected void declareRegistryImage(String key, String path) {
		imageRegistry.put(key, new ImageIcon(SwingImages.class.getResource(path) ) );		
	}

	public Icon getIcon(String iconName) {
		return (Icon) imageRegistry.get(iconName);
	}

}
