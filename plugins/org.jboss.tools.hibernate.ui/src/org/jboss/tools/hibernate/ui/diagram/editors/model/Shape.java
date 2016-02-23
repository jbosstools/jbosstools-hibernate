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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection.ConnectionType;
import org.jboss.tools.hibernate.ui.view.HibernateUtils;

/**
 * Shape which represents particular Hibernate element on the diagram.
 * 
 * @author some modifications from Vitali
 */
public class Shape extends BaseElement {
	
	public static final String SET_FOCUS = "setFocus"; //$NON-NLS-1$
	public static final String IDENT = "indent"; //$NON-NLS-1$

	/**
	 * left indent for property string on diagram
	 */
	private int indent = 0;
	/**
	 * source and target connections to connect shapes with directed edges.
	 */
	private List<Connection> sourceConnections = new ArrayList<Connection>();
	private List<Connection> targetConnections = new ArrayList<Connection>();
	/**
	 * orm element which is behind the shape
	 */
	private Object ormElement;
	/**
	 * only one BaseElement offspring has a parent - this is Shape
	 */
	private BaseElement parent;
	/**
	 * name of Hibernate Console Configuration
	 */
	private final String consoleConfigName;
	
	private static IPropertyDescriptor[] descriptors_property;
	private static IPropertyDescriptor[] descriptors_column;

	/**
	 * Hibernate properties set
	 */
	private static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "type"; //$NON-NLS-1$
	private static final String PROPERTY_CLASS = "persistanceClass"; //$NON-NLS-1$
	private static final String PROPERTY_VALUE = "value"; //$NON-NLS-1$
	private static final String PROPERTY_SELECT = "selectable"; //$NON-NLS-1$
	private static final String PROPERTY_INSERT = "insertable"; //$NON-NLS-1$
	private static final String PROPERTY_UPDATE = "updateable"; //$NON-NLS-1$
	private static final String PROPERTY_CASCADE = "cascade"; //$NON-NLS-1$
	private static final String PROPERTY_LAZY = "lazy"; //$NON-NLS-1$
	private static final String PROPERTY_OPTIONAL = "optional"; //$NON-NLS-1$
	private static final String PROPERTY_NATURAL_IDENTIFIER = "naturalIdentifier"; //$NON-NLS-1$
	private static final String PROPERTY_OPTIMISTIC_LOCKED = "optimisticLocked"; //$NON-NLS-1$
	private static final String PROPERTY_NULLABLE = "nullable"; //$NON-NLS-1$
	private static final String PROPERTY_UNIQUE = "unique"; //$NON-NLS-1$

	static {
		
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

	protected Shape(Object ioe, String consoleConfigName) {
		ormElement = ioe;
		this.consoleConfigName = consoleConfigName;
	}
	
	@Override
	public BaseElement getParent() {
		return parent;
	}
	
	public void setParent(BaseElement element) {
		parent = element;
	}
	
	public OrmDiagram getOrmDiagram() {
		OrmDiagram res = null;
		BaseElement el = this;
		while (el != null) {
			if (el instanceof OrmDiagram) {
				res = (OrmDiagram)el;
				break;
			}
			el = el.getParent();
		}
		return res;
	}

	public String getConsoleConfigName() {
		return consoleConfigName;
	}

	public void addConnection(Connection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this && !sourceConnections.contains(conn)) {
			sourceConnections.add(conn);
		} else if (conn.getTarget() == this && !targetConnections.contains(conn)) {
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

	@Override
	public String getKey() {
		//fix for https://jira.jboss.org/jira/browse/JBIDE-6186
		return Utils.getName(getOrmElement()).replaceAll("\\$", "."); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	@Override
	public void updateVisibleValue(boolean initState) {
		super.updateVisibleValue(initState);
		// update connections visibility state
		for (Connection connection : sourceConnections) {
			boolean state = true;
			ConnectionType ct = connection.getConnectionType();
			switch (ct) {
			case ClassMapping:
				state = getOrmDiagram().getConnectionsVisibilityClassMapping();
				break;
			case PropertyMapping:
				state = getOrmDiagram().getConnectionsVisibilityPropertyMapping();
				break;
			case Association:
				state = getOrmDiagram().getConnectionsVisibilityAssociation();
				break;
			case ForeignKeyConstraint:
				state = getOrmDiagram().getConnectionsVisibilityForeignKeyConstraint();
				break;
			}
			connection.updateVisibleValue(state);
		}
		for (Connection connection : targetConnections) {
			boolean state = true;
			ConnectionType ct = connection.getConnectionType();
			switch (ct) {
			case ClassMapping:
				state = getOrmDiagram().getConnectionsVisibilityClassMapping();
				break;
			case PropertyMapping:
				state = getOrmDiagram().getConnectionsVisibilityPropertyMapping();
				break;
			case Association:
				state = getOrmDiagram().getConnectionsVisibilityAssociation();
				break;
			case ForeignKeyConstraint:
				state = getOrmDiagram().getConnectionsVisibilityForeignKeyConstraint();
				break;
			}
			connection.updateVisibleValue(state);
		}
	}
	
	@Override
	public void loadState(IMemento memento) {
		super.loadState(memento);
		int indentTmp = getPrValue(memento, IDENT, 0);
		indent = indentTmp;
	}
	
	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		int indentTmp = getPrValue(properties, IDENT, 0);
		indent = indentTmp;
	}

	@Override
	public void saveState(IMemento memento) {
		setPrValue(memento, IDENT, indent);
		super.saveState(memento);
	}

	@Override
	protected void saveInProperties(Properties properties) {
		setPrValue(properties, IDENT, indent);
		super.saveInProperties(properties);
	}

	public String toString() {
		if (ormElement == null) {
			return super.toString();
		}
		return ormElement.toString();
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>The returned array is used to fill the property view, when the edit-part corresponding
	 * to this model element is selected.</p>
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (getOrmElement() instanceof IProperty) {
			return descriptors_property;
		} else if (getOrmElement() instanceof IColumn) {
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
	@Override
	public Object getPropertyValue(Object propertyId) {
		Object res = null;
		IColumn col = null;
		if (getOrmElement() instanceof IColumn) {
			col = (IColumn)getOrmElement();
		}
		IProperty prop = null;
		if (getOrmElement() instanceof IProperty) {
			prop = (IProperty)getOrmElement();
		}
		if (prop != null) {
			if (PROPERTY_NAME.equals(propertyId)) {
				res = prop.getName();
			} else if (PROPERTY_TYPE.equals(propertyId)) {
				IValue value = prop.getValue();
				if (value.isComponent()) {
					res = value.toString();
				} else {
					IType type = getTypeUsingExecContext(value);
					if (type != null) {
						res = type.getAssociatedEntityName();
					}
				}
			} else if (PROPERTY_VALUE.equals(propertyId)) {
				res = prop.getValue().toString();
			} else if (PROPERTY_CLASS.equals(propertyId)) {
				if (prop.getPersistentClass() != null) {
					res = prop.getPersistentClass().getClassName();
				}
			} else if (PROPERTY_SELECT.equals(propertyId)) {
				res = Boolean.valueOf(prop.isSelectable()).toString(); 
			} else if (PROPERTY_INSERT.equals(propertyId)) {
				res = Boolean.valueOf(prop.isInsertable()).toString(); 
			} else if (PROPERTY_UPDATE.equals(propertyId)) {
				res = Boolean.valueOf(prop.isUpdateable()).toString(); 
			} else if (PROPERTY_CASCADE.equals(propertyId)) {
				res = prop.getCascade(); 
			} else if (PROPERTY_LAZY.equals(propertyId)) {
				res = Boolean.valueOf(prop.isLazy()).toString(); 
			} else if (PROPERTY_OPTIONAL.equals(propertyId)) {
				res = Boolean.valueOf(prop.isOptional()).toString(); 
			} else if (PROPERTY_NATURAL_IDENTIFIER.equals(propertyId)) {
				res = Boolean.valueOf(prop.isNaturalIdentifier()).toString(); 
			} else if (PROPERTY_OPTIMISTIC_LOCKED.equals(propertyId)) {
				res = Boolean.valueOf(prop.isOptimisticLocked()).toString(); 
			}
		} else if (col != null) {
			if (PROPERTY_NAME.equals(propertyId)) {
				res = col.getName();
			} else if (PROPERTY_TYPE.equals(propertyId)) {
				String sqlType = col.getSqlType();
				if (sqlType == null) {
					getOrmDiagram().getLabelProvider().updateColumnSqlType(col);
					sqlType = col.getSqlType();
				}
				StringBuffer name = new StringBuffer();
				if (sqlType != null) {
					name.append(sqlType.toUpperCase());
					name.append(HibernateUtils.getTable(col) != null
							&& HibernateUtils.isPrimaryKey(col) ? " PK" : ""); //$NON-NLS-1$  //$NON-NLS-2$
					name.append(HibernateUtils.getTable(col) != null
							&& HibernateUtils.isForeignKey(col) ? " FK" : ""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				res = name.toString();
			} else if (PROPERTY_VALUE.equals(propertyId)) {
				res = col.getValue().toString();
			} else if (PROPERTY_NULLABLE.equals(propertyId)) {
				res = Boolean.valueOf(col.isNullable()).toString(); 
			} else if (PROPERTY_UNIQUE.equals(propertyId)) {
				res = Boolean.valueOf(col.isUnique()).toString(); 
			}
		}
		if (res == null) {
			res = super.getPropertyValue(propertyId);
		}
		return toEmptyStr(res);
	}

	public ConsoleConfiguration getConsoleConfig() {
		final KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		ConsoleConfiguration consoleConfig = knownConfigurations.find(consoleConfigName);
		return consoleConfig;
	}

	public IType getTypeUsingExecContext(final IValue val) {
		return UtilTypeExtract.getTypeUsingExecContext(val, getConsoleConfig());
	}
}
