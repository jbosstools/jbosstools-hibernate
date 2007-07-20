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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;

public class OrmDiagram extends ModelElement {
	
	public static final String REFRESH = "refresh";
	public static final String DIRTY = "dirty";
	private static final String qualifiedNameString = "OrmDiagramChildrenLocations";
	private	boolean dirty = false;
	private String childrenLocations[];
	private IResource resource = null;
	private HashMap<String,OrmShape> elements = new HashMap<String,OrmShape>();
	private RootClass  ormElement;
	private Configuration configuration;
	
	
	public OrmDiagram(Configuration configuration, RootClass ioe) {
		this.configuration = configuration;
		ormElement = (RootClass)ioe;
		if (ormElement instanceof RootClass) {
			String string = "";
// resource
// =((RootClass)ormElement).getPersistentClassMapping().getStorage().getResource();
// try {
// int i = 0;
// String tempString;
// do {
// tempString = resource.getPersistentProperty(new
// QualifiedName(VisualEditorPlugin.PLUGIN_ID,qualifiedNameString+i++));
// string += tempString;
// } while (tempString != null);
// } catch (CoreException e) {
// // ExceptionHandler.logThrowableError(e, e.getMessage());
// }
			childrenLocations = string.split("#");
		} // else
// throw new IllegalArgumentException();
		getOrCreatePersistentClass(ormElement, null);

	}
	
	public HashMap getCloneElements() {
		return (HashMap)elements.clone();
	}

	public RootClass getOrmElement() {
		return ormElement;
	}

	public void refresh() {
		saveHelper();
		getChildren().clear();
		elements.clear();
// if(
// ((IPersistentClass)ormElement).getProjectMapping().findClass(ormElement.getName())
// != null)
// / getOrCreatePersistentClass((IPersistentClass)ormElement, null);
		firePropertyChange(REFRESH, null, null);
	}
	
	public void save() {
		String string = "";
		saveHelper();
		for (int i = 0; i < childrenLocations.length; i++)
			string+=childrenLocations[i]+"#";
		if(resource.exists() && string.length() > 0)
			try {
				int i = 0;				
				while(string.length() > 2048*(i+1)) {
					resource.setPersistentProperty((new QualifiedName(VisualEditorPlugin.PLUGIN_ID,qualifiedNameString+i)),
							string.substring(2048*i,2048*(i++)+2047));
				}
				resource.setPersistentProperty((new QualifiedName(VisualEditorPlugin.PLUGIN_ID,qualifiedNameString+i)),
						string.substring(2048*i));
			} catch (CoreException e) {
// ExceptionHandler.logThrowableError(e, e.getMessage());
			}
	}
	
	private void saveHelper() {
		childrenLocations = new String[getChildren().size()];
		for (int i = 0; i < getChildren().size(); i++) {
			OrmShape shape = (OrmShape) getChildren().get(i);
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof RootClass) {
				childrenLocations[i] = ((RootClass)ormElement).getEntityName() + "@";
			} else if (ormElement instanceof Table) {
				childrenLocations[i] = ((Table)ormElement).getSchema() + "." + ((Table)ormElement).getName()+"@";
// } else if (ormElement instanceof Component) {
// childrenLocations[i] = ((Component)ormElement).getComponentClassName()+"@";
			}
			childrenLocations[i] += shape.getLocation().x + ";" + shape.getLocation().y+";" + shape.isHiden();
		}
	}
	
	private OrmShape createShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof RootClass) {
			ormShape = new OrmShape(ormElement);
			getChildren().add(ormShape);
			elements.put(((RootClass)ormElement).getEntityName(), ormShape);
		} else if (ormElement instanceof Table) {
			ormShape = new OrmShape(ormElement);
			getChildren().add(ormShape);
			Table table = (Table)ormElement;
			elements.put(table.getSchema() + "." + table.getName(), ormShape);
		} else if (ormElement instanceof Property) {
			SpecialRootClass specialRootClass = new SpecialRootClass((Property)ormElement);
			ormShape = new SpecialOrmShape(specialRootClass);
			getChildren().add(ormShape);
			elements.put(specialRootClass.getEntityName(), ormShape);
		} else if (ormElement instanceof Subclass) {
			ormShape = new OrmShape(ormElement);
			getChildren().add(ormShape);
			elements.put(((Subclass)ormElement).getEntityName(), ormShape);
		}
		return ormShape;
	}

	public OrmShape getShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof RootClass) {
			ormShape = elements.get(((RootClass)ormElement).getEntityName());
		} else if (ormElement instanceof Table) {
			Table table = (Table)ormElement;
			ormShape = elements.get(table.getSchema() + "." + table.getName());
		} else if (ormElement instanceof Property) {
			SpecialRootClass specialRootClass = new SpecialRootClass((Property)ormElement);
			ormShape = elements.get(specialRootClass.getEntityName());
		} else if (ormElement instanceof Subclass) {
			ormShape = elements.get(((Subclass)ormElement).getEntityName());
		}
		return ormShape;
	}
	

	private OrmShape getOrCreatePersistentClass(PersistentClass persistentClass, Table componentClassDatabaseTable){
		OrmShape classShape = null;
		OrmShape shape = null;
		if(persistentClass != null) {
			classShape = elements.get(persistentClass.getEntityName());
			if (classShape == null) classShape = createShape(persistentClass);
			if(componentClassDatabaseTable == null && persistentClass.getTable() != null)
				componentClassDatabaseTable = persistentClass.getTable();
			if(componentClassDatabaseTable != null) {
				shape = elements.get(componentClassDatabaseTable.getSchema() + "." + componentClassDatabaseTable.getName());
				if (shape == null) shape = getOrCreateDatabaseTable(componentClassDatabaseTable);
				createConnections(classShape, shape);
				if(!isConnectionExist(classShape, shape)){
					new Connection(classShape, shape);
					classShape.firePropertyChange(REFRESH, null, null);
					shape.firePropertyChange(REFRESH, null, null);
				}
			}
			RootClass rc = (RootClass)persistentClass;
			Iterator iter = rc.getSubclassIterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				if (element instanceof Subclass) {
					Subclass subclass = (Subclass)element;
					OrmShape subclassShape = elements.get(subclass.getEntityName());
					if (subclassShape == null) subclassShape = createShape(subclass);
					if (((Subclass)element).isJoinedSubclass()) {
						Table jcTable = ((Subclass)element).getTable();
						OrmShape jcTableShape = getOrCreateDatabaseTable(jcTable);
						createConnections(subclassShape, jcTableShape);
						if(!isConnectionExist(subclassShape, jcTableShape)){
							new Connection(subclassShape, jcTableShape);
							subclassShape.firePropertyChange(REFRESH, null, null);
							jcTableShape.firePropertyChange(REFRESH, null, null);
						}
					} else {
						createConnections(subclassShape, shape);
						if(!isConnectionExist(subclassShape, shape)){
							new Connection(subclassShape, shape);
							subclassShape.firePropertyChange(REFRESH, null, null);
							shape.firePropertyChange(REFRESH, null, null);
						}
					}
					OrmShape ownerTableShape = getOrCreateDatabaseTable(((Subclass)element).getRootTable());
					createConnections(subclassShape, ownerTableShape);
				}
			}

			if (persistentClass.getIdentifier() instanceof Component) {
				Component identifier = (Component)persistentClass.getIdentifier();
				if (identifier.getComponentClassName() != null && !identifier.getComponentClassName().equals(identifier.getOwner().getEntityName())) {
					OrmShape componentClassShape = elements.get(identifier.getComponentClassName());
					if (componentClassShape == null && persistentClass instanceof RootClass) {
						componentClassShape = getOrCreateComponentClass(((RootClass)persistentClass).getIdentifierProperty());
						OrmShape tableShape = getOrCreateDatabaseTable(identifier.getTable());
						if (componentClassShape != null)
							createConnections(componentClassShape, tableShape);
					}
				}
			}
		}
		return classShape;
	}
	
	private OrmShape getOrCreateDatabaseTable(Table databaseTable){
		OrmShape tableShape = null;
		if(databaseTable != null) {
			String tableName = databaseTable.getSchema() + "." + databaseTable.getName();
			tableShape = (OrmShape)elements.get(tableName);
			if(tableShape == null) {
				tableShape = createShape(databaseTable);
				Iterator iterator = getConfiguration().getClassMappings();
				while (iterator.hasNext()) {
					Object clazz = iterator.next();
					if (clazz instanceof RootClass) {
						RootClass cls = (RootClass)clazz;
						Table table = cls.getTable();
						if (tableName.equals(table.getName() + "." + table.getName())) {
							if (elements.get(cls.getEntityName()) == null)
								getOrCreatePersistentClass(cls, null);
						}
// } else if (clazz instanceof SingleTableSubclass) {
// SingleTableSubclass singleTableSubclass = (SingleTableSubclass)clazz;
// getOrCreatePersistentClass(singleTableSubclass, null);
					}
				}
			}			
		}
		return tableShape;
	}
	
	private void createConnections(ExpandeableShape persistentClass, ExpandeableShape databaseTable){
		int i = 0;
		boolean check = (persistentClass.getOrmElement() instanceof SpecialRootClass);
		Iterator persistentFields = persistentClass.getChildren().iterator();
		List databaseColumns = databaseTable.getChildren();
		List databaseColumns2 = new ArrayList();
		Iterator iterator = null;
		while (persistentFields.hasNext()) {
			Shape shape = (Shape) persistentFields.next();
			Object element = shape.getOrmElement();
			if (element instanceof Property && (!check || ((SpecialRootClass)persistentClass.getOrmElement()).getParentProperty() != element)) {
				Value value = ((Property)element).getValue();
				iterator = value.getColumnIterator();
				while (iterator.hasNext()) {
					Object o = iterator.next();
					if (o instanceof Column) {
						Column databaseColumn = (Column)o;
						for (int j = 0; j < databaseColumns.size(); j++) {
							if (databaseColumn.getName().equals(((Column)((Shape)databaseColumns.get(j)).getOrmElement()).getName())) {
								Shape databaseShape = (Shape)databaseColumns.remove(j);
								if(!isConnectionExist(shape, databaseShape)){
									new Connection(shape, databaseShape);
									shape.firePropertyChange(REFRESH, null, null);
									databaseShape.firePropertyChange(REFRESH, null, null);
								}
								databaseColumns2.add(i++, databaseShape);
							}						
						}
					}
				}
			}
		}
		databaseColumns.addAll(databaseColumns2);
	}
	
	private boolean isConnectionExist(Shape source, Shape target){
		Connection conn;
		if (source != null && source.getSourceConnections() != null) {
			for(int i=0;i<source.getSourceConnections().size();i++){
				conn = (Connection)source.getSourceConnections().get(i);
				if(conn.getTarget().equals(target)) return true;
			}
		}
		return false;
	}
	
	public String[] getChildrenLocations() {
		return childrenLocations;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		if(this.dirty != dirty) {
			this.dirty = dirty;
			firePropertyChange(DIRTY, null, null);
		}
	}
	
	public void processExpand(ExpandeableShape shape) {
		OrmShape s=null;
		Object element = shape.getOrmElement();
		if (element instanceof Property) {
			Property property = (Property)element;
			if (!property.isComposite()) {
				Type type = ((Property)element).getType();
				if (type.isEntityType()) {
					EntityType et = (EntityType) type;
					Object clazz = getConfiguration().getClassMapping(et.getAssociatedEntityName());
					if (clazz instanceof RootClass) {
						RootClass rootClass = (RootClass)clazz;
						s = getOrCreatePersistentClass(rootClass, null);
// HashMap targets = new HashMap();
// Iterator iterator = shape.getSourceConnections().iterator();
// while (iterator.hasNext()) {
// Connection connection = (Connection)iterator.next();
// connection.setHiden(shape.getHide());
// Object el = connection.getTarget().getOrmElement();
// if (el instanceof Column) {
// targets.put(((Column)el).getName(), connection.getTarget());
// } else if (el instanceof RootClass) {
// targets.put(((RootClass)el).getEntityName(), connection.getTarget());
// }
// }
// KeyValue id = rootClass.getIdentifier();
// iterator = id.getColumnIterator();
// while (iterator.hasNext()) {
// Column column = (Column)iterator.next();
// if (targets.get(column.getName()) != null && !isConnectionExist(s,
// (Shape)targets.get(column.getName()))) {
// new Connection(s, (Shape)targets.get(column.getName()));
// }
// }
						if(!isConnectionExist(shape, s)){
							new Connection(shape, s);
							shape.firePropertyChange(REFRESH, null, null);
							s.firePropertyChange(REFRESH, null, null);
						}
					} else if (clazz instanceof Subclass) {
						s = getOrCreatePersistentClass(((SingleTableSubclass)clazz).getRootClass(), null);
					}
				}
			} else {
				s = getOrCreatePersistentClass(new SpecialRootClass(property), null);
			}
			if(!shape.getParent().equals(s))
				shape.setReference(s);
			firePropertyChange(REFRESH, null, null);
		}
	}
	
	public void update(){
		firePropertyChange(REFRESH, null, null);
	}
	
	protected Configuration getConfiguration() {
		return configuration;
	}
	

	protected void refreshComponentReferences(ComponentShape componentShape) {
		OrmShape childShape = null;
		Property property = (Property)componentShape.getOrmElement();
		Type valueType = property.getValue().getType();
		if (valueType.isCollectionType()) {
			Collection collection = (Collection)property.getValue();
			Value component = collection.getElement();
			if (component instanceof Component) {// valueType.isComponentType()
				childShape = (OrmShape)elements.get(((Component)component).getComponentClassName());
				if(childShape == null) childShape = getOrCreateComponentClass(property);
				if(!isConnectionExist((Shape)(componentShape.getChildren().get(1)), childShape)){
					new Connection((Shape)(componentShape.getChildren().get(1)), childShape);
					((Shape)(componentShape.getChildren().get(1))).firePropertyChange(REFRESH, null, null);
					childShape.firePropertyChange(REFRESH, null, null);
				}
				
			} else if (collection.isOneToMany()) {
				childShape = getOrCreateAssociationClass(property);
				if(!isConnectionExist((Shape)(componentShape.getChildren().get(1)), childShape)){
					new Connection((Shape)(componentShape.getChildren().get(1)), childShape);
					((Shape)(componentShape.getChildren().get(1))).firePropertyChange(REFRESH, null, null);
					childShape.firePropertyChange(REFRESH, null, null);
				}
				OrmShape keyTableShape = getOrCreateDatabaseTable(collection.getKey().getTable());
				Iterator iter = collection.getKey().getColumnIterator();
				while (iter.hasNext()) {
					Column col = (Column)iter.next();
					Shape keyColumnShape = keyTableShape.getChild(col);
					if (keyColumnShape != null && !isConnectionExist((Shape)(componentShape.getChildren().get(0)), keyColumnShape)){
						new Connection((Shape)(componentShape.getChildren().get(0)), keyColumnShape);
						((Shape)(componentShape.getChildren().get(0))).firePropertyChange(REFRESH, null, null);
						keyColumnShape.firePropertyChange(REFRESH, null, null);
					}
				}
				
			} else /* if (collection.isMap() || collection.isSet()) */ {
				childShape = getOrCreateDatabaseTable(collection.getCollectionTable());
				Shape keyShape = childShape.getChild((Column)((DependantValue)((Shape)componentShape.getChildren().get(0)).getOrmElement()).getColumnIterator().next());
				if(!isConnectionExist((Shape)componentShape.getChildren().get(0), keyShape)){
					new Connection((Shape)componentShape.getChildren().get(0), keyShape);
					((Shape)componentShape.getChildren().get(0)).firePropertyChange(REFRESH, null, null);
					keyShape.firePropertyChange(REFRESH, null, null);
				}

				Iterator iter = ((SimpleValue)((Shape)componentShape.getChildren().get(1)).getOrmElement()).getColumnIterator();
				while (iter.hasNext()) {
					Column col = (Column)iter.next();
					Shape elementShape = childShape.getChild(col);
					if(!isConnectionExist((Shape)componentShape.getChildren().get(1), elementShape)){
						new Connection((Shape)componentShape.getChildren().get(1), elementShape);
						((Shape)componentShape.getChildren().get(1)).firePropertyChange(REFRESH, null, null);
						elementShape.firePropertyChange(REFRESH, null, null);
					}
				}
			}
			if(!componentShape.getParent().equals(childShape))
				componentShape.setReference(childShape);
			setDirty(true);
			firePropertyChange(REFRESH, null, null);
		}
	}

	public OrmShape getOrCreateComponentClass(Property property) {
		OrmShape classShape = null;
		if (property != null) {
			if (property.getValue() instanceof Collection) {
				Component component = (Component)((Collection)property.getValue()).getElement();
				if (component != null) {
					classShape = createShape(property);
					OrmShape tableShape = (OrmShape)elements.get(component.getTable().getSchema() + "." + component.getTable().getName());
					if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getTable());
						createConnections(classShape, tableShape);
						if(!isConnectionExist(classShape, tableShape)){
							new Connection(classShape, tableShape);
							classShape.firePropertyChange(REFRESH, null, null);
							tableShape.firePropertyChange(REFRESH, null, null);
						}
						Shape parentShape = ((SpecialOrmShape)classShape).getParentShape();
						OrmShape parentClassShape = (OrmShape)elements.get(((Property)parentShape.getOrmElement()).getPersistentClass().getEntityName());
						if(!isConnectionExist(parentShape, parentClassShape)){
							new Connection(parentShape, parentClassShape);
							parentShape.firePropertyChange(REFRESH, null, null);
							parentClassShape.firePropertyChange(REFRESH, null, null);
						}
				}
			} else if (property.getValue() instanceof Component) {
				classShape = createShape(property);
			}
		}
		return classShape;
	}

	private OrmShape getOrCreateAssociationClass(Property property) {
		OrmShape classShape = null;
		OneToMany component = (OneToMany)((Collection)property.getValue()).getElement();
		if (component != null) {
			classShape = (OrmShape)elements.get(component.getAssociatedClass().getEntityName());
			if (classShape == null) classShape = createShape(component.getAssociatedClass());
			OrmShape tableShape = (OrmShape)elements.get(component.getAssociatedClass().getTable().getSchema() + "." + component.getAssociatedClass().getTable().getName());
			if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getAssociatedClass().getTable());
				createConnections(classShape, tableShape);
				if(!isConnectionExist(classShape, tableShape)){
					new Connection(classShape, tableShape);
					classShape.firePropertyChange(REFRESH, null, null);
					tableShape.firePropertyChange(REFRESH, null, null);
				}
		}
		return classShape;
	}
}