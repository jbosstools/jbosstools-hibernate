/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.persistence.ClassRef;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.Property;
import org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.core.resource.persistence.XmlProperties;
import org.eclipse.jpt.core.resource.persistence.XmlProperty;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneIterator;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceUnit extends GenericPersistenceUnit 
	implements Messages, Hibernate {
	
	private HibernateProperties hibernateProperties;

	/**
	 * @param parent
	 * @param persistenceUnit
	 */
	public HibernatePersistenceUnit(Persistence parent,
			XmlPersistenceUnit persistenceUnit) {
		super(parent, persistenceUnit);
	}
	
	protected void initialize(XmlPersistenceUnit xmlPersistenceUnit) {
		super.initialize(xmlPersistenceUnit);
		updateGenericGenerators();
		this.hibernateProperties = new HibernateJpaProperties(this);
	}

	// ******** Behavior *********
	public BasicHibernateProperties getBasicProperties() {
		return this.hibernateProperties.getBasicHibernate();
	}

	// ********** Validation ***********************************************
	public void validate(List<IMessage> messages) {
		addToMessages(messages);
	}	
	
	public void addToMessages(List<IMessage> messages) {
		invokeMethod(this, "addMappingFileMessages", "validateMappingFiles", 
				new Class[]{List.class}, messages);
		invokeMethod(this, "addClassMessages", "validateClassRefs", new Class[]{List.class}, messages);
		addFileNotExistsMessages(messages);
	}
	
	protected void addFileNotExistsMessages(List<IMessage> messages) {
		String configFile = getBasicProperties().getConfigurationFile();
		if (configFile != null && configFile.length() > 0){
			IPath path = new Path(configFile);
				
			if (new File(path.toOSString()).exists()) return;

		    IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		    if (res != null) {
		        int resType= res.getType();
		        if (resType != IResource.FILE) {
		        	Property prop = getProperty(BasicHibernateProperties.HIBERNATE_CONFIG_FILE);
	            	IMessage message = new LocalMessage(Messages.class.getName(), IMessage.HIGH_SEVERITY, 
	            			NOT_A_FILE, new String[]{configFile}, getResource());
	            	message.setLineNo(prop.getValidationTextRange().getLineNumber());
	            	messages.add(message);					
		        }
		    } else {
		    	Property prop = getProperty(BasicHibernateProperties.HIBERNATE_CONFIG_FILE);
	        	IMessage message = new LocalMessage(Messages.class.getName(), IMessage.HIGH_SEVERITY, 
            			CONFIG_FILE_NOT_FOUND, new String[]{configFile}, getResource());
	        	message.setLineNo(prop.getValidationTextRange().getLineNumber());
            	messages.add(message);	
		    }
		}
	}
	
	
	/**
	 * If persistence.xml contains 3 properties: dialect, driver, url and we delete
	 * driver-property, then parent's method changes current properties so:
	 * 1. replace 1-st model's property(dialect) with 1-st property from persistence.xml (dialect)
	 * 2. replace 2-nd model's property(driver) with 2-nd property from persistence.xml (url)
	 * 3. remove 3-rd model's property(url)
	 * Step 3 call property change event which set url=null and this is wrong.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=255149
	 * TODO: remove after bug fixed.
	 */
	@Override
	protected void updateProperties(XmlPersistenceUnit persistenceUnit) {
		XmlProperties xmlProperties = persistenceUnit.getProperties();
		
		Iterator<Property> stream = properties();
		Iterator<XmlProperty> stream2;
		
		if (xmlProperties == null) {
			stream2 = EmptyIterator.instance();
		}
		else {
			stream2 = new CloneIterator<XmlProperty>(xmlProperties.getProperties());//avoid ConcurrentModificationException
		}
		
		Map<String, XmlProperty> map = new HashMap<String, XmlProperty>();
		while (stream2.hasNext()){
			XmlProperty xmlProp = stream2.next();
			map.put(xmlProp.getName(), xmlProp);
		}
		Set<String> performedSet = new HashSet<String>();
		while (stream.hasNext()) {
			Property property = stream.next();
			if (map.containsKey(property.getName())) {
				XmlProperty xmlProp = map.get(property.getName());
				property.update(xmlProp);
				performedSet.add(property.getName());
			} else {
				removeProperty_(property);
			}
		}		
		
		for (Entry<String, XmlProperty> entry : map.entrySet()) {
			if (!performedSet.contains(entry.getKey())) {
				addProperty_(buildProperty(entry.getValue()));
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit#update(org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit)
	 */
	@Override
	public void update(XmlPersistenceUnit persistenceUnit) {
		super.update(persistenceUnit);
		updateGenericGenerators();
	}
	
	protected void updateGenericGenerators(){
		JpaProject project = getJpaProject();
		
		for (ClassRef classRef : CollectionTools.iterable(classRefs())) {			
			String annotClass = classRef.getClassName();
			JavaPersistentType type = classRef.getJavaPersistentType();
			JavaResourcePersistentMember jrpt = null;
			GenericGeneratorAnnotation annotation = null;
			jrpt =	(JavaResourcePersistentMember) invokeMethod(project, "getJavaPersistentTypeResource", 
					"getJavaResourcePersistentType", new Class[]{String.class}, annotClass);
			if (jrpt != null){
				annotation = (GenericGeneratorAnnotation)invokeMethod(jrpt, "getAnnotation", 
						"getSupportingAnnotation", new Class[]{String.class}, GENERIC_GENERATOR);
				if (annotation != null) {
					addGenerator(annotation.buildJavaGenericGenerator(type));
				}				
				ListIterator<JavaPersistentAttribute> typeAttrs = type.attributes();
				for (JavaPersistentAttribute persAttr : CollectionTools.iterable(typeAttrs)) {
					JavaResourcePersistentAttribute jrpa = persAttr.getResourcePersistentAttribute();
					annotation = (GenericGeneratorAnnotation)invokeMethod(jrpa, "getAnnotation", 
							"getSupportingAnnotation", new Class[]{String.class}, GENERIC_GENERATOR);
					
					if (annotation != null) {
						addGenerator(annotation.buildJavaGenericGenerator(persAttr.getSpecifiedMapping()));
					}
				}				
			}			
		}
	}
	
	/**
	 * Hack method needed to make Hibernate Platform portable between Dali 2.0 and Dali 2.1
	 * @param object - object on which method will be called
	 * @param dali20Name - method name in Dali 2.0
	 * @param dali21Name - same method name in Dali 2.1
	 * @param paramTypes - method arguments types.
	 * @param args - arguments of the method
	 * @return
	 */
	private Object invokeMethod(Object object, String dali20Name, String dali21Name, Class[] argsTypes,
			Object... args){
		Method method = getMethod(object.getClass(), dali20Name, dali21Name, argsTypes);			
		if (method != null){
			try {
				return method.invoke(object, args);
			} catch (IllegalArgumentException e) {
				HibernateJptPlugin.logException(e);
			} catch (IllegalAccessException e) {
				HibernateJptPlugin.logException(e);
			} catch (InvocationTargetException e) {
				HibernateJptPlugin.logException(e);
			}
		} else {
			StringBuilder params = new StringBuilder();
			for (int i = 0; i < argsTypes.length; i++) {
				params.append(argsTypes[i].getName() + ", ");
			}
			if (params.length() > 0) params.deleteCharAt(params.length() - 2);
			HibernateJptPlugin.logError("Nor \"" + dali20Name + "\" nor \"" + dali21Name
					+ "\" methods were found with parameter types: (" + params + ")");
		}
		return null;
	}
	
	/**
	 * Hack method needed to make Hibernate Platform portable between Dali 2.0 and Dali 2.1
	 * @param parent
	 * @param dali20Name - method name in Dali 2.0
	 * @param dali21Name - same method name in Dali 2.1
	 * @param parameterTypes
	 * @return
	 */
	private Method getMethod(Class parent, String dali20Name, String dali21Name, Class... parameterTypes){
		Class clazz = parent;
		while (clazz != null){
			Method method = null;
			try {//try to get method from Dali 2.0
				method = clazz.getDeclaredMethod(dali20Name, parameterTypes);
				return method;
			} catch (Exception e) {
				try {//try to get method from Dali 2.1
					method = clazz.getDeclaredMethod(dali21Name, parameterTypes);
					return method;
				} catch (Exception e1) {
					clazz = clazz.getSuperclass();
				}				
			}
		}
		return null;
	}


	/**
	 * Hack class needed to make JPA/Validation API pick up our classloader instead of its own.
	 * 
	 * @author max
	 *
	 */
	static public class LocalMessage extends Message {

		public LocalMessage(String name, int highSeverity, String notAFile,
				String[] strings, IResource resource) {
			super(name, highSeverity, notAFile, strings, resource);
		}
	}
	
}
