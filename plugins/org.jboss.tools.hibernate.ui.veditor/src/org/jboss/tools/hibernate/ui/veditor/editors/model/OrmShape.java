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

import org.eclipse.draw2d.geometry.Point;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;

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
				getChildren().add(new Shape(identifierProperty));
			}

			KeyValue identifier = rootClass.getIdentifier();
			if (identifier instanceof Component) {
				Component component = (Component)identifier;
				if (component.isEmbedded()) {
					Iterator iterator = ((Component)identifier).getPropertyIterator();
					while (iterator.hasNext()) {
						Property property = (Property) iterator.next();
						getChildren().add(new Shape(property));
					}
				}
			}

			Iterator iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = (Property)iterator.next();
				if (!field.isComposite()) {
					boolean typeIsAccessible = true;
					if (field.getValue().isSimpleValue() && ((SimpleValue)field.getValue()).isTypeSpecified()) {
						try {
							field.getValue().getType();
						} catch (Exception e) {
							typeIsAccessible = false;
						}
					}
/*					if (field.isComposite()) {
						bodyOrmShape = new ExpandeableShape(field);
					} else */if (field.getValue().isSimpleValue() && !((SimpleValue)field.getValue()).isTypeSpecified()) {
						bodyOrmShape = new Shape(field);
					} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
						bodyOrmShape = new ExpandeableShape(field);
					} else if (typeIsAccessible && field.getValue().getType().isCollectionType()) {
						bodyOrmShape = new ComponentShape(field);
					} else {
						bodyOrmShape = new Shape(field);
					}
					getChildren().add(bodyOrmShape);
				} else {
//					Component component = (Component)field.getValue();
//					Iterator iter = component.getPropertyIterator();
//					while (iter.hasNext()) {
//						Property property = (Property)iter.next();
//						boolean typeIsAccesible = true;
//						try {property.getValue().getType();} catch (Exception e) {typeIsAccesible = false;}
///*						if (property.isComposite()) {
//							bodyOrmShape = new ExpandeableShape(property);
//						} else */if (typeIsAccesible && property.getValue().getType().isEntityType()) {
//							bodyOrmShape = new ExpandeableShape(property);
//						} else if (typeIsAccesible && property.getValue().getType().isCollectionType()) {
//							bodyOrmShape = new ComponentShape(property);
//						} else {
//							bodyOrmShape = new Shape(property);
//						}
						bodyOrmShape = new ExpandeableShape(field);
						getChildren().add(bodyOrmShape);
//					}
				}
			}
		} else if (ormElement instanceof Subclass) {
			RootClass rootClass = ((Subclass)ormElement).getRootClass();

			Property identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				getChildren().add(new Shape(identifierProperty));
			}

			KeyValue identifier = rootClass.getIdentifier();
			if (identifier instanceof Component) {
				Iterator iterator = ((Component)identifier).getPropertyIterator();
				while (iterator.hasNext()) {
					Property property = (Property) iterator.next();
					getChildren().add(new Shape(property));
				}
			}

			Iterator iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = (Property)iterator.next();
				if (!field.isComposite()) {
					if (field.getValue().isSimpleValue()) {
						bodyOrmShape = new Shape(field);
					} else if (field.getValue().getType().isEntityType()) {
						bodyOrmShape = new ExpandeableShape(field);
					} else if (field.getValue().getType().isCollectionType()) {
						bodyOrmShape = new ComponentShape(field);
					} else {
						bodyOrmShape = new Shape(field);
					}
					getChildren().add(bodyOrmShape);
				} else {
//					Component component = (Component)field.getValue();
//					Iterator iter = component.getPropertyIterator();
//					while (iter.hasNext()) {
//						Property property = (Property)iter.next();
//						if (property.getValue().getType().isEntityType()) {
//							bodyOrmShape = new ExpandeableShape(property);
//						} else if (property.getValue().getType().isCollectionType()) {
//							bodyOrmShape = new ComponentShape(property);
//						} else {
//							bodyOrmShape = new Shape(property);
//						}
						bodyOrmShape = new ExpandeableShape(field);
						getChildren().add(bodyOrmShape);
//					}
				}
			}
			Iterator iter = ((Subclass)ormElement).getPropertyIterator();
			while (iter.hasNext()) {
				Property property = (Property)iter.next();
				if (!property.isComposite()) {
					if (property.getValue().getType().isEntityType()) {
						bodyOrmShape = new ExpandeableShape(property);
					} else if (property.getValue().getType().isCollectionType()) {
						bodyOrmShape = new ComponentShape(property);
					} else {
						bodyOrmShape = new Shape(property);
					}
				} else {
					bodyOrmShape = new ExpandeableShape(property);
				}
				getChildren().add(bodyOrmShape);
			}
		} else if (ormElement instanceof Table) {
			Iterator iterator = ((Table)getOrmElement()).getColumnIterator();
			while (iterator.hasNext()) {
				Column column = (Column)iterator.next();
				bodyOrmShape = new Shape(column);
				getChildren().add(bodyOrmShape);
			}
		}
	}
	
	public Shape getChild(Column ormElement) {
		Shape shape = null;
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Shape child = (Shape)iter.next();
			Object childElement = child.getOrmElement();
			if (childElement instanceof Column && ormElement.getName().equals(((Column)childElement).getName())) {
				return child;
			}
		}
		return shape;
	}

	protected void setHidden(boolean hiden) {
		super.setHidden(hiden);
		for (int i = 0; i < getChildren().size(); i++)
			((Shape)getChildren().get(i)).setHidden(hiden);
	}

	public void refreshHiden() {
		hiden = !hiden;
		setElementHidden(this, hiden);
		firePropertyChange(SET_HIDEN, null, new Boolean(hiden));
	}
	
	public void refreshReference(){
		firePropertyChange(SET_HIDEN, null, new Boolean(hiden));
	}
	
	private void setElementHidden(ModelElement element, boolean hidden){
		for (int i = 0; i < element.getChildren().size(); i++){
			((Shape)element.getChildren().get(i)).setHidden(hidden);
			setElementHidden((ModelElement)element.getChildren().get(i), hidden);
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

	public boolean isHiden() {
		return hiden;
	}
}