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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.draw2d.geometry.Point;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.veditor.VizualEditorPlugin;


/**
 * @author Konstantin Mishin
 *
 */
public class OrmDiagram extends ModelElement {
	
	public static final String REFRESH = "refresh";
	public static final String DIRTY = "dirty";
	private static final String qualifiedNameString = "OrmDiagramChildrenLocations";
	private	boolean dirty = false;
	private String childrenLocations[];
	private IResource resource = null;
	private List<Shape> shapes = new ArrayList<Shape>();
	private HashMap<String,OrmShape> elements = new HashMap<String,OrmShape>();
	private RootClass  ormElement;
	private Configuration configuration;
	
	
	public OrmDiagram(Configuration configuration, RootClass ioe) {
		this.configuration = configuration;
		ormElement = (RootClass)ioe;
//		if (ormElement instanceof IPersistentClassMapping)
//			ormElement = ((IPersistentClassMapping)ormElement).getPersistentClass();
//		if (ormElement instanceof IPersistentClass) {
		if (ormElement instanceof RootClass) {
			String string = "";
//			resource =((RootClass)ormElement).getPersistentClassMapping().getStorage().getResource();
//			try {
//				int i = 0;
//				String tempString;
//				do {
//					tempString = resource.getPersistentProperty(new QualifiedName(VizualEditorPlugin.PLUGIN_ID,qualifiedNameString+i++));					
//					string += tempString;
//				} while (tempString != null);
//			} catch (CoreException e) {
////				ExceptionHandler.logThrowableError(e, e.getMessage());
//			}
			childrenLocations = string.split("#");
		} //else
//			throw new IllegalArgumentException();
		getOrCreatePersistentClass(ormElement, null);

	}
	
	public List getChildren() {
		return shapes;
	}
	
	public HashMap getCloneElements() {
		return (HashMap)elements.clone();
	}

	public RootClass getOrmElement() {
		return ormElement;
	}

	public void refresh() {
		saveHelper();
		shapes.clear();
		elements.clear();
//		if( ((IPersistentClass)ormElement).getProjectMapping().findClass(ormElement.getName()) != null)
///			getOrCreatePersistentClass((IPersistentClass)ormElement, null);
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
					resource.setPersistentProperty((new QualifiedName(VizualEditorPlugin.PLUGIN_ID,qualifiedNameString+i)),
							string.substring(2048*i,2048*(i++)+2047));
				}
				resource.setPersistentProperty((new QualifiedName(VizualEditorPlugin.PLUGIN_ID,qualifiedNameString+i)),
						string.substring(2048*i));
			} catch (CoreException e) {
//				ExceptionHandler.logThrowableError(e, e.getMessage());
			}
	}
	
	private void saveHelper() {
		childrenLocations = new String[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) {
			OrmShape shape = (OrmShape) shapes.get(i);
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof RootClass) {
				childrenLocations[i] = ((RootClass)ormElement).getClassName() + "@";
			} else if (ormElement instanceof Table) {
				childrenLocations[i] = ((Table)ormElement).getSchema() + "." + ((Table)ormElement).getName()+"@";
//			} else if (ormElement instanceof Component) {
//				childrenLocations[i] = ((Component)ormElement).getComponentClassName()+"@";
			}
			childrenLocations[i] += shape.getLocation().x + ";" + shape.getLocation().y+";" + shape.isHiden();
		}
	}
	
	private OrmShape createShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof RootClass) {
			ormShape = new OrmShape(ormElement);
			shapes.add(ormShape);
			elements.put(((RootClass)ormElement).getClassName(), ormShape);
		} else if (ormElement instanceof Table) {
			ormShape = new OrmShape(ormElement);
			shapes.add(ormShape);
			Table table = (Table)ormElement;
			elements.put(table.getSchema() + "." + table.getName(), ormShape);
		} else if (ormElement instanceof Property) {
//			ormShape = new OrmShape(ormElement);
			SpecialRootClass specialRootClass = new SpecialRootClass((Property)ormElement);
			ormShape = new SpecialOrmShape(specialRootClass);
			shapes.add(ormShape);
//			Property property = (Property)ormElement;
//			elements.put(property.getPersistentClass().getEntityName() + "." + property.getName(), ormShape);
			elements.put(specialRootClass.getClassName(), ormShape);
		}
		return ormShape;
	}
	
	private OrmShape getOrCreatePersistentClass(RootClass persistentClass, Table componentClassDatabaseTable){
		OrmShape classShape = null;
		OrmShape shape = null;
		if(persistentClass != null) {
			classShape = elements.get(persistentClass.getClassName());
			if (classShape == null) classShape = createShape(persistentClass);
			if(componentClassDatabaseTable == null && persistentClass.getTable() != null)
				componentClassDatabaseTable = persistentClass.getTable();
			if(componentClassDatabaseTable != null) {
				shape = elements.get(componentClassDatabaseTable.getSchema() + "." + componentClassDatabaseTable.getName());
				if (shape == null) shape = getOrCreateDatabaseTable(componentClassDatabaseTable);
				createConnections(classShape, shape);
				new Connection(classShape, shape);
			}
//			if (persistentClass.getPersistentClassMapping() != null) {
//				Iterator iter =((IHibernateClassMapping)(persistentClass).getPersistentClassMapping()).getJoinIterator();			
//				while ( iter.hasNext() ) {
//					IJoinMapping jm =(IJoinMapping)iter.next();
//					shape =  (OrmShape)elements.get(jm.getTable().getName());
//					if(shape == null)
//						shape = getOrCreateDatabaseTable(jm.getTable());
//					createConnections(classShape, shape);
//				}
//			}
		}
		return classShape;
	}
	
	private OrmShape getOrCreateDatabaseTable(Table databaseTable){
		OrmShape tableShape = null;
		if(databaseTable != null) {
			String tableName = databaseTable.getName() + "." + databaseTable.getName();
			tableShape = (OrmShape)elements.get(tableName);
			if(tableShape == null) {
				tableShape = createShape(databaseTable);
				Iterator iterator = getConfiguration().getClassMappings();
				while (iterator.hasNext()) {
					RootClass cls = (RootClass)iterator.next();
					Table table = cls.getTable();
					if (tableName.equals(table.getName() + "." + table.getName())) {
						if (elements.get(cls.getClassName()) == null)
							getOrCreatePersistentClass(cls, null);
					}
				}
//				IPersistentClassMapping persistentClassMappings[] = databaseTable.getPersistentClassMappings();
//				for (int j = 0; j < persistentClassMappings.length; j++) {
//					if(persistentClassMappings[j].getPersistentClass() != null ) {
//						OrmShape shape =  (OrmShape)elements.get(persistentClassMappings[j].getPersistentClass().getName());
//						if(shape == null)
//							getOrCreatePersistentClass(persistentClassMappings[j].getPersistentClass(), null);
//					}
//				}
			}			
		}
		return tableShape;
	}
	
	private void createConnections(ExtendedShape persistentClass, ExtendedShape databaseTable){
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
					Column databaseColumn = (Column) iterator.next();
					for (int j = 0; j < databaseColumns.size(); j++) {
						if (databaseColumn.getName().equals(((Column)((Shape)databaseColumns.get(j)).getOrmElement()).getName())) {
							Shape databaseShape = (Shape)databaseColumns.remove(j);
							new Connection(shape, databaseShape);
							databaseColumns2.add(i++, databaseShape);
						}						
					}
				}
			}
		}
		databaseColumns.addAll(databaseColumns2);
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
		Object element = shape.getOrmElement();
		if (element instanceof Property) {
			Type type = ((Property)element).getType();
			if (type.isEntityType()) {
				EntityType et = (EntityType) type;
				RootClass rootClass = (RootClass)getConfiguration().getClassMapping(et.getAssociatedEntityName());
				OrmShape s = getOrCreatePersistentClass(rootClass, null);
				HashMap targets = new HashMap();
				Iterator iterator = shape.getSourceConnections().iterator();
				while (iterator.hasNext()) {
					Connection connection = (Connection)iterator.next();
					connection.setHiden(shape.getHide());
					targets.put(((Column)connection.getTarget().getOrmElement()).getName(), connection.getTarget());
				}
				KeyValue id = rootClass.getIdentifier();
				iterator = id.getColumnIterator();
				while (iterator.hasNext()) {
					Column column = (Column)iterator.next();
					if (targets.get(column.getName()) != null) {
						new Connection(s, (Shape)targets.get(column.getName()));
					}
				}
				new Connection(shape, s);
				firePropertyChange(REFRESH, null, null);
			}
		}
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	protected void refreshComponentReferences(ComponentShape componentShape) {
		Property property = (Property)componentShape.getOrmElement();
		Type valueType = property.getValue().getType();
		if (valueType.isCollectionType()) {
			Collection collection = (Collection)property.getValue();
			Value component = collection.getElement();
			if (valueType.isComponentType()) {
				OrmShape childShape = (OrmShape)elements.get(((Component)component).getComponentClassName());
				if(childShape == null) childShape = getOrCreateComponentClass(property);
				new Connection((Shape)(componentShape.getChildren().get(1)), childShape);
			} else if (collection.isOneToMany()) {
				OrmShape childShape = getOrCreateAssociationClass(property);
				new Connection((Shape)(componentShape.getChildren().get(1)), childShape);
			} else if (collection.isMap()) {
				Map map = (Map)collection;
				OrmShape childShape = getOrCreateDatabaseTable(map.getCollectionTable());
				Shape keyShape = childShape.getChild(((DependantValue)componentShape.getChildren().get(0).getOrmElement()).getColumnIterator().next());
				new Connection(componentShape.getChildren().get(0), keyShape);
				Shape elementShape = childShape.getChild(((SimpleValue)componentShape.getChildren().get(1).getOrmElement()).getColumnIterator().next());
				new Connection(componentShape.getChildren().get(1), elementShape);
			}
			setDirty(true);
			firePropertyChange(REFRESH, null, null);
		}
	}

	private OrmShape getOrCreateComponentClass(Property property) {
		OrmShape classShape = null;
		Component component = (Component)((Collection)property.getValue()).getElement();
		if (component != null) {
			classShape = createShape(property);
			OrmShape tableShape = (OrmShape)elements.get(component.getTable().getSchema() + "." + component.getTable().getName());
			if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getTable());
				createConnections(classShape, tableShape);
				new Connection(classShape, tableShape);
				Shape parentShape = ((SpecialOrmShape)classShape).getParentShape();
				OrmShape parentClassShape = (OrmShape)elements.get(((Property)parentShape.getOrmElement()).getPersistentClass().getClassName());
				new Connection(parentShape, parentClassShape);
		}
		return classShape;
	}

	private OrmShape getOrCreateAssociationClass(Property property) {
		OrmShape classShape = null;
		OneToMany component = (OneToMany)((Collection)property.getValue()).getElement();
		if (component != null) {
			classShape = (OrmShape)elements.get(component.getAssociatedClass().getClassName());
			if (classShape == null) classShape = createShape(component.getAssociatedClass());
			OrmShape tableShape = (OrmShape)elements.get(component.getAssociatedClass().getTable().getSchema() + "." + component.getAssociatedClass().getTable().getName());
			if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getAssociatedClass().getTable());
				createConnections(classShape, tableShape);
				new Connection(classShape, tableShape);
		}
		return classShape;
	}
}