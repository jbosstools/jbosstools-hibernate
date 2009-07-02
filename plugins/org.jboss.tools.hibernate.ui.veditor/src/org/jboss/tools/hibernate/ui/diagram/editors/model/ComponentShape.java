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
package org.jboss.tools.hibernate.ui.veditor.editors.model;

import java.util.Iterator;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;

public class ComponentShape extends ExpandeableShape {

	public static final String SET_CHILDS_HIDEN = "set childs hiden"; //$NON-NLS-1$

	public ComponentShape(Object ioe) {	
		super(ioe);
		Shape bodyOrmShape;
		if (ioe instanceof Property) {
			Collection collection = (Collection)((Property)ioe).getValue();
			bodyOrmShape = new Shape(collection.getKey());
			bodyOrmShape.setIndent(20);
			addChild(bodyOrmShape);
			bodyOrmShape = new Shape(collection.getElement());
			bodyOrmShape.setIndent(20);
			addChild(bodyOrmShape);
		}
	}
	
	protected void setChildsHidden(boolean hidden) {
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			it.next().setHidden(hidden);
		}
	}

	public void refreshChildsHiden(OrmDiagram ormDiagram) {
		refHide = !refHide;
		setChildsHidden(!refHide);
		if (refHide) {
			if (first) {
				ormDiagram.refreshComponentReferences(this);
				first = false;
			}
		}
		firePropertyChange(SET_CHILDS_HIDEN, null, Boolean.valueOf(!refHide));
	}
}