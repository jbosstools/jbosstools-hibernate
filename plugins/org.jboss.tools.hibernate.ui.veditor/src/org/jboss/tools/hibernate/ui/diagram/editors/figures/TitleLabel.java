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
package org.jboss.tools.hibernate.ui.diagram.editors.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.view.ImageBundle;


public class TitleLabel extends Label {

	static Image shevronUp = UiPlugin.getImageDescriptor2(ImageBundle.getString("VisualMapping.shevronUp")).createImage(); //$NON-NLS-1$
	static Image shevronDown = UiPlugin.getImageDescriptor2(ImageBundle.getString("VisualMapping.shevronDown")).createImage(); //$NON-NLS-1$

	protected boolean hiden = false; 
	

	protected Dimension calculateLabelSize(Dimension txtSize) {
		Dimension p = super.calculateLabelSize(txtSize).getCopy();
		p.width += 40;
		return p;
	}

	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if (hiden) {
			graphics.drawImage(shevronDown, getBounds().x+getBounds().width-20, getBounds().y);
		} else {
			graphics.drawImage(shevronUp, getBounds().x+getBounds().width-20, getBounds().y);
		}
	}
	
	public void setHidden(boolean hiden) {
		this.hiden = hiden;
		repaint();
	}
}
