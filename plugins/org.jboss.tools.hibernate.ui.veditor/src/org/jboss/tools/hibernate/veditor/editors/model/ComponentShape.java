/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.veditor.editors.model;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;

/**
 * @author Konstantin Mishin
 *
 */
public class ComponentShape extends ExtendedShape {
	public static final String SET_CHILDS_HIDEN = "set childs hiden";

	protected boolean childsHiden = true;

	public ComponentShape(Object ioe) {	
		super(ioe);
		Shape bodyOrmShape;
		if (ioe instanceof Property) {
			Collection collection = (Collection)((Property)ioe).getValue();
			bodyOrmShape = new Shape(collection.getKey());
			bodyOrmShape.setIndent(20);
			shapes.add(bodyOrmShape);
			bodyOrmShape = new Shape(collection.getElement());
			bodyOrmShape.setIndent(20);
			shapes.add(bodyOrmShape);
		}
	}
	
	protected void setChildsHiden(boolean hiden) {
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(hiden);
	}

	public void refreshChildsHiden(OrmDiagram ormDiagram) {
		childsHiden = !childsHiden;
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(childsHiden);
		firePropertyChange(SET_CHILDS_HIDEN, null, new Boolean(childsHiden));
		ormDiagram.refreshComponentReferences(this);
	}
}