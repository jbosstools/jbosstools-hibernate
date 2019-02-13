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
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class InheritanceViewAdapter extends AssociationViewAdapter {

	private final PersistentClassViewAdapter subclass;
	private final PersistentClassViewAdapter superclass;

	public InheritanceViewAdapter(PersistentClassViewAdapter subclass, PersistentClassViewAdapter superclass) {
		if(subclass==superclass) throw new IllegalArgumentException(HibernateConsoleMessages.InheritanceViewAdapter_subclass_must_not_be_equal_to_superclass);
		if(subclass==null) throw new IllegalArgumentException(HibernateConsoleMessages.InheritanceViewAdapter_subclass_must_not_null);
		if(superclass==null) throw new IllegalArgumentException(HibernateConsoleMessages.InheritanceViewAdapter_superclass_must_not_null);
		this.subclass = subclass;
		this.superclass = superclass;
	}

	public RotatableDecoration getTargetDecoration() {
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0,0);
		decorationPointList.addPoint(-2,2);
		decorationPointList.addPoint(-2,-2);
		decoration.setTemplate(decorationPointList);
		decoration.setFill(true);
		decoration.setBackgroundColor(ColorConstants.white);
		return decoration;
	}

	public ConnectionRouter getConnectionRouter() {
		return new ManhattanConnectionRouter();
	}

	public String toString() {
		return HibernateConsoleMessages.InheritanceViewAdapter_inheritance + super.toString();
	}

	public String getSourceName() {
		return subclass.getPersistentClass().getEntityName();
	}

	public String getTargetName() {
		return superclass.getPersistentClass().getEntityName();
	}

}