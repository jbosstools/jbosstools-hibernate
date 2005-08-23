/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.console;

import javax.swing.Icon;

/**
 * @author max
 *
 */
public class SwingImages implements ImageConstants {

	static SwingImageMap map;
	
	public static Icon getIcon(String iconName) {
		return getMap().getIcon(iconName);
	}


	private static SwingImageMap getMap() {
		if(map==null) {
			map = new SwingImageMap();
		}
		return map;
	}

	


	

}
