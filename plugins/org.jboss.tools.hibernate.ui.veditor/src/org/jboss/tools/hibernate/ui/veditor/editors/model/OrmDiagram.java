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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IJavaProject;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;
import org.jboss.tools.hibernate.ui.view.views.HibernateUtils;

public class OrmDiagram extends ModelElement {
	
	public static final String REFRESH = "refresh"; //$NON-NLS-1$
	public static final String DIRTY = "dirty"; //$NON-NLS-1$
	private	boolean dirty = false;
	private String childrenLocations[];
	private HashMap<String,OrmShape> elements = new HashMap<String,OrmShape>();
	private RootClass[] ormElements;
	private Configuration configuration;
	private ConsoleConfiguration consoleConfiguration;
	private IJavaProject javaProject;
	private String[] entityNames;
	public static final String HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME = "hibernateMapping"; //$NON-NLS-1$
	
	public OrmDiagram(ConsoleConfiguration configuration, RootClass ioe, IJavaProject javaProject) {
		consoleConfiguration = configuration;
		this.configuration = configuration.getConfiguration();
		ormElements = new RootClass[1];
		ormElements[0] = ioe;
		entityNames = new String[1];
		entityNames[0] = ioe.getEntityName();
		this.javaProject = javaProject;

		if (ioe instanceof RootClass) {
			String string = ""; //$NON-NLS-1$
			childrenLocations = string.split("#"); //$NON-NLS-1$
		}
		getOrCreatePersistentClass(ormElements[0], null);
		expandModel(this);
		load();
		setDirty(false);
	}
	
	public OrmDiagram(ConsoleConfiguration configuration, RootClass[] ioe, IJavaProject javaProject) {
		consoleConfiguration = configuration;
		this.configuration = configuration.getConfiguration();
		ormElements = new RootClass[ioe.length];
		System.arraycopy(ioe, 0, ormElements, 0, ioe.length);
		entityNames = new String[ioe.length];
		for (int i = 0; i < ormElements.length; i++) {
			entityNames[i] = ormElements[i].getEntityName();
		}
		this.javaProject = javaProject;
		if (ioe instanceof RootClass[]) {
			String string = ""; //$NON-NLS-1$
			childrenLocations = string.split("#"); //$NON-NLS-1$
		}
		for (int i = 0; i < ormElements.length; i++) {
			getOrCreatePersistentClass(ormElements[i], null);
		}
		expandModel(this);
		load();
		setDirty(false);
	}
	
	private IPath getStoreFolderPath() {
		return javaProject.getProject().getLocation().append(".settings").append(HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME); //$NON-NLS-1$
	}

	private IPath getStoreFilePath() {
		return getStoreFolderPath().append(getStoreFileName());
	}

	private String getStoreFileName() {
		String name = ormElements.length > 0 ? ormElements[0].getClassName() : ""; //$NON-NLS-1$
		for (int i = 1; i < ormElements.length; i++) {
			name += "_" + ormElements[i].getClassName(); //$NON-NLS-1$
		}
		return consoleConfiguration.getName() + "_" + name; //$NON-NLS-1$
	}

	public HashMap getCloneElements() {
		return (HashMap)elements.clone();
	}

	public RootClass getOrmElement(int idx) {
		if (0 > idx || idx >= ormElements.length) {
			return null;
		}
		return ormElements[idx];
	}

	public RootClass[] getOrmElements() {
		return ormElements;
	}

	public void refresh() {
		boolean bRefresh = false;
		for (int i = 0; i < ormElements.length; i++) {
			RootClass newOrmElement = (RootClass) consoleConfiguration
				.getConfiguration().getClassMapping(entityNames[i]);
			if (ormElements[i].equals(newOrmElement)) {
				continue;
			}
			ormElements[i] = newOrmElement;
			bRefresh = true;
		}
		if (!bRefresh) {
			return;
		}
		saveHelper();
		getChildren().clear();
		elements.clear();
		for (int i = 0; i < ormElements.length; i++) {
			getOrCreatePersistentClass(ormElements[i], null);
		}
		expandModel(this);
		load();
		firePropertyChange(REFRESH, null, null);
	}
	
	private void expandModel(ModelElement element){
		if(element.getClass().equals(ExpandeableShape.class)){
			processExpand((ExpandeableShape)element);
		}else if(element.getClass().equals(ComponentShape.class)){
			refreshComponentReferences((ComponentShape)element);
		}
		for(int i=0; i <element.getChildren().size(); i++){
			expandModel((ModelElement)element.getChildren().get(i));
		}
	}
	
	private void saveHelper() {
		childrenLocations = new String[getChildren().size()];
		for (int i = 0; i < getChildren().size(); i++) {
			OrmShape shape = (OrmShape) getChildren().get(i);
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof RootClass) {
				childrenLocations[i] = ((RootClass)ormElement).getEntityName() + "@"; //$NON-NLS-1$
			} else if (ormElement instanceof Table) {
				childrenLocations[i] = HibernateUtils.getTableName((Table)ormElement)+"@"; //$NON-NLS-1$
			}
			childrenLocations[i] += shape.getLocation().x + ";" + shape.getLocation().y+";" + shape.isHiden(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private OrmShape createShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof RootClass) {
			String key = HibernateUtils.getPersistentClassName(((RootClass)ormElement).getEntityName());
			ormShape = (OrmShape)elements.get(key);
			if (null == ormShape) {
				ormShape = new OrmShape(ormElement);
				getChildren().add(ormShape);
				elements.put(key, ormShape);
			}
		} else if (ormElement instanceof Table) {
			String key = HibernateUtils.getTableName((Table)ormElement);
			ormShape = (OrmShape)elements.get(key);
			if (null == ormShape) {
				ormShape = new OrmShape(ormElement);
				getChildren().add(ormShape);
				elements.put(key, ormShape);
			}
		} else if (ormElement instanceof Property) {
			SpecialRootClass specialRootClass = new SpecialRootClass((Property)ormElement);
			String key = HibernateUtils.getPersistentClassName(specialRootClass.getEntityName());
			ormShape = (OrmShape)elements.get(key);
			if (null == ormShape) {
				ormShape = new SpecialOrmShape(specialRootClass);
				getChildren().add(ormShape);
				elements.put(key, ormShape);
			}
		} else if (ormElement instanceof Subclass) {
			String key = HibernateUtils.getPersistentClassName(((Subclass)ormElement).getEntityName());
			ormShape = (OrmShape)elements.get(key);
			if (null == ormShape) {
				ormShape = new OrmShape(ormElement);
				getChildren().add(ormShape);
				elements.put(key, ormShape);
			}
		}
		return ormShape;
	}

	public OrmShape getShape(Object ormElement) {
		OrmShape ormShape = null;
		if (ormElement instanceof RootClass) {
			ormShape = elements.get(HibernateUtils.getPersistentClassName(((RootClass)ormElement).getEntityName()));
		} else if (ormElement instanceof Table) {
			ormShape = elements.get(HibernateUtils.getTableName((Table)ormElement));
		} else if (ormElement instanceof Property) {
			SpecialRootClass specialRootClass = new SpecialRootClass((Property)ormElement);
			ormShape = elements.get(HibernateUtils.getPersistentClassName(specialRootClass.getEntityName()));
		} else if (ormElement instanceof Subclass) {
			ormShape = elements.get(HibernateUtils.getPersistentClassName(((Subclass)ormElement).getEntityName()));
		}
		return ormShape;
	}
	

	private OrmShape getOrCreatePersistentClass(PersistentClass persistentClass, Table componentClassDatabaseTable){
		OrmShape classShape = null;
		OrmShape shape = null;
		if(persistentClass != null) {
			classShape = elements.get(HibernateUtils.getPersistentClassName(persistentClass.getEntityName()));
			if (classShape == null) classShape = createShape(persistentClass);
			if(componentClassDatabaseTable == null && persistentClass.getTable() != null)
				componentClassDatabaseTable = persistentClass.getTable();
			if(componentClassDatabaseTable != null) {
				shape = elements.get(HibernateUtils.getTableName(componentClassDatabaseTable));
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
					OrmShape subclassShape = elements.get(HibernateUtils.getPersistentClassName(subclass.getEntityName()));
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

					Iterator joinIterator = subclass.getJoinIterator();
					while (joinIterator.hasNext()) {
						Join join = (Join)joinIterator.next();
						Iterator iterator = join.getPropertyIterator();
						while (iterator.hasNext()) {
							Property property = (Property)iterator.next();
							OrmShape tableShape =  getOrCreateDatabaseTable(property.getValue().getTable());
							createConnections(subclassShape, tableShape);
							subclassShape.firePropertyChange(REFRESH, null, null);
							tableShape.firePropertyChange(REFRESH, null, null);
						}
					}
				}
			}

			if (persistentClass.getIdentifier() instanceof Component) {
				Component identifier = (Component)persistentClass.getIdentifier();
				if (identifier.getComponentClassName() != null && !identifier.getComponentClassName().equals(identifier.getOwner().getEntityName())) {
					OrmShape componentClassShape = elements.get(identifier.getComponentClassName());
					if (componentClassShape == null && persistentClass instanceof RootClass) {
						componentClassShape = getOrCreateComponentClass(((RootClass)persistentClass).getIdentifierProperty());

						Shape idPropertyShape = classShape.getChild(persistentClass.getIdentifierProperty());
						if (idPropertyShape != null && !isConnectionExist(idPropertyShape, componentClassShape)) {
							new Connection(idPropertyShape, componentClassShape);
							idPropertyShape.firePropertyChange(REFRESH, null, null);
							componentClassShape.firePropertyChange(REFRESH, null, null);
						}

						OrmShape tableShape = getOrCreateDatabaseTable(identifier.getTable());
						if (componentClassShape != null) {
							createConnections(componentClassShape, tableShape);
							componentClassShape.firePropertyChange(REFRESH, null, null);
							tableShape.firePropertyChange(REFRESH, null, null);
						}
					}
				}
			}

			Iterator joinIterator = persistentClass.getJoinIterator();
			while (joinIterator.hasNext()) {
				Join join = (Join)joinIterator.next();
				Iterator iterator = join.getPropertyIterator();
				while (iterator.hasNext()) {
					Property property = (Property)iterator.next();
					OrmShape tableShape =  getOrCreateDatabaseTable(property.getValue().getTable());
					createConnections(classShape, tableShape);
					classShape.firePropertyChange(REFRESH, null, null);
					tableShape.firePropertyChange(REFRESH, null, null);
				}
			}
		}
		return classShape;
	}
	
	private OrmShape getOrCreateDatabaseTable(Table databaseTable){
		OrmShape tableShape = null;
		if(databaseTable != null) {
			String tableName = HibernateUtils.getTableName(databaseTable);
			tableShape = (OrmShape)elements.get(tableName);
			if(tableShape == null) {
				tableShape = createShape(databaseTable);
				Iterator iterator = getConfiguration().getClassMappings();
				while (iterator.hasNext()) {
					Object clazz = iterator.next();
					if (clazz instanceof RootClass) {
						RootClass cls = (RootClass)clazz;
						Table table = cls.getTable();
						if (tableName.equals(table.getName() + "." + table.getName())) { //$NON-NLS-1$
							if (elements.get(HibernateUtils.getPersistentClassName(cls.getEntityName())) == null)
								getOrCreatePersistentClass(cls, null);
						}
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
						if(!isConnectionExist(shape, s)){
							new Connection(shape, s);
							shape.firePropertyChange(REFRESH, null, null);
							s.firePropertyChange(REFRESH, null, null);
						}
					} else if (clazz instanceof Subclass) {
						s = getOrCreatePersistentClass(((Subclass)clazz).getRootClass(), null);
					}
				}
			} else {
				s = getOrCreatePersistentClass(new SpecialRootClass(property), null);
				new Connection(shape, s);
				createConnections(s, getOrCreateDatabaseTable(property.getValue().getTable()));
				shape.firePropertyChange(REFRESH, null, null);
				s.firePropertyChange(REFRESH, null, null);
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
//		Type valueType = property.getValue().getType();
		if (property.getValue() instanceof Collection) {
			Collection collection = (Collection)property.getValue();
			Value component = collection.getElement();
			if (component instanceof Component) {// valueType.isComponentType()
				childShape = (OrmShape)elements.get(((Component)component).getComponentClassName());
				if(childShape == null) childShape = getOrCreateComponentClass(property);

				
				SimpleValue value = (SimpleValue)((Shape)componentShape.getChildren().get(0)).getOrmElement();
				OrmShape tableShape = getOrCreateDatabaseTable(value.getTable());
				Iterator iterator = value.getColumnIterator();
				while (iterator.hasNext()) {
					Column column = (Column)iterator.next();
					Shape colShape = tableShape.getChild(column);
					if(!isConnectionExist((Shape)(componentShape.getChildren().get(0)), colShape)){
						new Connection((Shape)(componentShape.getChildren().get(0)), colShape);
						((Shape)(componentShape.getChildren().get(0))).firePropertyChange(REFRESH, null, null);
						childShape.firePropertyChange(REFRESH, null, null);
					}
				}
				
				if(!isConnectionExist((Shape)(componentShape.getChildren().get(1)), childShape)){
					new Connection((Shape)(componentShape.getChildren().get(1)), childShape);
					((Shape)(componentShape.getChildren().get(1))).firePropertyChange(REFRESH, null, null);
					childShape.firePropertyChange(REFRESH, null, null);
				}
				
			} else if (collection.isOneToMany()) {
				childShape = getOrCreateAssociationClass(property);
				if (childShape == null) return;
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
				Iterator columnIterator = ((DependantValue)((Shape)componentShape.getChildren().get(0)).getOrmElement()).getColumnIterator();
				while (columnIterator.hasNext()) {
					Shape keyShape = childShape.getChild((Column)(columnIterator.next()));
					if(!isConnectionExist((Shape)componentShape.getChildren().get(0), keyShape)){
						new Connection((Shape)componentShape.getChildren().get(0), keyShape);
						((Shape)componentShape.getChildren().get(0)).firePropertyChange(REFRESH, null, null);
						keyShape.firePropertyChange(REFRESH, null, null);
					}
				}

				Iterator iter = ((SimpleValue)((Shape)componentShape.getChildren().get(1)).getOrmElement()).getColumnIterator();
				while (iter.hasNext()) {
					Object element = iter.next();
					if (element instanceof Column) {
						Column col = (Column)element;
						Shape elementShape = childShape.getChild(col);
						if(!isConnectionExist((Shape)componentShape.getChildren().get(1), elementShape)){
							new Connection((Shape)componentShape.getChildren().get(1), elementShape);
							((Shape)componentShape.getChildren().get(1)).firePropertyChange(REFRESH, null, null);
							elementShape.firePropertyChange(REFRESH, null, null);
						}
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
					OrmShape tableShape = (OrmShape)elements.get(HibernateUtils.getTableName(component.getTable()));
					if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getTable());
						createConnections(classShape, tableShape);
						if(!isConnectionExist(classShape, tableShape)){
							new Connection(classShape, tableShape);
							classShape.firePropertyChange(REFRESH, null, null);
							tableShape.firePropertyChange(REFRESH, null, null);
						}
						Shape parentShape = ((SpecialOrmShape)classShape).getParentShape();
						if (parentShape != null) {
							OrmShape parentClassShape = (OrmShape)elements.get(HibernateUtils.getPersistentClassName(((Property)parentShape.getOrmElement()).getPersistentClass().getEntityName()));
							if(!isConnectionExist(parentShape, parentClassShape)){
								new Connection(parentShape, parentClassShape);
								parentShape.firePropertyChange(REFRESH, null, null);
								parentClassShape.firePropertyChange(REFRESH, null, null);
							}
						}
				}
			} else if (property.getValue() instanceof Component) {
				classShape = (OrmShape)elements.get(((Component)property.getValue()).getComponentClassName());
				if (classShape == null) classShape = createShape(property);
			}
		}
		return classShape;
	}

	private OrmShape getOrCreateAssociationClass(Property property) {
		OrmShape classShape = null;
		OneToMany component = (OneToMany)((Collection)property.getValue()).getElement();
		if (component != null) {
			if (component.getAssociatedClass() instanceof RootClass) {
				classShape = getOrCreatePersistentClass(component.getAssociatedClass(), null);
				if (classShape == null) classShape = createShape(component.getAssociatedClass());
				OrmShape tableShape = (OrmShape)elements.get(HibernateUtils.getTableName(component.getAssociatedClass().getTable()));
				if (tableShape == null) tableShape = getOrCreateDatabaseTable(component.getAssociatedClass().getTable());
					createConnections(classShape, tableShape);
					if(!isConnectionExist(classShape, tableShape)){
						new Connection(classShape, tableShape);
						classShape.firePropertyChange(REFRESH, null, null);
						tableShape.firePropertyChange(REFRESH, null, null);
					}
			}
		}
		return classShape;
	}
	
	
	
	public String getKey(Shape shape) {
		Object element = shape.getOrmElement();
		String key=null;
		if (element instanceof RootClass) {
			key = HibernateUtils.getPersistentClassName(((RootClass)element).getEntityName());
		} else if (element instanceof Table) {
			key = HibernateUtils.getTableName((Table)element);
		} else if (element instanceof Property) {
			Property property = (Property)element;
			key = property.getPersistentClass().getEntityName() + "." + property.getName(); //$NON-NLS-1$
		} else if (element instanceof Subclass) {
			key = HibernateUtils.getPersistentClassName(((Subclass)element).getEntityName());
		}
		
		return key;
	}
	
	public void propertiesInit(Properties properties, ModelElement shape){
		boolean state;
		
		if(shape instanceof OrmShape){
			state = getState(properties, (Shape)shape);
			if(state)
				((OrmShape)shape).refreshHiden();
			((OrmShape)shape).setLocation(getPosition(properties, (OrmShape)shape));
		}else if(shape instanceof ExpandeableShape){
			state = getState(properties, (Shape)shape);
			if(!state)
				((ExpandeableShape)shape).refHide = false;
		}

		for(int i=0;i<shape.getChildren().size();i++){
			propertiesInit(properties, (ModelElement)shape.getChildren().get(i));
		}
	}
	
	
	
	private void storeProperties(Properties properties, ModelElement shape){
		boolean state;
		if(shape instanceof OrmShape){
			state = ((OrmShape)shape).hiden;
			setState(properties, (Shape)shape, state);
			setPosition(properties, (OrmShape)shape);
		}else if(shape instanceof ExpandeableShape){
			state = ((ExpandeableShape)shape).refHide;
			setState(properties, (Shape)shape, state);
		}
		for(int i=0;i<shape.getChildren().size();i++){
			storeProperties(properties, (Shape)shape.getChildren().get(i));
		}
	}
	
	public void save(){
		Properties properties = new Properties();
		storeProperties(properties, this);
		FileOutputStream fos = null;
		try {
			File folder = new File(getStoreFolderPath().toOSString());
			if(!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(getStoreFilePath().toOSString());
			if(!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			properties.store(fos, ""); //$NON-NLS-1$
		} catch (IOException e) {
			VisualEditorPlugin.getDefault().logError("Can't save layout of mapping.", e); //$NON-NLS-1$
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public IFile createLayoutFile(InputStream source) {
		IFile file = null;
		IPath path = javaProject.getProject().getLocation().append(".settings").append(HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME); //$NON-NLS-1$
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		if(!folder.exists()) {
			try {
				folder.create(true, true, null);

				file = folder.getFile(getStoreFileName());
				if (!file.exists()) {
					file.create(source, true, null);
				}
			} catch (CoreException e) {
				VisualEditorPlugin.getDefault().logError(e);
			}
		}
		return file;
	}

	private boolean loadSuccessfull = false;
	
	public void load(){
		Properties properties = new Properties();
		FileInputStream fis = null;
		try{
			File file = new File(getStoreFilePath().toOSString());
			if(file.exists()){
				fis = new FileInputStream(file);
				properties.load(fis);
				propertiesInit(properties, this);
				loadSuccessfull = true;
			}
		}catch(IOException ex){
			VisualEditorPlugin.getDefault().logError("Can't load layout of mapping.", ex); //$NON-NLS-1$
		} finally {
			if(fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	public boolean isLoadSuccessfull(){
		return loadSuccessfull;
	}
	
		
	private void setState(Properties properties,String key, boolean value){
		if(properties.containsKey(key)){
			properties.remove(key);
			properties.put(key, Boolean.valueOf(value).toString());
		}else{
			properties.put(key, Boolean.valueOf(value).toString());
		}
	}
	
	public void setState(Properties properties,Shape shape, boolean value){
		setState(properties, getKey(shape)+".state", value); //$NON-NLS-1$
	}
	
	private boolean getState(Properties properties, String key){
		String str = properties.getProperty(key, "true"); //$NON-NLS-1$
		
		return Boolean.valueOf(str).booleanValue();
	}
	
	private Point getPoint(Properties properties, String key){
		Point point = new Point(0,0);
		String str = properties.getProperty(key+".x","0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.x = Integer.parseInt(str);
		String str2 = properties.getProperty(key+".y","0"); //$NON-NLS-1$ //$NON-NLS-2$
		point.y = Integer.parseInt(str2);
		return point;
	}
	
	private void setPoint(Properties properties, String key, Point point){
		String key1 = key+".x"; //$NON-NLS-1$
		if(!properties.containsKey(key1)){
			properties.remove(key1);
			properties.put(key1, ""+point.x); //$NON-NLS-1$
		}else
			properties.put(key1, ""+point.x); //$NON-NLS-1$
		String key2 = key+".y"; //$NON-NLS-1$
		if(!properties.containsKey(key2)){
			properties.remove(key2);
			properties.put(key2, ""+point.y); //$NON-NLS-1$
		}else
			properties.put(key2, ""+point.y); //$NON-NLS-1$
	}
	
	public void setPosition(Properties properties, OrmShape shape){
		Point point = shape.getLocation();
		setPoint(properties, getKey(shape), point);
	}

	public Point getPosition(Properties properties, OrmShape shape){
		return getPoint(properties, getKey(shape));
	}
	
	public boolean getState(Properties properties, Shape shape){
		return getState(properties, getKey(shape)+".state"); //$NON-NLS-1$
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		return consoleConfiguration;
	}
}