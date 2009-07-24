/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl;

public class LayoutConstants {
	static int DELTA_X = 200;
	static int DELTA_Y = 104;
	static int X_INC = 32;
	static int Y_INC = 24;
	
	public int deltaX = DELTA_X;
	public int deltaY = DELTA_Y;
	public int incX = X_INC;  
	public int incY = Y_INC;
	public int indentX = 24;
	public int indentY = 16;
	
	public void update(String gridStep) {
		int step = 0;
		try {
			step = Integer.parseInt(gridStep);
		} catch (NumberFormatException e) {
			deltaX = DELTA_X;
			deltaY = DELTA_Y;
			incX = X_INC;
			incY = Y_INC;
		}	
		indentX = (step < 24) ? 24 : step;
		indentY = (step < 16) ? 16 : step;
		if (step == 16) {
			deltaX = 208;
			deltaY = 112;
			incX = 16;
			incY = 32;
			indentX = 32;
		} else if (step == 24) {
			deltaX = 240;
			deltaY = 120;
			incX = 24;
			incY = 24;
		} else if (step == 32) {
			deltaX = 256;
			deltaY = 128;
			incX = 32;
			incY = 32;
		} else if (step == 40) {
			deltaX = 240;
			deltaY = 120;
			incX = 40;
			incY = 40;
		} else {
			deltaX = DELTA_X;
			deltaY = DELTA_Y;
			incX = X_INC;
			incY = Y_INC;
		}
	}
	
}
