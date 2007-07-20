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
package org.jboss.tools.hibernate.ui.veditor.editors.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;


public class TitleLabel extends Label {

	static Image shevronUp = ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("VisualMapping.shevronUp")).createImage();
	static Image shevronDown = ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("VisualMapping.shevronDown")).createImage();

	protected boolean hiden = false; 
	

	protected Dimension calculateLabelSize(Dimension txtSize) {
		Dimension p = super.calculateLabelSize(txtSize).getCopy();
		p.width+=40;
		return p;
	}

	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if(hiden)
			graphics.drawImage(shevronDown, getBounds().x+getBounds().width-20, getBounds().y);
		else
			graphics.drawImage(shevronUp, getBounds().x+getBounds().width-20, getBounds().y);
	}
	
	public void setHiden(boolean hiden) {
		this.hiden = hiden;
		repaint();
	}
}
