/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.view;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Tau from Minsk
 * 
 */
public class ColourProvider {

	public final static RGB DEFAULT = new RGB(0, 0, 0); // Black

	public final static RGB KEYWORD = new RGB(127, 0, 85);

	public final static RGB COMMENT = new RGB(63, 95, 191); 

	public static final RGB STRING = new RGB(0, 128, 0);

	public final static RGB ENTITY = new RGB(0, 128, 0); 

	public final static RGB ERROR = new RGB(210, 20, 60);

	private HashMap<RGB,Color> fColourTable = new HashMap<RGB,Color>(10);

	/**
	 * Insert the method's description here.
	 */
	public void dispose() {

		Iterator e = fColourTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();

	}

	/**
	 * Insert the method's description here.
	 */
	public void finalize() {

		dispose();
	}

	/**
	 * Insert the method's description here.
	 * @return org.eclipse.swt.graphics.Color
	 * @param rgb org.eclipse.swt.graphics.RGB
	 */
	public Color getColor(RGB rgb) {

		Color color = (Color) fColourTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColourTable.put(rgb, color);
		}
		return color;
	}
}
