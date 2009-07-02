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
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

public class RoundPolylineConnection extends PolylineConnection {
	protected void outlineShape(Graphics g) {
		PointList points = getPoints();
		Point point = points.getPoint(0);
		Point beg = new Point();
		Point end = new Point();
		Point eCorner = new Point();
		Point bCorner = new Point();
		boolean horiz;
		beg.x = point.x;
		beg.y = point.y;
	
		if (points.getFirstPoint().y == points.getLastPoint().y) {
			super.outlineShape(g);
			return;
		}
		if (Math.abs(points.getFirstPoint().y - points.getLastPoint().y) < 4) {
			int delta = Math.abs(points.getFirstPoint().y - points.getLastPoint().y);
			if (points.size() == 4) {
				Point point1 = points.getPoint(0);
				Point point2 = points.getPoint(1);
				Point point3 = points.getPoint(2);
				Point point4 = points.getPoint(3);
				if (point1.x < point4.x) {
					point2.x -= delta/2;
					point3.x += delta/2;
				} else {
					point2.x += delta/2;
					point3.x -= delta/2;
				}
				g.drawLine(point1, point2);
				g.drawLine(point2, point3);
				g.drawLine(point3, point4);
				return;
			}
		}
	
		for (int i = 1; i < points.size(); i++) {
			point = points.getPoint(i);
			end.x = point.x;
			end.y = point.y;
	
			if (beg.y == end.y)
				horiz = true;
			else
				horiz = false;
	
			eCorner.x = 0;
			if (i != 1) {
				if (horiz) {
					if (end.x > beg.x) {
						beg.x += 2;
					} else {
						beg.x -= 2;
					}
				} else {
					if (end.y > beg.y) {
						beg.y += 2;
					} else {
						beg.y -= 2;
					}
				}
				eCorner.x = beg.x;
				eCorner.y = beg.y;
			}
			
			if (bCorner.x != 0 && eCorner.x != 0) {
				g.drawLine(bCorner, eCorner);
			}
			bCorner.x = 0;
	
			if (i != points.size() - 1) {
				if (horiz) {
					if (end.x > beg.x) {
						end.x -= 2;
					} else {
						end.x += 2;
					}
				} else {
					if (end.y > beg.y) {
						end.y -= 2;
					} else {
						end.y += 2;
					}
				}
				bCorner.x = end.x;
				bCorner.y = end.y;
			}
			
			g.drawLine(beg, end);
			point = points.getPoint(i);
			beg.x = point.x;
			beg.y = point.y;
		}
	}
}