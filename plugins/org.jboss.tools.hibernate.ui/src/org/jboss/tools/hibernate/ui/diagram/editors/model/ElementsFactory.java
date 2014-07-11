/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.proxy.ColumnProxy;
import org.jboss.tools.hibernate.proxy.PersistentClassProxy;
import org.jboss.tools.hibernate.proxy.TableProxy;
import org.jboss.tools.hibernate.proxy.ValueProxy;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IJoin;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.util.HibernateHelper;

/**
 * Responsible to create diagram elements for given
 * Hibernate Console Configuration.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ElementsFactory {

	private final String consoleConfigName;
	private final HashMap<String, OrmShape> elements;
	private final ArrayList<Connection> connections;
	private IService service;
	
	public ElementsFactory(String consoleConfigName, HashMap<String, OrmShape> elements,
			ArrayList<Connection> connections) {
		this.consoleConfigName = consoleConfigName;
		this.elements = elements;
		this.connections = connections;
		this.service = HibernateHelper.INSTANCE.getHibernateService();
	}
	
	@SuppressWarnings("unchecked")
	public void createForeingKeyConnections() {
		// do clone cause elements could be changed during iteration!
		HashMap<String, OrmShape> elementsTmp = (HashMap<String, OrmShape>)elements.clone();
		Iterator<OrmShape> it = elementsTmp.values().iterator();
		while (it.hasNext()) {
			final OrmShape shape = it.next();
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof ITable) {
				ITable databaseTable = (ITable)ormElement;
				Iterator<ForeignKey> itFK = (Iterator<ForeignKey>)databaseTable.getForeignKeyIterator();
				while (itFK.hasNext()) {
					final ForeignKey fk = itFK.next();
					ITable referencedTable = fk.getReferencedTable() != null ? new TableProxy(fk.getReferencedTable()) : null;
					final OrmShape referencedShape = getOrCreateDatabaseTable(referencedTable);
					//
					Iterator itColumns = fk.columnIterator();
					while (itColumns.hasNext()) {
						IColumn col = new ColumnProxy(itColumns.next());
						Shape shapeColumn = shape.getChild(col);
						Iterator<IColumn> itReferencedColumns = null;
						if (fk.isReferenceToPrimaryKey()) {
							itReferencedColumns = 
								(Iterator<IColumn>)referencedTable.getPrimaryKey().columnIterator();
						} else {
							itReferencedColumns = 
								(Iterator<IColumn>)fk.getReferencedColumns().iterator();
						}
						while (itReferencedColumns != null && itReferencedColumns.hasNext()) {
							IColumn colReferenced = new ColumnProxy(itReferencedColumns.next());
							Shape shapeReferencedColumn = referencedShape.getChild(colReferenced);
							if (shouldCreateConnection(shapeColumn, shapeReferencedColumn)) {
								connections.add(new Connection(shapeColumn, shapeReferencedColumn));
							}
						}
					}
				}
			}
		}
	}
	
	public void createChildren(BaseElement element) {
		if (element.getClass().equals(ExpandableShape.class)) {
			processExpand((ExpandableShape)element);
		} else if (element.getClass().equals(ComponentShape.class)) {
			refreshComponentReferences((ComponentShape)element);
		}
		Iterator<Shape> it = element.getChildrenList().iterator();
		while (it.hasNext()) {
			final Shape shape = it.next();
			createChildren(shape);
		}
	}
	
	protected void processExpand(ExpandableShape shape) {
		Object element = shape.getOrmElement();
		if (!(element instanceof Property)) {
			return;
		}
		OrmShape s = null;
		Property property = (Property)element;
		if (!property.isComposite()) {
			final IConfiguration config = getConfig();
			//
			IValue v = property.getValue() != null ? new ValueProxy(property.getValue()) : null;
			IType type = UtilTypeExtract.getTypeUsingExecContext(v, getConsoleConfig());
			if (type != null && type.isEntityType()) {
				Object clazz = config != null ? 
						config.getClassMapping(type.getAssociatedEntityName()) : null;
				if (clazz instanceof IPersistentClass) {
					clazz = ((PersistentClassProxy)clazz).getTarget();
				}
				if (clazz instanceof IPersistentClass && ((IPersistentClass)clazz).isInstanceOfRootClass()) {
					IPersistentClass rootClass = (IPersistentClass)clazz;
					s = getOrCreatePersistentClass(rootClass, null);
					if (shouldCreateConnection(shape, s)) {
						connections.add(new Connection(shape, s));
					}
				} else if (clazz instanceof IPersistentClass && ((IPersistentClass)clazz).isInstanceOfSubclass()) {
					s = getOrCreatePersistentClass(((IPersistentClass)clazz).getRootClass(), null);
				}
			}
		} else {
			s = getOrCreatePersistentClass(service.newSpecialRootClass(property), null);
			if (shouldCreateConnection(shape, s)) {
				connections.add(new Connection(shape, s));
			}
			createConnections(s, getOrCreateDatabaseTable(property.getValue().getTable() != null ? new TableProxy(property.getValue().getTable()) : null));
		}
	}

	protected void refreshComponentReferences(ComponentShape componentShape) {
		Property property = (Property)componentShape.getOrmElement();
		IValue v = (new ValueProxy(property.getValue()));
		if (!v.isCollection()) {
			return;
		}
		IValue collection = v;
		IValue component = collection.getElement();
		Shape csChild0 = null, csChild1 = null;
		Iterator<Shape> tmp = componentShape.getChildrenIterator();
		if (tmp.hasNext()) {
			csChild0 = tmp.next();
		}
		if (tmp.hasNext()) {
			csChild1 = tmp.next();
		}
		OrmShape childShape = null;
		if (component.isComponent()) {
			childShape = elements.get(component.getComponentClassName());
			if (childShape == null) {
				childShape = getOrCreateComponentClass(property);
			}
			IValue value = (IValue)csChild0.getOrmElement();
			OrmShape tableShape = getOrCreateDatabaseTable(value.getTable());
			if (tableShape != null) {
				Iterator<IColumn> it = value.getColumnIterator();
				while (it.hasNext()) {
					IColumn el = it.next();
					Shape shape = tableShape.getChild(el);
					if (shouldCreateConnection(csChild0, shape)) {
						connections.add(new Connection(csChild0, shape));
					}
				}
			}
			if (shouldCreateConnection(csChild1, childShape)) {
				connections.add(new Connection(csChild1, childShape));
			}
			
		} else if (collection.isOneToMany()) {
			childShape = getOrCreateAssociationClass(property);
			if (childShape != null) {
				if (shouldCreateConnection(csChild1, childShape)) {
					connections.add(new Connection(csChild1, childShape));
				}
				OrmShape keyTableShape = getOrCreateDatabaseTable(collection.getKey().getTable());
				Iterator it = collection.getKey().getColumnIterator();
				while (it.hasNext()) {
					Object el = it.next();
					if (el instanceof IColumn) {
						IColumn col = (IColumn)el;
						Shape shape = keyTableShape.getChild(col);
						if (shouldCreateConnection(csChild0, shape)) {
							connections.add(new Connection(csChild0, shape));
						}
					}
				}
			}

		} else {
			// this is case: if (collection.isMap() || collection.isSet())
			childShape = getOrCreateDatabaseTable(collection.getCollectionTable());
			if (childShape != null) {
				Iterator<IColumn> it = ((IValue)csChild0.getOrmElement()).getColumnIterator();
				while (it.hasNext()) {
					Object el = it.next();
					if (el instanceof IColumn) {
						IColumn col = (IColumn)el;
						Shape shape = childShape.getChild(col);
						if (shouldCreateConnection(csChild0, shape)) {
							connections.add(new Connection(csChild0, shape));
						}
					}
				}
				it = ((IValue)csChild1.getOrmElement()).getColumnIterator();
				while (it.hasNext()) {
					Object el = it.next();
					if (el instanceof IColumn) {
						IColumn col = (IColumn)el;
						Shape shape = childShape.getChild(col);
						if (shouldCreateConnection(csChild1, shape)) {
							connections.add(new Connection(csChild1, shape));
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected OrmShape getOrCreateDatabaseTable(ITable databaseTable) {
		OrmShape tableShape = null;
		if (databaseTable != null) {
			tableShape = getShape(databaseTable);
			if (tableShape == null) {
				tableShape = createShape(databaseTable);
				final IConfiguration config = getConfig();
				if (config != null) {
					Iterator iterator = config.getClassMappings();
					while (iterator.hasNext()) {
						Object clazz = iterator.next();
						if (clazz instanceof IPersistentClass) {
							clazz = ((PersistentClassProxy)clazz).getTarget();
						}
						if (clazz instanceof IPersistentClass && ((IPersistentClass)clazz).isInstanceOfRootClass()) {
							IPersistentClass cls = (IPersistentClass)clazz;
							if (databaseTable.equals(cls.getTable())) {
								// create persistent class shape only for RootClass,
								// which has same table reference
								getOrCreatePersistentClass(cls, null);
							}
						}
					}
				}
			}			
		}
		return tableShape;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected OrmShape getOrCreatePersistentClass(IPersistentClass persistentClass, 
			ITable componentClassDatabaseTable) {
		OrmShape classShape = null;
		if (persistentClass == null) {
			return classShape;
		}
		OrmShape shape = null;
		classShape = getShape(persistentClass.getEntityName());
		if (classShape == null) {
			classShape = createShape(persistentClass);
		}
		if (componentClassDatabaseTable == null && persistentClass.getTable() != null) {
			componentClassDatabaseTable = persistentClass.getTable();
		}
		if (componentClassDatabaseTable != null) {
			shape = getShape(componentClassDatabaseTable);
			if (shape == null) {
				shape = getOrCreateDatabaseTable(componentClassDatabaseTable);
			}
			createConnections(classShape, shape);
			if (shouldCreateConnection(classShape, shape)) {
				connections.add(new Connection(classShape, shape));
			}
		}
		IPersistentClass rc = persistentClass;
		Iterator iter = rc.getSubclassIterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof IPersistentClass && ((IPersistentClass)element).isInstanceOfSubclass()) {
				IPersistentClass subclass = (IPersistentClass)element;
				OrmShape subclassShape = getShape(subclass);
				if (subclassShape == null) {
					subclassShape = createShape(subclass);
				}
				if (((IPersistentClass)element).isJoinedSubclass()) {
					ITable jcTable = ((IPersistentClass)element).getTable();
					OrmShape jcTableShape = getOrCreateDatabaseTable(jcTable);
					createConnections(subclassShape, jcTableShape);
					if (shouldCreateConnection(subclassShape, jcTableShape)) {
						connections.add(new Connection(subclassShape, jcTableShape));
					}
				} else {
					createConnections(subclassShape, shape);
					if (shouldCreateConnection(subclassShape, shape)) {
						connections.add(new Connection(subclassShape, shape));
					}
				}
				OrmShape ownerTableShape = getOrCreateDatabaseTable(((IPersistentClass)element).getRootTable());
				createConnections(subclassShape, ownerTableShape);

				Iterator<IJoin> joinIterator = subclass.getJoinIterator();
				while (joinIterator.hasNext()) {
					IJoin join = joinIterator.next();
					Iterator<Property> iterator = join.getPropertyIterator();
					while (iterator.hasNext()) {
						Property property = iterator.next();
						OrmShape tableShape =  getOrCreateDatabaseTable(property.getValue().getTable() != null ? new TableProxy(property.getValue().getTable()) : null);
						createConnections(subclassShape, tableShape);
					}
				}
			}
		}

		IValue identifier = persistentClass.getIdentifier();
		if (identifier != null && identifier.isComponent()) {
			if (identifier.getComponentClassName() != null && !identifier.getComponentClassName().equals(identifier.getOwner().getEntityName())) {
				OrmShape componentClassShape = elements.get(identifier.getComponentClassName());
				if (componentClassShape == null && persistentClass.isInstanceOfRootClass()) {
					componentClassShape = getOrCreateComponentClass(persistentClass.getIdentifierProperty());

					Shape idPropertyShape = classShape.getChild(persistentClass.getIdentifierProperty());
					if (shouldCreateConnection(idPropertyShape, componentClassShape)) {
						connections.add(new Connection(idPropertyShape, componentClassShape));
					}

					OrmShape tableShape = getOrCreateDatabaseTable(identifier.getTable());
					if (componentClassShape != null) {
						createConnections(componentClassShape, tableShape);
					}
				}
			}
		}

		Iterator<IJoin> joinIterator = persistentClass.getJoinIterator();
		while (joinIterator.hasNext()) {
			IJoin join = (IJoin)joinIterator.next();
			Iterator<Property> iterator = join.getPropertyIterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				OrmShape tableShape = getOrCreateDatabaseTable(property.getValue().getTable() != null ? new TableProxy(property.getValue().getTable()) : null);
				createConnections(classShape, tableShape);
			}
		}
		return classShape;
	}

	protected OrmShape getOrCreateComponentClass(Property property) {
		OrmShape classShape = null;
		if (property == null) {
			return classShape;
		}
		IValue value = property.getValue() != null ? new ValueProxy(property.getValue()) : null;
		if (value.isCollection()) {
			IValue component = value.getElement();
			if (component != null) {
				classShape = createShape(property);
				OrmShape tableShape = elements.get(Utils.getTableName(component.getTable()));
				if (tableShape == null) {
					tableShape = getOrCreateDatabaseTable(component.getTable());
				}
				createConnections(classShape, tableShape);
				if (shouldCreateConnection(classShape, tableShape)) {
					connections.add(new Connection(classShape, tableShape));
				}
				Shape parentShape = ((SpecialOrmShape)classShape).getParentShape();
				if (parentShape != null) {
					OrmShape parentClassShape = elements.get(
							Utils.getName(((Property)parentShape.getOrmElement()).getPersistentClass().getEntityName()));
					if (shouldCreateConnection(parentShape, parentClassShape)) {
						connections.add(new Connection(parentShape, parentClassShape));
					}
				}
			}
		} else if (value.isComponent()) {
			classShape = elements.get(value.getComponentClassName());
			if (classShape == null) {
				classShape = createShape(property);
			}
		}
		return classShape;
	}

	protected OrmShape getOrCreateAssociationClass(Property property) {
		OrmShape classShape = null;
		IValue component = new ValueProxy(property.getValue()).getElement();
		if (component == null) {
			return classShape;
		}
		if (component.getAssociatedClass().isInstanceOfRootClass()) {
			classShape = getOrCreatePersistentClass(component.getAssociatedClass(), null);
			if (classShape == null) {
				classShape = createShape(component.getAssociatedClass());
			}
			OrmShape tableShape = elements.get(Utils.getTableName(
					component.getAssociatedClass().getTable()));
			if (tableShape == null) {
				tableShape = getOrCreateDatabaseTable(
						component.getAssociatedClass().getTable());
			}
			createConnections(classShape, tableShape);
			if (shouldCreateConnection(classShape, tableShape)) {
				connections.add(new Connection(classShape, tableShape));
			}
		}
		return classShape;
	}
	
	protected OrmShape createShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof Property) {
			IPersistentClass specialRootClass = service.newSpecialRootClass((Property)ormElement);
			String key = Utils.getName(specialRootClass.getEntityName());
			ormShape = elements.get(key);
			if (null == ormShape) {
				ormShape = new SpecialOrmShape(specialRootClass, consoleConfigName);
				elements.put(key, ormShape);
			}
		} else {
			String key = Utils.getName(ormElement);
			ormShape = elements.get(key);
			if (null == ormShape) {
				ormShape = new OrmShape(ormElement, consoleConfigName);
				elements.put(key, ormShape);
			}
		}
		return ormShape;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean createConnections(ExpandableShape persistentClass, ExpandableShape dbTable) {
		boolean res = false;
		if (persistentClass == null || dbTable == null) {
			return res;
		}
		Property parentProperty = null;
		if (persistentClass.getOrmElement() instanceof IPersistentClass && ((IPersistentClass)persistentClass.getOrmElement()).isInstanceOfSpecialRootClass()) {
			parentProperty = ((IPersistentClass)persistentClass.getOrmElement()).getParentProperty();
		}
		Iterator<Shape> itFields = persistentClass.getChildrenIterator();
		Set<Shape> processed = new HashSet<Shape>();
		while (itFields.hasNext()) {
			final Shape shape = itFields.next();
			Object element = shape.getOrmElement();
			if (!(element instanceof Property && parentProperty != element)) {
				continue;
			}
			IValue value = new ValueProxy(((Property)element).getValue());
			Iterator iterator = value.getColumnIterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				if (!(o instanceof IColumn)) {
					continue;
				}
				IColumn dbColumn = (IColumn)o;
				Iterator<Shape> itColumns = dbTable.getChildrenIterator();
				while (itColumns.hasNext()) {
					final Shape shapeCol = itColumns.next();
					if (processed.contains(shapeCol)) {
						continue;
					}
					if (shape.equals(shapeCol)) {
						continue;
					}
					Object ormElement = shapeCol.getOrmElement();
					String name2 = ""; //$NON-NLS-1$
					if (ormElement instanceof IColumn) {
						IColumn dbColumn2 = (IColumn)ormElement;
						name2 = dbColumn2.getName();
					} else if (ormElement instanceof Property) {
						Property property2 = (Property)ormElement;
						name2 = property2.getName();
					}
					if (dbColumn.getName().equals(name2)) {
						if (shouldCreateConnection(shape, shapeCol)) {
							connections.add(new Connection(shape, shapeCol));
							res = true;
						}
						processed.add(shapeCol);
					}						
				}
			}
		}
		return res;
	}
	

	public OrmShape getShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof Property) {
			IPersistentClass specialRootClass = service.newSpecialRootClass((Property)ormElement);
			ormShape = elements.get(Utils.getName(specialRootClass.getEntityName()));
		} else {
			ormShape = elements.get(Utils.getName(ormElement));
		}
		return ormShape;
	}
	
	
	private boolean isConnectionExist(Shape source, Shape target) {
		return Utils.isConnectionExist(source, target);
	}
	
	private boolean shouldCreateConnection(Shape source, Shape target) {
		if (source == null || target == null || source == target) {
			return false;
		}
		if (isConnectionExist(source, target)) {
			return false;
		}
		return true;
	}

	public ConsoleConfiguration getConsoleConfig() {
		final KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		ConsoleConfiguration consoleConfig = knownConfigurations.find(consoleConfigName);
		return consoleConfig;
	}

	public IConfiguration getConfig() {
		IConfiguration config = null;
		final ConsoleConfiguration consoleConfig = getConsoleConfig();
		if (consoleConfig != null) {
			config = consoleConfig.getConfiguration();
		}
		return config;
	}
}
