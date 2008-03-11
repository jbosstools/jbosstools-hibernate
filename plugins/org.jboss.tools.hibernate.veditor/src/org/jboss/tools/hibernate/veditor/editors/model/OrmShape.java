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

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;


/**
 * @author Konstantin Mishin
 *
 */
public class OrmShape extends ExtendedShape {
	
	public static final String LOCATION_PROP = "OrmShape.Location";		
	public static final String SET_HIDEN = "set hiden";
	private Point location = new Point(0, 0);
	private boolean hiden = false; 
	
	public OrmShape(IOrmElement  ioe) {	
		super(ioe);
		Shape bodyOrmShape;
		if (getOrmElement() instanceof IPersistentClass) {
			IPersistentField persistentFields[] = ((IPersistentClass)getOrmElement()).getFields();
			for (int i = 0; i < persistentFields.length; i++) {
				if (persistentFields[i].getMapping() != null && persistentFields[i].getMapping().getPersistentValueMapping() instanceof ICollectionMapping)
					bodyOrmShape = new ExtendedShape(persistentFields[i]);		
				else
					bodyOrmShape = new Shape(persistentFields[i]);
				shapes.add(bodyOrmShape);
			}
		}else if (getOrmElement() instanceof IDatabaseTable) {
			IDatabaseColumn databaseColumns[] = ((IDatabaseTable)getOrmElement()).getColumns();
			for (int i = 0; i < databaseColumns.length; i++) {
				bodyOrmShape = new Shape(databaseColumns[i]);
				shapes.add(bodyOrmShape);
			}
		}		
	}
	
	public Point getLocation() {
		return location.getCopy();
	}
	
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, location);
	}
	
	public void refreshHiden() {
		hiden = !hiden;
		firePropertyChange(SET_HIDEN, null, new Boolean(hiden));
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(hiden);
	}

	public boolean isHiden() {
		return hiden;
	}
}