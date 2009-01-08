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
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.persistence.ClassRef;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.Property;
import org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.core.resource.persistence.XmlProperties;
import org.eclipse.jpt.core.resource.persistence.XmlProperty;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneIterator;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;

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
	@Override
	public void addToMessages(List<IMessage> messages) {
		super.addToMessages(messages);
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
		for (String annotClass : CollectionTools.iterable(getJpaProject().annotatedClassNames())) {
			ClassRef classRef = buildClassRef(annotClass);
			JavaPersistentType type = classRef.getJavaPersistentType();
			ListIterator<JavaPersistentAttribute> typeAttrs = type.attributes();
			for (JavaPersistentAttribute persAttr : CollectionTools.iterable(typeAttrs)) {
				JavaResourcePersistentAttribute jrpa = persAttr.getResourcePersistentAttribute();
				GenericGeneratorAnnotation annotation = (GenericGeneratorAnnotation) jrpa.getAnnotation(GENERIC_GENERATOR);
				if (annotation != null){
					JavaGenericGenerator generator = annotation.buildJavaGenericGenerator(persAttr.getSpecifiedMapping());
					addGenerator(generator);
				}
			}
		}
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
