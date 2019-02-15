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
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
public class DiagramElementInfo implements IItemInfo {

	protected OrmShape element;
	protected List<ILinkInfo> links = new ArrayList<ILinkInfo>();
	protected int cxFigure, cyFigure;

	/**
	 * 
	 * @param element
	 */
	public DiagramElementInfo(OrmShape element, int cxFigure, int cyFigure) {
		this.element = element;
		this.cxFigure = cxFigure;
		this.cyFigure = cyFigure;
		ILinkInfo link;
		for (Connection connection : element.getSourceConnections()) {
			if (connection.isVisible()) {
				link = new LinkInfo(connection);
				addLink(link);
			}
		}
		Iterator<Shape> it = element.getChildrenIterator();
		while (it.hasNext()) {
			Shape child = it.next();
			if (!child.isVisible()) {
				continue;
			}
			final List<Connection> sourceConnections = child.getSourceConnections(); 
			if (sourceConnections.size() == 0) {
				link = new LinkInfo(getID());
				addLink(link);
			}
			for (Connection connection : sourceConnections) {
				if (connection.isVisible()) {
					link = new LinkInfo(connection);
					addLink(link);
				}
			}
		}
	}

	/**
	 * 
	 */
	public String getID() {
		return element.toString();
	}

	/**
	 * there are no comments on Diagram Viewer!!!
	 */
	public boolean isComment() {
		return false;
	}

	public boolean isVisible() {
		return element.isVisible();
	}

	/**
	 * gets shape vertices
	 */
	public int[] getShape() {
		int[] shape = new int[4];
		shape[0] = element.getLocation().x;
		shape[1] = element.getLocation().y;
		shape[2] = cxFigure;
		shape[3] = cyFigure;
		return shape;
	}

	/**
	 * setup OrmShape up-left point location, using s[0] and s[1]
	 */
	public void setShape(int[] s) {
		element.setLocation(new Point(s[0], s[1]));
	}

	/**
	 * 
	 */
	public ILinkInfo[] getLinks() {
		return links.toArray(new ILinkInfo[0]);
	}

	/**
	 * 
	 * @param link
	 */
	public void addLink(ILinkInfo link) {
		links.add(link);
	}

}
