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
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.persistence.ClassRef;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
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
		updateGenericGenerators();
		this.hibernateProperties = new HibernateJpaProperties(this);
	}

	// ******** Behavior *********
	public BasicHibernateProperties getBasicProperties() {
		return this.hibernateProperties.getBasicHibernate();
	}

	// ********** Validation ***********************************************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		validateHibernateConfigurationFileExists(messages, reporter);
	}	

	protected void validateHibernateConfigurationFileExists(List<IMessage> messages, IReporter reporter) {
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.internal.context.persistence.GenericPersistenceUnit#update(org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit)
	 */
	@Override
	public void update(XmlPersistenceUnit persistenceUnit) {
		super.update(persistenceUnit);
		updateGenericGenerators();
		this.fireListChanged(GENERATORS_LIST);
	}
	
	
	protected void updateGenericGenerators(){
		JpaProject project = getJpaProject();
		
		for (ClassRef classRef : CollectionTools.iterable(classRefs())) {			
			String annotClass = classRef.getClassName();
			JavaPersistentType type = classRef.getJavaPersistentType();
			JavaResourcePersistentType jrpt = project.getJavaResourcePersistentType(annotClass);
			if (jrpt != null){
				GenericGeneratorAnnotation annotation = null;
				JavaResourceNode jrn = jrpt.getSupportingAnnotation(GENERIC_GENERATOR);
				if (jrn instanceof GenericGeneratorAnnotation) {
					annotation = (GenericGeneratorAnnotation)jrn;
				}
				if (annotation != null) {
					addGenerator(((HibernateJpaFactory)getJpaFactory()).buildJavaGenericGenerator(type));
				}				
				ListIterator<JavaPersistentAttribute> typeAttrs = type.attributes();
				for (JavaPersistentAttribute persAttr : CollectionTools.iterable(typeAttrs)) {
					if (persAttr.getSpecifiedMapping() == null) {
						continue;
					}
					JavaResourcePersistentAttribute jrpa = persAttr.getResourcePersistentAttribute();
					jrn = jrpa.getSupportingAnnotation(GENERIC_GENERATOR);
					if (jrn instanceof GenericGeneratorAnnotation) {
						annotation = (GenericGeneratorAnnotation)jrn;
					}
					if (annotation != null) {
						addGenerator(((HibernateJpaFactory)getJpaFactory()).buildJavaGenericGenerator(type));
					}
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
