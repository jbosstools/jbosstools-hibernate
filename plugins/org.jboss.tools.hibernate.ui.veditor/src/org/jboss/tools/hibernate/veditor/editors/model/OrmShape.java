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
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;

/**
 * @author Konstantin Mishin
 *
 */
public class OrmShape extends ExpandeableShape {
	public static final String SET_HIDEN = "set hiden";
	
	public static final String LOCATION_PROP = "OrmShape.Location";		
	private Point location = new Point(0, 0);
	protected boolean hiden = false;
	
	public OrmShape(Object ioe) {	
		super(ioe);
		generate();
	}
	
	protected void generate() {
		Shape bodyOrmShape;
		Object ormElement = getOrmElement();
		if (ormElement instanceof RootClass) {
			RootClass rootClass = (RootClass)getOrmElement();
			Property identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				shapes.add(new Shape(identifierProperty));
			}

			KeyValue identifier = rootClass.getIdentifier();
			if (identifier instanceof Component) {
				Iterator iterator = ((Component)identifier).getPropertyIterator();
				while (iterator.hasNext()) {
					Property property = (Property) iterator.next();
					shapes.add(new Shape(property));
				}
			}

			Iterator iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = (Property)iterator.next();
				if (!field.isComposite()) {
					if (field.getValue().getType().isEntityType()) {
						bodyOrmShape = new ExpandeableShape(field);
					} else if (field.getValue().getType().isCollectionType()) {
						bodyOrmShape = new ComponentShape(field);
					} else {
						bodyOrmShape = new Shape(field);
					}
					shapes.add(bodyOrmShape);
				} else {
					Component component = (Component)field.getValue();
					Iterator iter = component.getPropertyIterator();
					while (iter.hasNext()) {
						Property property = (Property)iter.next();
						if (property.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandeableShape(property);
						} else if (property.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(property);
						} else {
							bodyOrmShape = new Shape(property);
						}
						shapes.add(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof SingleTableSubclass) {
			Iterator iterator = ((SingleTableSubclass)ormElement).getRootClass().getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = (Property)iterator.next();
				if (!field.isComposite()) {
					if (field.getValue().getType().isEntityType()) {
						bodyOrmShape = new ExpandeableShape(field);
					} else if (field.getValue().getType().isCollectionType()) {
						bodyOrmShape = new ComponentShape(field);
					} else {
						bodyOrmShape = new Shape(field);
					}
					shapes.add(bodyOrmShape);
				} else {
					Component component = (Component)field.getValue();
					Iterator iter = component.getPropertyIterator();
					while (iter.hasNext()) {
						Property property = (Property)iter.next();
						if (property.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandeableShape(property);
						} else if (property.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(property);
						} else {
							bodyOrmShape = new Shape(property);
						}
						shapes.add(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof Table) {
			Iterator iterator = ((Table)getOrmElement()).getColumnIterator();
			while (iterator.hasNext()) {
				Column column = (Column)iterator.next();
				bodyOrmShape = new Shape(column);
				shapes.add(bodyOrmShape);
			}
		}
	}
	
	public Shape getChild(Object ormElement) {
		Shape shape = null;
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Shape child = (Shape)iter.next();
			Object childElement = child.getOrmElement();
			if (ormElement == childElement) {
				return child;
			}
		}
		return shape;
	}

	protected void setHiden(boolean hiden) {
		super.setHiden(hiden);
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(hiden);
	}

	public void refreshHiden() {
		hiden = !hiden;
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(hiden);
		firePropertyChange(SET_HIDEN, null, new Boolean(hiden));
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

	public boolean isHiden() {
		return hiden;
	}
}