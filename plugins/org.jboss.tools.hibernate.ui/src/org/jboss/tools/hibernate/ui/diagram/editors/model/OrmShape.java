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
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
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

	private static IPropertyDescriptor[] descriptors_entity;
	private static IPropertyDescriptor[] descriptors_table;

	private static final String ENTITY_isAbstract = "isAbstract"; //$NON-NLS-1$
	private static final String ENTITY_isCustomDeleteCallable = "isCustomDeleteCallable"; //$NON-NLS-1$
	private static final String ENTITY_isCustomInsertCallable = "isCustomInsertCallable"; //$NON-NLS-1$
	private static final String ENTITY_isCustomUpdateCallable = "isCustomUpdateCallable"; //$NON-NLS-1$
	private static final String ENTITY_isDiscriminatorInsertable = "isDiscriminatorInsertable"; //$NON-NLS-1$
	private static final String ENTITY_isDiscriminatorValueNotNull = "isDiscriminatorValueNotNull"; //$NON-NLS-1$
	private static final String ENTITY_isDiscriminatorValueNull = "isDiscriminatorValueNull"; //$NON-NLS-1$
	private static final String ENTITY_isExplicitPolymorphism = "isExplicitPolymorphism"; //$NON-NLS-1$
	private static final String ENTITY_isForceDiscriminator = "isForceDiscriminator"; //$NON-NLS-1$
	private static final String ENTITY_isInherited = "isInherited"; //$NON-NLS-1$
	private static final String ENTITY_isJoinedSubclass = "isJoinedSubclass"; //$NON-NLS-1$
	private static final String ENTITY_isLazy = "isLazy"; //$NON-NLS-1$
	private static final String ENTITY_isLazyPropertiesCacheable = "isLazyPropertiesCacheable"; //$NON-NLS-1$
	private static final String ENTITY_isMutable = "isMutable"; //$NON-NLS-1$
	private static final String ENTITY_isPolymorphic = "isPolymorphic"; //$NON-NLS-1$
	private static final String ENTITY_isVersioned = "isVersioned"; //$NON-NLS-1$
	private static final String ENTITY_batchSize = "batchSize"; //$NON-NLS-1$
	private static final String ENTITY_cacheConcurrencyStrategy = "cacheConcurrencyStrategy"; //$NON-NLS-1$
	private static final String ENTITY_className = "className"; //$NON-NLS-1$
	private static final String ENTITY_customSQLDelete = "customSQLDelete"; //$NON-NLS-1$
	private static final String ENTITY_customSQLInsert = "customSQLInsert"; //$NON-NLS-1$
	private static final String ENTITY_customSQLUpdate = "customSQLUpdate"; //$NON-NLS-1$
	private static final String ENTITY_discriminatorValue = "discriminatorValue"; //$NON-NLS-1$
	private static final String ENTITY_entityName = "entityName"; //$NON-NLS-1$
	private static final String ENTITY_loaderName = "loaderName"; //$NON-NLS-1$
	private static final String ENTITY_optimisticLockMode = "optimisticLockMode"; //$NON-NLS-1$
	private static final String ENTITY_table = "table"; //$NON-NLS-1$
	private static final String ENTITY_temporaryIdTableDDL = "temporaryIdTableDDL"; //$NON-NLS-1$
	private static final String ENTITY_temporaryIdTableName = "temporaryIdTableName"; //$NON-NLS-1$
	private static final String ENTITY_where = "where"; //$NON-NLS-1$
	private static final String ENTITY_cacheRegionName = "cacheRegionName"; //$NON-NLS-1$

	private static final String TABLE_catalog = "catalog"; //$NON-NLS-1$
	private static final String TABLE_comment = "comment"; //$NON-NLS-1$
	private static final String TABLE_name = "name"; //$NON-NLS-1$
	private static final String TABLE_primaryKey = "primaryKey"; //$NON-NLS-1$
	private static final String TABLE_rowId = "rowId"; //$NON-NLS-1$
	private static final String TABLE_schema = "schema"; //$NON-NLS-1$
	private static final String TABLE_subselect = "subselect"; //$NON-NLS-1$
	private static final String TABLE_hasDenormalizedTables = "hasDenormalizedTables"; //$NON-NLS-1$
	private static final String TABLE_isAbstract = "isAbstract"; //$NON-NLS-1$
	private static final String TABLE_isAbstractUnionTable = "isAbstractUnionTable"; //$NON-NLS-1$
	private static final String TABLE_isPhysicalTable = "isPhysicalTable"; //$NON-NLS-1$
	
	static {
		
		descriptors_entity = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(ENTITY_isAbstract, ENTITY_isAbstract),
			new TextPropertyDescriptor(ENTITY_isCustomDeleteCallable, ENTITY_isCustomDeleteCallable),
			new TextPropertyDescriptor(ENTITY_isCustomInsertCallable, ENTITY_isCustomInsertCallable),
			new TextPropertyDescriptor(ENTITY_isCustomUpdateCallable, ENTITY_isCustomUpdateCallable),
			new TextPropertyDescriptor(ENTITY_isDiscriminatorInsertable, ENTITY_isDiscriminatorInsertable),
			new TextPropertyDescriptor(ENTITY_isDiscriminatorValueNotNull, ENTITY_isDiscriminatorValueNotNull),
			new TextPropertyDescriptor(ENTITY_isDiscriminatorValueNull, ENTITY_isDiscriminatorValueNull),
			new TextPropertyDescriptor(ENTITY_isExplicitPolymorphism, ENTITY_isExplicitPolymorphism),
			new TextPropertyDescriptor(ENTITY_isForceDiscriminator, ENTITY_isForceDiscriminator),
			new TextPropertyDescriptor(ENTITY_isInherited, ENTITY_isInherited),
			new TextPropertyDescriptor(ENTITY_isJoinedSubclass, ENTITY_isJoinedSubclass),
			new TextPropertyDescriptor(ENTITY_isLazy, ENTITY_isLazy),
			new TextPropertyDescriptor(ENTITY_isLazyPropertiesCacheable, ENTITY_isLazyPropertiesCacheable),
			new TextPropertyDescriptor(ENTITY_isMutable, ENTITY_isMutable),
			new TextPropertyDescriptor(ENTITY_isPolymorphic, ENTITY_isPolymorphic),
			new TextPropertyDescriptor(ENTITY_isVersioned, ENTITY_isVersioned),
			new TextPropertyDescriptor(ENTITY_batchSize, ENTITY_batchSize),
			new TextPropertyDescriptor(ENTITY_cacheConcurrencyStrategy, ENTITY_cacheConcurrencyStrategy),
			new TextPropertyDescriptor(ENTITY_className, ENTITY_className),
			new TextPropertyDescriptor(ENTITY_customSQLDelete, ENTITY_customSQLDelete),
			new TextPropertyDescriptor(ENTITY_customSQLInsert, ENTITY_customSQLInsert),
			new TextPropertyDescriptor(ENTITY_customSQLUpdate, ENTITY_customSQLUpdate),
			new TextPropertyDescriptor(ENTITY_discriminatorValue, ENTITY_discriminatorValue),
			new TextPropertyDescriptor(ENTITY_entityName, ENTITY_entityName),
			new TextPropertyDescriptor(ENTITY_loaderName, ENTITY_loaderName),
			new TextPropertyDescriptor(ENTITY_optimisticLockMode, ENTITY_optimisticLockMode),
			new TextPropertyDescriptor(ENTITY_table, ENTITY_table),
			new TextPropertyDescriptor(ENTITY_temporaryIdTableDDL, ENTITY_temporaryIdTableDDL),
			new TextPropertyDescriptor(ENTITY_temporaryIdTableName, ENTITY_temporaryIdTableName),
			new TextPropertyDescriptor(ENTITY_where, ENTITY_where),
			new TextPropertyDescriptor(ENTITY_cacheRegionName, ENTITY_cacheRegionName),
		};
		
		descriptors_table = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(TABLE_catalog, TABLE_catalog),
			new TextPropertyDescriptor(TABLE_comment, TABLE_comment),
			new TextPropertyDescriptor(TABLE_name, TABLE_name),
			new TextPropertyDescriptor(TABLE_primaryKey, TABLE_primaryKey),
			new TextPropertyDescriptor(TABLE_rowId, TABLE_rowId),
			new TextPropertyDescriptor(TABLE_schema, TABLE_schema),
			new TextPropertyDescriptor(TABLE_subselect, TABLE_subselect),
			new TextPropertyDescriptor(TABLE_hasDenormalizedTables, TABLE_hasDenormalizedTables),
			new TextPropertyDescriptor(TABLE_isAbstract, TABLE_isAbstract),
			new TextPropertyDescriptor(TABLE_isAbstractUnionTable, TABLE_isAbstractUnionTable),
			new TextPropertyDescriptor(TABLE_isPhysicalTable, TABLE_isPhysicalTable),
		};

	} // static
	
	public OrmShape(Object ioe, String consoleConfigName) {	
		super(ioe, consoleConfigName);
		initModel();
	}
	
	/**
	 * creates children of the shape, 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void initModel() {
		Object ormElement = getOrmElement();
		if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfRootClass()) {
			IPersistentClass rootClass = (IPersistentClass)ormElement;
			IProperty identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				addChild(new Shape(identifierProperty, getConsoleConfigName()));
			}

			IValue identifier = rootClass.getIdentifier();
			if (identifier != null && identifier.isComponent()) {
				IValue component = identifier;
				if (component.isEmbedded()) {
					Iterator<IProperty> iterator = identifier.getPropertyIterator();
					while (iterator.hasNext()) {
						IProperty property = iterator.next();
						addChild(new Shape(property, getConsoleConfigName()));
					}
				}
			}

			Iterator<IProperty> iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				IProperty field = iterator.next();
				if (!field.isBackRef()) {
					if (!field.isComposite()) {
						final IValue val = field.getValue();
						Shape bodyOrmShape = null;
						if (val.isSimpleValue() && val.isTypeSpecified()) {
							bodyOrmShape = new Shape(field, getConsoleConfigName());
						} else {
							if (val.isCollection()) {
								bodyOrmShape = new ComponentShape(field, getConsoleConfigName());
							} else {
								IType type = getTypeUsingExecContext(val);
								if (type != null && type.isEntityType()) {
									bodyOrmShape = new ExpandableShape(field, getConsoleConfigName());
								} else {
									bodyOrmShape = new Shape(field, getConsoleConfigName());
								}
							}
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(field, getConsoleConfigName());
						addChild(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfSubclass()) {
			IPersistentClass rootClass = ((IPersistentClass)ormElement).getRootClass();

			IProperty identifierProperty = rootClass.getIdentifierProperty();
			if (identifierProperty != null) {
				addChild(new Shape(identifierProperty, getConsoleConfigName()));
			}

			IValue identifier = rootClass.getIdentifier();
			if (identifier.isComponent()) {
				Iterator<IProperty> iterator = identifier.getPropertyIterator();
				while (iterator.hasNext()) {
					IProperty property = iterator.next();
					addChild(new Shape(property, getConsoleConfigName()));
				}
			}

			Iterator<IProperty> iterator = rootClass.getPropertyIterator();
			while (iterator.hasNext()) {
				IProperty field = iterator.next();
				if (!field.isBackRef()) {
					if (!field.isComposite()) {

						IValue fieldValue = field.getValue();
						boolean typeIsAccessible = true;
						if (fieldValue.isSimpleValue() && fieldValue.isTypeSpecified()) {
							try {
								field.getValue().getType();
							} catch (Exception e) {
								typeIsAccessible = false;
							}
						}
						Shape bodyOrmShape = null;
						if (typeIsAccessible && field.getValue().isSimpleValue()) {
							bodyOrmShape = new Shape(field, getConsoleConfigName());
						} else if (typeIsAccessible && field.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandableShape(field, getConsoleConfigName());
						} else if (typeIsAccessible && field.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(field, getConsoleConfigName());
						} else {
							bodyOrmShape = new Shape(field, getConsoleConfigName());
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(field, getConsoleConfigName());
						addChild(bodyOrmShape);
					}
				}
			}
			Iterator<IProperty> iter = ((IPersistentClass)ormElement).getPropertyIterator();
			while (iter.hasNext()) {
				IProperty property = iter.next();
				if (!property.isBackRef()) {
					if (!property.isComposite()) {
						
						boolean typeIsAccessible = true;
						IValue propertyValue = property.getValue();
						if (propertyValue.isSimpleValue() && propertyValue.isTypeSpecified()) {
							try {
								property.getValue().getType();
							} catch (Exception e) {
								typeIsAccessible = false;
							}
						}						
						Shape bodyOrmShape = null;
						if (typeIsAccessible && property.getValue().getType().isEntityType()) {
							bodyOrmShape = new ExpandableShape(property, getConsoleConfigName());
						} else if (typeIsAccessible && property.getValue().getType().isCollectionType()) {
							bodyOrmShape = new ComponentShape(property, getConsoleConfigName());
						} else {
							bodyOrmShape = new Shape(property, getConsoleConfigName());
						}
						addChild(bodyOrmShape);
					} else {
						Shape bodyOrmShape = new ExpandableShape(property, getConsoleConfigName());
						addChild(bodyOrmShape);
					}
				}
			}
		} else if (ormElement instanceof ITable) {
			Iterator iterator = ((ITable)getOrmElement()).getColumnIterator();
			while (iterator.hasNext()) {
				IColumn column = (IColumn)iterator.next();
				Shape bodyOrmShape = new Shape(column, getConsoleConfigName());
				addChild(bodyOrmShape);
			}
		}
	}
	
	public Shape getChild(IColumn ormElement) {
		if (ormElement == null) {
			return null;
		}
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			Object childElement = child.getOrmElement();
			if (childElement instanceof IColumn && ormElement.getName().equals(((IColumn)childElement).getName())) {
				return child;
			}
		}
		return null;
	}

	public Shape getChild(IProperty ormElement) {
		if (ormElement == null) {
			return null;
		}
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			final Shape child = it.next();
			Object childElement = child.getOrmElement();
			if (childElement instanceof IProperty && ormElement.getName().equals(((IProperty)childElement).getName())) {
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
	
	protected Point getPoint(IMemento memento, String key) {
		Point point = new Point(0, 0);
		String str = Utils.getPropertyValue(memento, key + ".x", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.x = Integer.parseInt(str);
		String str2 = Utils.getPropertyValue(memento, key + ".y", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.y = Integer.parseInt(str2);
		return point;
	}
	
	protected void setPoint(IMemento memento, String key, Point point) {
		String key1 = key + ".x"; //$NON-NLS-1$
		memento.putString(key1, "" + point.x); //$NON-NLS-1$
		String key2 = key + ".y"; //$NON-NLS-1$
		memento.putString(key2, "" + point.y); //$NON-NLS-1$
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
		}
		properties.put(key1, "" + point.x); //$NON-NLS-1$
		String key2 = key + ".y"; //$NON-NLS-1$
		if (!properties.containsKey(key2)) {
			properties.remove(key2);
		}
		properties.put(key2, "" + point.y); //$NON-NLS-1$
	}
	
	public void setPosition(IMemento memento) {
		Point point = getLocation();
		setPoint(memento, getKey(), point);
	}

	public Point getPosition(IMemento memento) {
		return getPoint(memento, getKey());
	}
	
	public void setPosition(Properties properties) {
		Point point = getLocation();
		setPoint(properties, getKey(), point);
	}

	public Point getPosition(Properties properties) {
		return getPoint(properties, getKey());
	}
	
	@Override
	public void loadState(IMemento memento) {
		super.loadState(memento);
		Point pos = getPosition(memento);
		setLocation(pos);
	}
	
	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		Point pos = getPosition(properties);
		setLocation(pos);
	}

	@Override
	public void saveState(IMemento memento) {
		setPosition(memento);
		super.saveState(memento);
	}

	@Override
	protected void saveInProperties(Properties properties) {
		setPosition(properties);
		super.saveInProperties(properties);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] res = super.getPropertyDescriptors();
		if (res.length > 0) {
			return res;
		}
		Object ormElement = getOrmElement();
		if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfRootClass()) {
			//RootClass rootClass = (RootClass)ormElement;
			res = descriptors_entity;
		} else if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfSubclass()) {
			//RootClass rootClass = ((Subclass)ormElement).getRootClass();
		} else if (ormElement instanceof ITable) {
			//Iterator iterator = ((Table)getOrmElement()).getColumnIterator();
			//while (iterator.hasNext()) {
			//	Column column = (Column)iterator.next();
			//	Shape bodyOrmShape = new Shape(column);
			//	addChild(bodyOrmShape);
			//}
			res = descriptors_table;
		}
		return res;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		Object res = null;
		IPersistentClass rootClass = null;
		ITable table = null;
		Object ormElement = getOrmElement();
		if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfRootClass()) {
			rootClass = (IPersistentClass)ormElement;
		} else if (ormElement instanceof IPersistentClass && ((IPersistentClass)ormElement).isInstanceOfSubclass()) {
			//rootClass = ((Subclass)ormElement).getRootClass();
		} else if (ormElement instanceof ITable) {
			table = (ITable)getOrmElement();
		}
		if (rootClass != null) {
			if (ENTITY_isAbstract.equals(propertyId)) {
				if (rootClass.isAbstract() != null) {
					res = rootClass.isAbstract().toString();
				} 
			} else if (ENTITY_isCustomDeleteCallable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isCustomDeleteCallable()).toString();
			} else if (ENTITY_isCustomInsertCallable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isCustomInsertCallable()).toString();
			} else if (ENTITY_isCustomUpdateCallable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isCustomUpdateCallable()).toString();
			} else if (ENTITY_isDiscriminatorInsertable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isDiscriminatorInsertable()).toString();
			} else if (ENTITY_isDiscriminatorValueNotNull.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isDiscriminatorValueNotNull()).toString();
			} else if (ENTITY_isDiscriminatorValueNull.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isDiscriminatorValueNull()).toString();
			} else if (ENTITY_isExplicitPolymorphism.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isExplicitPolymorphism()).toString();
			} else if (ENTITY_isForceDiscriminator.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isForceDiscriminator()).toString();
			} else if (ENTITY_isInherited.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isInherited()).toString();
			} else if (ENTITY_isJoinedSubclass.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isJoinedSubclass()).toString();
			} else if (ENTITY_isLazy.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isLazy()).toString();
			} else if (ENTITY_isLazyPropertiesCacheable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isLazyPropertiesCacheable()).toString();
			} else if (ENTITY_isMutable.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isMutable()).toString();
			} else if (ENTITY_isPolymorphic.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isPolymorphic()).toString();
			} else if (ENTITY_isVersioned.equals(propertyId)) {
				res = Boolean.valueOf(rootClass.isVersioned()).toString();
			} else if (ENTITY_batchSize.equals(propertyId)) {
				res = Integer.valueOf(rootClass.getBatchSize()).toString();
			} else if (ENTITY_cacheConcurrencyStrategy.equals(propertyId)) {
				res = rootClass.getCacheConcurrencyStrategy();
			} else if (ENTITY_className.equals(propertyId)) {
				res = rootClass.getClassName();
			} else if (ENTITY_customSQLDelete.equals(propertyId)) {
				res = rootClass.getCustomSQLDelete();
			} else if (ENTITY_customSQLInsert.equals(propertyId)) {
				res = rootClass.getCustomSQLInsert();
			} else if (ENTITY_customSQLUpdate.equals(propertyId)) {
				res = rootClass.getCustomSQLUpdate();
			} else if (ENTITY_discriminatorValue.equals(propertyId)) {
				res = rootClass.getDiscriminatorValue();
			} else if (ENTITY_entityName.equals(propertyId)) {
				res = rootClass.getEntityName();
			} else if (ENTITY_loaderName.equals(propertyId)) {
				res = rootClass.getLoaderName();
			} else if (ENTITY_optimisticLockMode.equals(propertyId)) {
				res = Integer.valueOf(rootClass.getOptimisticLockMode()).toString();
			} else if (ENTITY_table.equals(propertyId)) {
				if (rootClass.getTable() != null) {
					res = rootClass.getTable().getName();
				}
			} else if (ENTITY_where.equals(propertyId)) {
				res = rootClass.getWhere();
			}
		}
		if (table != null) {
			if (TABLE_catalog.equals(propertyId)) {
				res = table.getCatalog();
			} else if (TABLE_comment.equals(propertyId)) {
				res = table.getComment();
			} else if (TABLE_name.equals(propertyId)) {
				res = table.getName();
			} else if (TABLE_primaryKey.equals(propertyId)) {
				if (table.getPrimaryKey() != null) {
					res = table.getPrimaryKey().getName();
				}
			} else if (TABLE_rowId.equals(propertyId)) {
				res = table.getRowId();
			} else if (TABLE_schema.equals(propertyId)) {
				res = table.getSchema();
			} else if (TABLE_subselect.equals(propertyId)) {
				res = table.getSubselect();
			} else if (TABLE_hasDenormalizedTables.equals(propertyId)) {
				res = Boolean.valueOf(table.hasDenormalizedTables()).toString();
			} else if (TABLE_isAbstract.equals(propertyId)) {
				res = Boolean.valueOf(table.isAbstract()).toString();
			} else if (TABLE_isAbstractUnionTable.equals(propertyId)) {
				res = Boolean.valueOf(table.isAbstractUnionTable()).toString();
			} else if (TABLE_isPhysicalTable.equals(propertyId)) {
				res = Boolean.valueOf(table.isPhysicalTable()).toString();
			}
		}
		if (res == null) {
			res = super.getPropertyValue(propertyId);
		}
		return toEmptyStr(res);
	}
}