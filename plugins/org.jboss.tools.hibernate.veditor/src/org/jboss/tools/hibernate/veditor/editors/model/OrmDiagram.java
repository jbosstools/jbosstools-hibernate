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
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
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
	private HashMap<String,Shape> elements = new HashMap<String,Shape>();
	private IOrmElement  ormElement;
	
	
	public OrmDiagram(IOrmElement ioe) {
		ormElement = ioe;
		if (ormElement instanceof IPersistentClassMapping)
			ormElement = ((IPersistentClassMapping)ormElement).getPersistentClass();
		if (ormElement instanceof IPersistentClass) {
			String string = "";
			resource =((IPersistentClass)ormElement).getPersistentClassMapping().getStorage().getResource();
			try {
				int i = 0;
				String tempString;
				do {
					tempString = resource.getPersistentProperty(new QualifiedName(VizualEditorPlugin.PLUGIN_ID,qualifiedNameString+i++));					
					string += tempString;
				} while (tempString != null);
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage());
			}
			childrenLocations = string.split("#");
		} else
			throw new IllegalArgumentException();
		getOrCreatePersistentClass((IPersistentClass)ormElement, null);
		
	}
	
	public List getChildren() {
		return shapes;
	}
	
	public HashMap getCloneElements() {
		return (HashMap)elements.clone();
	}

	public IOrmElement getOrmElement() {
		return ormElement;
	}

	public void refresh() {
		saveHelper();
		shapes.clear();
		elements.clear();
		if( ((IPersistentClass)ormElement).getProjectMapping().findClass(ormElement.getName()) != null)
			getOrCreatePersistentClass((IPersistentClass)ormElement, null);
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
				ExceptionHandler.logThrowableError(e, e.getMessage());
			}
	}
	
	private void saveHelper() {
		childrenLocations = new String[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) {
			OrmShape shape = (OrmShape) shapes.get(i);
			childrenLocations[i] = shape.getOrmElement().getName()+"@" +
			shape.getLocation().x+";" +
			shape.getLocation().y+";" +
			shape.isHiden();
		}
	}
	
//	private void restoreOld() {
//		HashMap hashMap = (HashMap)elements.clone();
//		int tempPoint = 1;
//		OrmShape ormShape;
//		int point = 1;
//		String string, xy[];
//		for (int i = 0; i < childrenLocations.length; i++) 
//			if (childrenLocations[i].indexOf('@') != -1 && childrenLocations[i].indexOf(';') != -1){
//				string = childrenLocations[i].substring(0,childrenLocations[i].indexOf('@'));
//				ormShape = (OrmShape)hashMap.remove(string);
//				if (ormShape != null) {
//					string = childrenLocations[i].substring(childrenLocations[i].indexOf('@')+1);
//					xy = string.split(";");
//					if(xy.length>1)
//						try {
//							ormShape.setLocation(new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1])));							
//						} catch (NumberFormatException  e) {}
//					if(xy.length>2)
//						if((new Boolean(xy[2])).booleanValue())
//							ormShape.refreshHiden();
//					tempPoint = ormShape.getLocation().y + 17*(ormShape.getHeight() + 1) + 40;
//					if(tempPoint > point)
//						point = tempPoint;
//				}
//			}
//		if (ormElement instanceof IPersistentClass) {
//			IPersistentClass persistentClass = (IPersistentClass)ormElement;
//			ormShape = (OrmShape)hashMap.remove(persistentClass.getName());
//			if (ormShape != null) {
//				ormShape.setLocation(new Point(20,20));	
//				tempPoint = 40 + 17*(ormShape.getHeight() + 1);
//				if(tempPoint > point)
//					point = tempPoint;
//				
//			}
//			if (persistentClass.getPersistentClassMapping() != null) {
//				ormShape =  (OrmShape)hashMap.remove(persistentClass.getPersistentClassMapping().getDatabaseTable().getName());
//				if (ormShape != null) {
//					ormShape.setLocation(new Point(350,20));
//					tempPoint = 40 + 17*(ormShape.getHeight() + 1);
//					if(tempPoint > point)
//						point = tempPoint;
//				}
//			}
//		}
//		Object objects[] = hashMap.keySet().toArray();
//		for (int i = 0; i < objects.length; i++) {
//			ormShape = (OrmShape)hashMap.get(objects[i]);
//			if (ormShape != null && ormShape.getOrmElement() instanceof IPersistentClass) {	
//				ormShape.setLocation(new Point(20,point));
//				tempPoint = point + 17*(ormShape.getHeight() + 1) + 20;
//				ormShape = (OrmShape)hashMap.remove(((IPersistentClass)ormShape.getOrmElement()).getPersistentClassMapping().getDatabaseTable().getName());
//				if (ormShape != null ) {
//					ormShape.setLocation(new Point(350,point));
//					point = point + 17*(ormShape.getHeight() + 1) + 20;
//				}
//				if(tempPoint > point)
//					point = tempPoint;
//			}
//		}
//		Iterator iterator = hashMap.values().iterator();
//		while (iterator.hasNext()) {
//			ormShape = (OrmShape) iterator.next();
//			if (ormShape.getOrmElement() instanceof IDatabaseTable)	{
//				ormShape.setLocation(new Point(350,point));				
//				point = point + 17*(ormShape.getHeight() + 1) + 20;
//			}
//		}
//	}
	
	private OrmShape createShape(IOrmElement ormElement) {
		OrmShape ormShape = new OrmShape(ormElement);
		shapes.add(ormShape);
		elements.put(ormElement.getName(),ormShape);
		return ormShape;
	}
	
	private OrmShape getOrCreatePersistentClass(IPersistentClass persistentClass, IDatabaseTable componentClassDatabaseTable){
		OrmShape classShape = null;
		OrmShape shape = null;
		if(persistentClass != null) {
			classShape = createShape(persistentClass);
			if(componentClassDatabaseTable == null && persistentClass.getPersistentClassMapping() != null)
				componentClassDatabaseTable = persistentClass.getPersistentClassMapping().getDatabaseTable();	
			if(componentClassDatabaseTable != null) {
				shape =  (OrmShape)elements.get(componentClassDatabaseTable.getName());
				if(shape == null)
					shape = getOrCreateDatabaseTable(componentClassDatabaseTable);
				createConnections(classShape, shape);
				new Connection(classShape, shape);
			}
			if (persistentClass.getPersistentClassMapping() != null) {
				Iterator iter =((IHibernateClassMapping)(persistentClass).getPersistentClassMapping()).getJoinIterator();			
				while ( iter.hasNext() ) {
					IJoinMapping jm =(IJoinMapping)iter.next();
					shape =  (OrmShape)elements.get(jm.getTable().getName());
					if(shape == null)
						shape = getOrCreateDatabaseTable(jm.getTable());
					createConnections(classShape, shape);
				}
			}
		}
		return classShape;
	}
	
	private OrmShape getOrCreateDatabaseTable(IDatabaseTable databaseTable){
		OrmShape tableShape = null;
		if(databaseTable != null) {
			tableShape = (OrmShape)elements.get(databaseTable.getName());
			if(tableShape == null) {
				tableShape = createShape(databaseTable);				
				IPersistentClassMapping persistentClassMappings[] = databaseTable.getPersistentClassMappings();
				for (int j = 0; j < persistentClassMappings.length; j++) {
					if(persistentClassMappings[j].getPersistentClass() != null ) {
						OrmShape shape =  (OrmShape)elements.get(persistentClassMappings[j].getPersistentClass().getName());
						if(shape == null)
							getOrCreatePersistentClass(persistentClassMappings[j].getPersistentClass(), null);
					}
				}
			}				
		}
		return tableShape;
	}
	
	
	private void createConnections(ExtendedShape persistentClass, ExtendedShape databaseTable){
		int i = 0;
		Iterator persistentFields = persistentClass.getChildren().iterator();
		List<Shape> databaseColumns = databaseTable.getChildren();
		List<Shape> databaseColumns2 = new ArrayList<Shape>();
		IPersistentValueMapping valueMapping;
		ICollectionMapping collectionMapping;
		IComponentMapping componentMapping;
		IOneToManyMapping oneToManyMapping;
		Iterator iterator;
		OrmShape childShape;
		while (persistentFields.hasNext()) {
			Shape shape = (Shape) persistentFields.next();
			valueMapping = null;
			if (shape.getOrmElement() instanceof IPersistentField && ((IPersistentField)shape.getOrmElement()).getMapping() != null)
				valueMapping =((IPersistentField)shape.getOrmElement()).getMapping().getPersistentValueMapping();
			else if (shape.getOrmElement() instanceof IPersistentValueMapping)
				valueMapping = (IPersistentValueMapping)shape.getOrmElement();
			if (valueMapping != null)
				if (valueMapping instanceof ICollectionMapping) {
					collectionMapping = (ICollectionMapping)valueMapping;
					valueMapping = collectionMapping.getKey();
					childShape =  (OrmShape)elements.get(valueMapping.getTable().getName());
					if(childShape == null)
						childShape = getOrCreateDatabaseTable(valueMapping.getTable());
					createConnections((ExtendedShape)shape,childShape);
				}else if (valueMapping instanceof IComponentMapping) {
					componentMapping = (IComponentMapping)valueMapping;
					childShape =  (OrmShape)elements.get(componentMapping.getComponentClass().getName());
					if(childShape == null)
						childShape = getOrCreatePersistentClass(componentMapping.getComponentClass(), componentMapping.getTable());
					new Connection(shape, childShape);								
				} else if (valueMapping instanceof IOneToManyMapping) {
					oneToManyMapping = (IOneToManyMapping)valueMapping;
					if(oneToManyMapping.getAssociatedClass() != null) {
						childShape =  (OrmShape)elements.get(oneToManyMapping.getAssociatedClass().getPersistentClass().getName());
						if(childShape == null)
							childShape = getOrCreatePersistentClass(oneToManyMapping.getAssociatedClass().getPersistentClass(), null);
						new Connection(shape, childShape);								
					}
				} else	if (valueMapping instanceof IOneToOneMapping){
					IOneToOneMapping oneToOneMapping = (IOneToOneMapping)valueMapping;
					if(oneToOneMapping.getReferencedEntityName() != null) {
						childShape =  (OrmShape)elements.get(oneToOneMapping.getReferencedEntityName());
						if(childShape != null)
							new Connection(shape, childShape);								
					}
					
				} else {
					iterator = valueMapping.getColumnIterator();
					while (iterator.hasNext()) {
						IDatabaseColumn databaseColumn = (IDatabaseColumn) iterator.next();
						for (int j = 0; j < databaseColumns.size(); j++) {
							//TODO mk
							if (databaseColumn.getName().equals(((Shape)databaseColumns.get(j)).getOrmElement().getName())) {
								Shape databaseShape = (Shape)databaseColumns.remove(j);
								new Connection(shape, databaseShape);
								databaseColumns2.add(i++,databaseShape);
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
}