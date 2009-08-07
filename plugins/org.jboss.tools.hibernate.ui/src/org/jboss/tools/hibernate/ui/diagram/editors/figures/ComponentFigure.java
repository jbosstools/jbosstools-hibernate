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
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author some modifications from Vitali
 */
@SuppressWarnings("unchecked")
public class ComponentFigure extends Figure {
	
	/**
	 * to represent expanded/collapsed state
	 */
	protected boolean expanded = true;
	
	protected TitleLabel titleLabel = null;
	
	public ComponentFigure() {
		super();
		setLayoutManager(new ToolbarLayout());
	}
	
	public void createTitle(String text, Image icon, Color bg) {
		removeTitle();
		TitleLabel label = new TitleLabel();
		label.setText(text);	
		label.setBackgroundColor(bg);
		label.setOpaque(true);
		label.setIcon(icon);
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setBorder(new MarginBorder(1, 2, 1, 2));
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
