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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.ui.view.views.HibernateUtils;
import org.jboss.tools.hibernate.ui.view.views.OrmModelNameVisitor;

public class Shape extends ModelElement {
	
	private int indent = 0;
		
	private List<Connection> sourceConnections = new ArrayList<Connection>();
	private List<Connection> targetConnections = new ArrayList<Connection>();
	
	public static final String HIDE_SELECTION = "hide selection";
	public static final String SHOW_SELECTION = "show selection";
	public static final String SET_FOCUS = "set focus";
	
	private Object  ormElement;
	
	static OrmModelNameVisitor ormModelNameVisitor;
		
	private static IPropertyDescriptor[] descriptors_property;
	private static IPropertyDescriptor[] descriptors_column;

	/**
	 * Property set
	 */
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_TYPE = "type";
	private static final String PROPERTY_CLASS = "persistanceClass";
	private static final String PROPERTY_VALUE = "value";
	private static final String PROPERTY_SELECT = "selectable";
	private static final String PROPERTY_INSERT = "insertable";
	private static final String PROPERTY_UPDATE = "updateable";
	private static final String PROPERTY_CASCADE = "cascade";
	private static final String PROPERTY_LAZY = "lazy";
	private static final String PROPERTY_OPTIONAL = "optional";
	private static final String PROPERTY_NATURAL_IDENTIFIER = "naturalIdentifier";
	private static final String PROPERTY_NODE_NAME = "nodeName";
	private static final String PROPERTY_OPTIMISTIC_LOCKED = "optimisticLocked";
	private static final String PROPERTY_NULLABLE = "nullable";
	private static final String PROPERTY_UNIQUE = "unique";

	static {
		
		ormModelNameVisitor = new OrmModelNameVisitor();
		
		descriptors_property = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(PROPERTY_NAME, PROPERTY_NAME),
				new TextPropertyDescriptor(PROPERTY_TYPE, PROPERTY_TYPE),
				new TextPropertyDescriptor(PROPERTY_VALUE, PROPERTY_VALUE),
				new TextPropertyDescriptor(PROPERTY_CLASS, PROPERTY_CLASS),
				new TextPropertyDescriptor(PROPERTY_SELECT, PROPERTY_SELECT),
				new TextPropertyDescriptor(PROPERTY_INSERT, PROPERTY_INSERT),
				new TextPropertyDescriptor(PROPERTY_UPDATE, PROPERTY_UPDATE),
				new TextPropertyDescriptor(PROPERTY_CASCADE, PROPERTY_CASCADE),
				new TextPropertyDescriptor(PROPERTY_LAZY, PROPERTY_LAZY),
				new TextPropertyDescriptor(PROPERTY_OPTIONAL, PROPERTY_OPTIONAL),
				new TextPropertyDescriptor(PROPERTY_NATURAL_IDENTIFIER, PROPERTY_NATURAL_IDENTIFIER),
				new TextPropertyDescriptor(PROPERTY_NODE_NAME, PROPERTY_NODE_NAME),
				new TextPropertyDescriptor(PROPERTY_OPTIMISTIC_LOCKED, PROPERTY_OPTIMISTIC_LOCKED),
		};

	
		descriptors_column = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(PROPERTY_NAME, PROPERTY_NAME),
				new TextPropertyDescriptor(PROPERTY_TYPE, PROPERTY_TYPE),
				new TextPropertyDescriptor(PROPERTY_VALUE, PROPERTY_VALUE),
				new TextPropertyDescriptor(PROPERTY_NULLABLE, PROPERTY_NULLABLE),
				new TextPropertyDescriptor(PROPERTY_UNIQUE, PROPERTY_UNIQUE),
		};

	} // static

	protected Shape(Object ioe) {
		ormElement = ioe;
	}

	public void addConnection(Connection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.add(conn);
		} else if (conn.getTarget() == this) {
			targetConnections.add(conn);
		}
	}
	
	
	public List<Connection> getSourceConnections() {
		return sourceConnections;
	}
	
	public List<Connection> getTargetConnections() {
		return targetConnections;
	}
	
	public Object getOrmElement() {
		return ormElement;
	}
	
	public void hideSelection() {
		firePropertyChange(HIDE_SELECTION, null, null);
	}

	public void showSelection() {
		firePropertyChange(SHOW_SELECTION, null, null);
	}

	public void setFocus() {
		firePropertyChange(SET_FOCUS, null, null);		
	}
	
	public int getIndent() {
		return indent;
	}

	protected void setIndent(int indent) {
		this.indent = indent;
	}
	
	protected void setHidden(boolean hiden) {
		for (int i = 0; i < sourceConnections.size(); i++)
			((Connection)sourceConnections.get(i)).setHidden(hiden);
		for (int i = 0; i < targetConnections.size(); i++)
			((Connection)targetConnections.get(i)).setHidden(hiden);
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>The returned array is used to fill the property view, when the edit-part corresponding
	 * to this model element is selected.</p>
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (getOrmElement() instanceof Property) {
			return descriptors_property;
		}
		else if (getOrmElement() instanceof Column) {
			return descriptors_column;
		}
		return super.getPropertyDescriptors();
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array 
	 * to obtain the value of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (PROPERTY_NAME.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return ((Property) getOrmElement()).getName();
			}
			else if (getOrmElement() instanceof Column) {
				return ((Column) getOrmElement()).getName();
			}
		}
		else if (PROPERTY_TYPE.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				Value value = ((Property) getOrmElement()).getValue();
				if (value instanceof Component) return ((Property) getOrmElement()).getValue().toString();
				return ((Property) getOrmElement()).getType().getReturnedClass().getName();
			}
			else if (getOrmElement() instanceof Column) {
				String type = ormModelNameVisitor.getColumnSqlType((Column) getOrmElement(), getOrmDiagram().getConfiguration());
				Column column = (Column) getOrmElement();

				StringBuffer name = new StringBuffer();

				if (type != null) {
					name.append(type != null ? type.toUpperCase() : "");
					name.append(HibernateUtils.getTable(column) != null
							&& HibernateUtils.isPrimaryKey(column) ? " PK" : "");
					name.append(HibernateUtils.getTable(column) != null
							&& HibernateUtils.isForeignKey(column) ? " FK" : "");
				}

				return name.toString();
			}
		}
		else if (PROPERTY_VALUE.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return ((Property) getOrmElement()).getValue().toString();
			}
			else if (getOrmElement() instanceof Column) {
				return ((Column) getOrmElement()).getValue().toString();
			}
		}
		else if (PROPERTY_CLASS.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return ((Property) getOrmElement()).getPersistentClass().getClassName();
			}
		}
		else if (PROPERTY_SELECT.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isSelectable()).toString(); 
			}
		}
		else if (PROPERTY_INSERT.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isInsertable()).toString(); 
			}
		}
		else if (PROPERTY_UPDATE.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isUpdateable()).toString(); 
			}
		}
		else if (PROPERTY_CASCADE.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return ((Property) getOrmElement()).getCascade(); 
			}
		}
		else if (PROPERTY_LAZY.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isLazy()).toString(); 
			}
		}
		else if (PROPERTY_OPTIONAL.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isOptional()).toString(); 
			}
		}
		else if (PROPERTY_NATURAL_IDENTIFIER.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isNaturalIdentifier()).toString(); 
			}
		}
		else if (PROPERTY_NODE_NAME.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return ((Property) getOrmElement()).getNodeName();
			}
		}
		else if (PROPERTY_OPTIMISTIC_LOCKED.equals(propertyId)) {
			if (getOrmElement() instanceof Property) {
				return Boolean.valueOf(((Property) getOrmElement()).isOptimisticLocked()).toString(); 
			}
		}
		else if (PROPERTY_NULLABLE.equals(propertyId)) {
			if (getOrmElement() instanceof Column) {
				return Boolean.valueOf(((Column) getOrmElement()).isNullable()).toString(); 
			}
		}
		else if (PROPERTY_UNIQUE.equals(propertyId)) {
			if (getOrmElement() instanceof Column) {
				return Boolean.valueOf(((Column) getOrmElement()).isUnique()).toString(); 
			}
		}
		return super.getPropertyValue(propertyId);
	}

}
