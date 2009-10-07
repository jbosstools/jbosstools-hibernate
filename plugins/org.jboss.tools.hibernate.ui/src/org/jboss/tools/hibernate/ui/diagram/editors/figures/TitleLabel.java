/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.ResourceManager;
import org.jboss.tools.hibernate.ui.view.ImageBundle;

/**
 * Specific label with 2 additional states: expanded and collapsed
 * 
 * @author some modifications from Vitali
 */
public class TitleLabel extends Label {

	static Image shevronUp = UiPlugin.getImageDescriptor2(ImageBundle.getString("VisualMapping.shevronUp")).createImage(); //$NON-NLS-1$
	static Image shevronDown = UiPlugin.getImageDescriptor2(ImageBundle.getString("VisualMapping.shevronDown")).createImage(); //$NON-NLS-1$

	/**
	 * to represent expanded/collapsed state
	 */
	protected boolean expanded = true;
	
	public TitleLabel(float fontHeight) {
		if (Display.getCurrent() != null) {
			final Font font = Display.getCurrent().getSystemFont();
			if (font != null) {
				FontData fontData[] = font.getFontData();
				fontData[0].height = fontHeight;
				setFont(ResourceManager.getInstance().getFont(fontData[0]));
			}
		}
	}

	/**
	 * @see Label#calculateLabelSize(Dimension)
	 */
	@Override
	protected Dimension calculateLabelSize(Dimension txtSize) {
		Dimension p = super.calculateLabelSize(txtSize).getCopy();
		p.width += 40;
		return p;
	}

	/**
	 * @see Figure#paintFigure(Graphics)
	 * plus draws expanded or collapsed state
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if (expanded) {
			graphics.drawImage(shevronUp, getBounds().x + getBounds().width - 20, getBounds().y);
		} else {
			graphics.drawImage(shevronDown, getBounds().x + getBounds().width - 20, getBounds().y);
		}
	}
	
	public void setExpanded(boolean expanded) {
		if (this.expanded != expanded) {
			this.expanded = expanded;
		}
		repaint();
	}
}
