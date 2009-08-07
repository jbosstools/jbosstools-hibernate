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
package org.jboss.tools.hibernate.ui.diagram.editors.model;

import java.util.Iterator;
import java.util.Properties;

import org.eclipse.draw2d.geometry.Point;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramGuide;

/**
 * Only OrmShape has it's own location on Diagram.
 * 
 * @author some modifications from Vitali
 */
public class OrmShape extends ExpandableShape {

	public static final String LOCATION_PROP = "location";		 //$NON-NLS-1$
	/**
	 * up-left point element location on diagram
	 */
	private Point location = new Point(0, 0);
	/**
	 * vertical and horizontal guide attached to OrmShape, 
	 * additional way to change shape location on diagram
	 */
	private DiagramGuide verticalGuide, horizontalGuide;
	
	public OrmShape(Object ioe) {	
		super(ioe);
		initModel();
	}
	
	/**
	 * creates children of the shape, 
	 */
	@SuppressWarnings("unchecked")
	protected void initModel() {
		Object ormElement = getOrmElement();
		if (ormElement instanceof RootClass) {
			RootClass rootClass = (RootClass)ormElement;
			Property identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				addChild(new Shape(identifierProperty));
			}

			KeyValue identifier = rootClass.getIdentifier();
			if (identifier instanceof Component) {
				Component component = (Component)identifier;
				if (component.isEmbedded()) {
					Iterator<Property> iterator = ((Component)identifier).getPropertyIterator();
					while (iterator.hasNext()) {
						Property property = iterator.next();
						addChild(new Shape(property));
					}
				}
			}

			Iterator<Property> iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = iterator.next();
				if (!field.isBackRef()) {
					if (!field.isComposite()) {
						boolean typeIsAccessible = true;
						if (field.getValue().isSimpleValue() && ((SimpleValue)field.getValue()).isTypeSpecified()) {
							try {
								field.getValue().getType();
							} catch (Exception e) {
								typeIsAccessible = false;
							}
						}
						Shape bodyOrmShape = null;
						if (field.getValue().isSimpleValue() && !((SimpleValue)field.getValue()).isTypeSpecified()) {
							bodyOrmShape = new Shape(field);
						} else if (typeIsAccessible && field.getValue() instanceof Collection) {
							bodyOrmShape = new ComponentShape(field);
						} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandableShape(field);
						} else {
							bodyOrmShape = new Shape(field);
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(field);
						addChild(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof Subclass) {
			RootClass rootClass = ((Subclass)ormElement).getRootClass();

			Property identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				addChild(new Shape(identifierProperty));
			}

			KeyValue identifier = rootClass.getIdentifier();
			if (identifier instanceof Component) {
				Iterator<Property> iterator = ((Component)identifier).getPropertyIterator();
				while (iterator.hasNext()) {
					Property property = iterator.next();
					addChild(new Shape(property));
				}
			}

			Iterator<Property> iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				Property field = iterator.next();
				if (!field.isBackRef()) {
					if (!field.isComposite()) {

						boolean typeIsAccessible = true;
						if (field.getValue().isSimpleValue() && ((SimpleValue)field.getValue()).isTypeSpecified()) {
							try {
								field.getValue().getType();
							} catch (Exception e) {
								typeIsAccessible = false;
							}
						}
						Shape bodyOrmShape = null;
						if (typeIsAccessible && field.getValue().isSimpleValue()) {
							bodyOrmShape = new Shape(field);
						} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandableShape(field);
						} else if (typeIsAccessible && field.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(field);
						} else {
							bodyOrmShape = new Shape(field);
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(field);
						addChild(bodyOrmShape);
					}
				}
			}
			Iterator<Property> iter = ((Subclass)ormElement).getPropertyIterator();
			while (iter.hasNext()) {
				Property property = iter.next();
				if (!property.isBackRef()) {
					if (!property.isComposite()) {
						
						boolean typeIsAccessible = true;
						if (property.getValue().isSimpleValue() && ((SimpleValue)property.getValue()).isTypeSpecified()) {
							try {
								property.getValue().getType();
							} catch (Exception e) {
								typeIsAccessible = false;
							}
						}						
						Shape bodyOrmShape = null;
						if (typeIsAccessible && property.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandableShape(property);
						} else if (typeIsAccessible && property.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(property);
						} else {
							bodyOrmShape = new Shape(property);
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(property);
						addChild(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof Table) {
			Iterator iterator = ((Table)getOrmElement()).getColumnIterator();
			while (iterator.hasNext()) {
				Column column = (Column)iterator.next();
				Shape bodyOrmShape = new Shape(column);
				addChild(bodyOrmShape);
			}
		}
	}
	
	public Shape getChild(Column ormElement) {
		if (ormElement == null) {
			return null;
		}
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			Object childElement = child.getOrmElement();
			if (childElement instanceof Column && ormElement.getName().equals(((Column)childElement).getName())) {
				return child;
			}
		}
		return null;
	}

	public Shape getChild(Property ormElement) {
		if (ormElement == null) {
			return null;
		}
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			Object childElement = child.getOrmElement();
			if (childElement instanceof Property && ormElement.getName().equals(((Property)childElement).getName())) {
				return child;
			}
		}
		return null;
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

	public DiagramGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	public void setHorizontalGuide(DiagramGuide hGuide) {
		horizontalGuide = hGuide;
	}

	public DiagramGuide getVerticalGuide() {
		return verticalGuide;
	}

	public void setVerticalGuide(DiagramGuide vGuide) {
		verticalGuide = vGuide;
	}
	
	protected Point getPoint(Properties properties, String key) {
		Point point = new Point(0, 0);
		String str = properties.getProperty(key + ".x", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.x = Integer.parseInt(str);
		String str2 = properties.getProperty(key + ".y", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.y = Integer.parseInt(str2);
		return point;
	}
	
	protected void setPoint(Properties properties, String key, Point point) {
		String key1 = key + ".x"; //$NON-NLS-1$
		if (!properties.containsKey(key1)) {
			properties.remove(key1);
			properties.put(key1, "" + point.x); //$NON-NLS-1$
		} else {
			properties.put(key1, "" + point.x); //$NON-NLS-1$
		}
		String key2 = key + ".y"; //$NON-NLS-1$
		if (!properties.containsKey(key2)) {
			properties.remove(key2);
			properties.put(key2, "" + point.y); //$NON-NLS-1$
		} else {
			properties.put(key2, "" + point.y); //$NON-NLS-1$
		}
	}
	
	public void setPosition(Properties properties) {
		Point point = getLocation();
		setPoint(properties, getKey(), point);
	}

	public Point getPosition(Properties properties) {
		return getPoint(properties, getKey());
	}
	
	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		Point pos = getPosition(properties);
		setLocation(pos);
	}

	@Override
	protected void saveInProperties(Properties properties) {
		setPosition(properties);
		super.saveInProperties(properties);
	}
}