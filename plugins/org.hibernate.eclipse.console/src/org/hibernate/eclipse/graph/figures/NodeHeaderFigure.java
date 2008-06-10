/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http:/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.hibernate.eclipse.console.utils.EclipseImages;

// Based on what was found in JBPM GPD 19/4/2006 04:34 AM
public class NodeHeaderFigure extends Figure {
	
	private static final Font NAMEFONT = new Font(null, "Arial", 9, SWT.BOLD); //$NON-NLS-1$
	private static final Font TYPEFONT = new Font(null, "Arial", 9, SWT.ITALIC); //$NON-NLS-1$
	
	private Figure embeddedFigure;
	private Figure typeAndLabelColumn;
	private Label typeLabel;
	private Label nameLabel;
	private Label iconLabel;
	
	public NodeHeaderFigure(String nodeName, String nodeType, String iconName, boolean hideName) {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
		setLayoutManager(flowLayout);
		addEmbeddedParent(nodeName, nodeType, iconName, hideName);
	}
  
  
  private void addEmbeddedParent(String nodeName, String nodeType, String iconDescriptor, boolean hideName) {
    embeddedFigure = new Figure();
    ToolbarLayout layout = new ToolbarLayout(true);
    layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
    embeddedFigure.setLayoutManager(layout);
    addIconLabel(iconDescriptor);
    addTypeAndNameColumn(nodeType, nodeName, hideName);
    add(embeddedFigure);
  }
  
  
	private void addTypeAndNameColumn(String nodeType, String nodeName, boolean hideName) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(false);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		typeAndLabelColumn = new Figure();
		typeAndLabelColumn.setLayoutManager(layout);
		//addTypeLabel(nodeType);
		if (!hideName) {
			addNameLabel(nodeName);
		}
		embeddedFigure.add(typeAndLabelColumn);
	}

	private void addNameLabel(String nodeName) {
		nameLabel = new Label();
		nameLabel.setBorder(new MarginBorder(2));
		nameLabel.setForegroundColor(ColorConstants.darkGray);
		nameLabel.setFont(NAMEFONT);
		nameLabel.setText(nodeName);
		typeAndLabelColumn.add(nameLabel);
	}

	private void addTypeLabel(String nodeType) {
		typeLabel = new Label();
		typeLabel.setBorder(new MarginBorder(2));
		typeLabel.setForegroundColor(ColorConstants.darkGray);
		typeLabel.setFont(TYPEFONT);
		typeLabel.setText("<<" + nodeType + ">>"); //$NON-NLS-1$ //$NON-NLS-2$
		typeAndLabelColumn.add(typeLabel);
	}

  private void addIconLabel(String iconDescriptor) {
    iconLabel = new Label();
    iconLabel.setBorder(new MarginBorder(2));
    iconLabel.setIcon(EclipseImages.getImage(iconDescriptor));
    embeddedFigure.add(iconLabel);
  }
  
/*  private Image getNodeIcon(ImageDescriptor iconDescriptor) {
	  return EclipseImages.getImage(iconDescriptor);
  }*/
	
  
	
	protected void paintClientArea(Graphics graphics) {
		Color foreground = graphics.getForegroundColor();
		graphics.setForegroundColor(FiguresConstants.white);
		graphics.fillGradient(getClientArea(), true);
		graphics.setForegroundColor(foreground);
		super.paintClientArea(graphics);
	}
	
	public void setNodeName(String name) {
		if (name != null && nameLabel != null) {
			nameLabel.setText(name);
		}
	}
	
	public Label getNameLabel() {
		return nameLabel;
	}
}
