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

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.ResourceManager;

/**
 * Main draw figure to OrmShape. It has a border and children.
 * One specific child is TitleLabel.
 * Could be in 2 states: expanded and collapsed.
 *
 * @author some modifications from Vitali
 */
@SuppressWarnings("unchecked")
public class TitleFigure extends Figure {
	
	/**
	 * to represent expanded/collapsed state
	 */
	protected boolean expanded = true;
	
	protected TitleLabel titleLabel = null;
	
	public TitleFigure() {
		super();
		RoundLineBorder border = new RoundLineBorder();
		border.setColor(ResourceManager.getInstance().getColor(new RGB(160, 160, 160)));
		setBorder(border);
		setSize(-1, -1);
	}
	
	/**
	 * Creates title child figure.
	 * @param text
	 * @param icon
	 * @param bg
	 */
	public void createTitle(String text, Image icon, Color bg) {
		removeTitle();
		TitleLabel label = new TitleLabel();
		label.setText(text);
		FontData fontData[] = Display.getCurrent().getSystemFont().getFontData();
		fontData[0].setStyle(SWT.BOLD);
		//fontData[0].height++;
		label.setFont(ResourceManager.getInstance().getFont(fontData[0]));
		label.setBackgroundColor(bg);
		label.setIcon(icon);
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setBorder(new MarginBorder(1, 2, 1, 2));
		label.setOpaque(true);
		add(label, -2);
		titleLabel = label;
	}

	public void removeTitle() {
		if (titleLabel != null) {
			remove(titleLabel);
			titleLabel = null;
		}
	}

	/**
	 * Override
	 * @see IFigure#add(IFigure, Object, int)
	 * so index == -2 means add to the 0 place, in other case index incremented
	 */
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (index != -1) {
			if (index == -2) {
				index = 0;
			} else {
				index++;
			}
		}
		super.add(figure, constraint, index);
	}
	
	/**
	 * @see IFigure#getChildren()
	 * in expanded state return all children,
	 * in collapsed state return only the first child
	 */
	@Override
	public List getChildren() {
		List res = super.getChildren();
		// in collapsed state - show only first title item
		res = expanded ? res : res.subList(0, 1);
		return res;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (titleLabel != null) {
			titleLabel.setExpanded(expanded);
		}
		int width = expanded ? -1 : getPreferredSize().width;
		setSize(width, -1);
		repaint();
	}

	public boolean getExpanded() {
		return expanded;
	}

	public void refresh() {
		setExpanded(getExpanded());
	}
}
