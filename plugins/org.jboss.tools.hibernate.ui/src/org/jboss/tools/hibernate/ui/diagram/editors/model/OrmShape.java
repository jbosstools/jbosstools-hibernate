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

public class OrmShape extends ExpandeableShape {
	public static final String SET_HIDEN = "set hiden"; //$NON-NLS-1$
	
	public static final String LOCATION_PROP = "OrmShape.Location";		 //$NON-NLS-1$
	private Point location = new Point(0, 0);
	protected boolean hiden = false;

	private DiagramGuide verticalGuide, horizontalGuide;
	
	public OrmShape(Object ioe) {	
		super(ioe);
		generate();
	}
	
	@SuppressWarnings("unchecked")
	protected void generate() {
		Shape bodyOrmShape;
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
						if (field.getValue().isSimpleValue() && !((SimpleValue)field.getValue()).isTypeSpecified()) {
							bodyOrmShape = new Shape(field);
						} else if (typeIsAccessible && field.getValue() instanceof Collection) {
							bodyOrmShape = new ComponentShape(field);
						} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandeableShape(field);
						} else {
							bodyOrmShape = new Shape(field);
						}
						addChild(bodyOrmShape);
					} else {
						bodyOrmShape = new ExpandeableShape(field);
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
						
						if (typeIsAccessible && field.getValue().isSimpleValue()) {
							bodyOrmShape = new Shape(field);
						} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandeableShape(field);
						} else if (typeIsAccessible && field.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(field);
						} else {
							bodyOrmShape = new Shape(field);
						}
						addChild(bodyOrmShape);
					} else {
						bodyOrmShape = new ExpandeableShape(field);
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
						
						if (typeIsAccessible && property.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandeableShape(property);
						} else if (typeIsAccessible && property.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(property);
						} else {
							bodyOrmShape = new Shape(property);
						}
					} else {
						bodyOrmShape = new ExpandeableShape(property);
					}
					addChild(bodyOrmShape);
				}
			}
		} else if (ormElement instanceof Table) {
			Iterator iterator = ((Table)getOrmElement()).getColumnIterator();
			while (iterator.hasNext()) {
				Column column = (Column)iterator.next();
				bodyOrmShape = new Shape(column);
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

	protected void setHidden(boolean hiden) {
		super.setHidden(hiden);
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			child.setHidden(hiden);
		}
	}

	public void refreshHiden() {
		hiden = !hiden;
		setElementHidden(this, hiden);
		firePropertyChange(SET_HIDEN, null, Boolean.valueOf(hiden));
	}
	
	public void refreshReference() {
		firePropertyChange(SET_HIDEN, null, Boolean.valueOf(hiden));
	}
	
	private void setElementHidden(ModelElement element, boolean hidden) {
		Iterator<Shape> it = element.getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			child.setHidden(hidden);
			setElementHidden(child, hidden);
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
}