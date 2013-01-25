/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.utility.internal.filter.NotNullFilter;
import org.eclipse.jpt.common.utility.internal.iterable.CompositeIterable;
import org.eclipse.jpt.common.utility.internal.iterable.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterable.SubIterableWrapper;
import org.eclipse.jpt.common.utility.internal.iterable.TransformationIterable;
import org.eclipse.jpt.common.utility.internal.iterator.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.Generator;
import org.eclipse.jpt.jpa.core.context.java.JavaGenerator;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.internal.GenericJpaJpqlQueryHelper;
import org.eclipse.jpt.jpa.core.internal.context.persistence.AbstractPersistenceUnit;
import org.eclipse.jpt.jpa.core.jpql.JpaJpqlQueryHelper;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.HibernatePersistenceUnitProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDef;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernateClassRef;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.HibernatePersistenceUnitPropertiesBuilder;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceUnit extends AbstractPersistenceUnit
implements Messages, Hibernate {

	public String TYPE_DEF_LIST = "typeDefs"; //$NON-NLS-1$

	private HibernatePersistenceUnitProperties hibernateProperties;

	/* global type def definitions, defined elsewhere in model */
	protected final Vector<JavaTypeDef> typeDefs = new Vector<JavaTypeDef>();

	/**
	 * @param parent
	 * @param persistenceUnit
	 */
	public HibernatePersistenceUnit(Persistence parent,
			XmlPersistenceUnit persistenceUnit) {
		super(parent, persistenceUnit);
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject)super.getJpaProject();
	}

	@Override
	protected void addNonUpdateAspectNamesTo(Set<String> nonUpdateAspectNames) {
		super.addNonUpdateAspectNamesTo(nonUpdateAspectNames);
		nonUpdateAspectNames.add(this.TYPE_DEF_LIST);
	}

	@Override
	public void update() {
		this.typeDefs.clear();
		super.update();
		this.fireListChanged(this.TYPE_DEF_LIST, this.typeDefs);
	}

	@Override
	protected void initializeProperties() {
		super.initializeProperties();
		this.hibernateProperties = ((HibernatePersistenceUnitPropertiesBuilder)this.getContextNodeFactory())
		.buildHibernatePersistenceUnitProperties(this);
	}

	@Override
	public void propertyRemoved(String propertyName) {
		super.propertyRemoved(propertyName);
		this.hibernateProperties.propertyRemoved(propertyName);
	}

	@Override
	public void propertyValueChanged(String propertyName, String newValue) {
		super.propertyValueChanged(propertyName, newValue);
		this.hibernateProperties.propertyValueChanged(propertyName, newValue);
	}

	// ******** Behavior *********
	public BasicHibernateProperties getHibernatePersistenceUnitProperties() {
		return this.hibernateProperties;
	}

	// ******** Type Def *********

	public ListIterator<JavaTypeDef> typeDefs() {
		return new CloneListIterator<JavaTypeDef>(this.typeDefs);
	}

	public int typeDefsSize() {
		return this.typeDefs.size();
	}

	public void addTypeDef(JavaTypeDef typeDef) {
		this.typeDefs.add(typeDef);
	}

	public String[] uniqueTypeDefNames() {
		HashSet<String> names = new HashSet<String>(this.typeDefs.size());
		this.addNonNullTypeDefNamesTo(names);
		return names.toArray(new String[names.size()]);
	}

	public boolean hasTypeDef(String name) {
		for (Iterator<JavaTypeDef> stream = this.typeDefs(); stream.hasNext(); ) {
			String typeDefName = stream.next().getName();
			if (name.equals(typeDefName)) {
				return true;
			}
		}
		return false;
	}

	protected void addNonNullTypeDefNamesTo(Set<String> names) {
		for (Iterator<JavaTypeDef> stream = this.typeDefs(); stream.hasNext(); ) {
			String typeDefName = stream.next().getName();
			if (typeDefName != null) {
				names.add(typeDefName);
			}
		}
	}
	
	/**
	 * Return the names of all the Java classes and packages in the JPA project that are
	 * mapped (i.e. have the appropriate annotation etc.) but not specified
	 * in the persistence unit.
	 */

	// FIXME Find what was changed related to this function
	//	@SuppressWarnings("unchecked")
//	@Override
//	protected Iterable<String> getImpliedClassNames_() {
//		return new CompositeIterable<String>(super.getImpliedClassNames_(),
//				new FilteringIterable<String>(this.getJpaProject().getMappedJavaSourcePackagesNames()) {
//					@Override
//					protected boolean accept(String mappedPackageName) {
//						return !HibernatePersistenceUnit.this.specifiedPackageInfo(mappedPackageName);
//					}
//				}
//		);
//	}
	
	
	
	/**
	 * Ignore implied class refs and jar files.
	 */
	public boolean specifiedPackageInfo(String typeName) {
		for (ClassRef classRef : this.getSpecifiedClassRefs()) {
			if (classRef.isFor(typeName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ignore implied class refs and jar files.
	 */
	public boolean impliedPackageInfo(String typeName) {
		for (ClassRef classRef : this.getImpliedClassRefs()) {
			if (classRef.isFor(typeName)) {
				return true;
			}
		}
		return false;
	}

	// ********** Validation ***********************************************
	@Override
	protected void validateProperties(List<IMessage> messages, IReporter reporter) {
		String configFile = this.hibernateProperties.getConfigurationFile();
		if (configFile != null && configFile.length() > 0){
			IPath path = new Path(configFile);

			try {
				IJavaProject jp = getJpaProject().getJavaProject();
				IPackageFragmentRoot[] pfrs = jp.getPackageFragmentRoots();
				for (int i = 0; i < pfrs.length; i++) {
					if (pfrs[i].isArchive()) continue;
					if (((IContainer)pfrs[i].getResource()).findMember(path) != null){
						return;
					}
				}
			} catch (JavaModelException e) {
				HibernateJptPlugin.logException(e);
			}

			IJavaProject jProject = getJpaProject().getJavaProject();
			IResource res = null;
			if (jProject != null){
				try {
					IPackageFragmentRoot[] allPackageFragmentRoots = jProject.getAllPackageFragmentRoots();
					for (IPackageFragmentRoot iPackageFragmentRoot : allPackageFragmentRoots) {
						if (!iPackageFragmentRoot.isArchive()){
							IResource sourceFolder = iPackageFragmentRoot.getResource();
							if (sourceFolder instanceof IContainer) {
								IContainer folder = (IContainer) sourceFolder;
								if ((res = folder.findMember(path)) != null){
									break;
								}
							}
						}
					}
				} catch (JavaModelException e) {
					//ignore
				}
			}
			if (res != null) {
				int resType= res.getType();
				if (resType != IResource.FILE) {
					Property prop = getProperty(BasicHibernateProperties.HIBERNATE_CONFIG_FILE);
					IMessage message = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
							NOT_A_FILE, new String[]{configFile}, getResource());
					message.setLineNo(prop.getValidationTextRange().getLineNumber());
					messages.add(message);
				}
			} else {
				Property prop = getProperty(BasicHibernateProperties.HIBERNATE_CONFIG_FILE);
				IMessage message = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
						CONFIG_FILE_NOT_FOUND, new String[]{configFile}, getResource());
				message.setLineNo(prop.getValidationTextRange().getLineNumber());
				messages.add(message);
			}
		}
	}
	
	@Override
	public JpaJpqlQueryHelper createJpqlQueryHelper() {
		return new GenericJpaJpqlQueryHelper(this.getJpaPlatform().getJpqlGrammar());
	}
	
	@SuppressWarnings("unchecked")
	protected Iterable<JavaGenerator> getAllJavaGenerators() {
		return new CompositeIterable<JavaGenerator>(
				new CompositeIterable<JavaGenerator>(this.getAllJavaTypeMappingGeneratorLists()),
				new CompositeIterable<JavaGenerator>(this.getAllPackageInfoMappingGeneratorLists()));
	}

	/**
	 * @return
	 */
	protected Iterable<Iterable<JavaGenerator>>  getAllPackageInfoMappingGeneratorLists() {
		return new TransformationIterable<HibernatePackageInfo, Iterable<JavaGenerator>>(this.getClassRefPackageInfos_()) {
			@Override
			protected Iterable<JavaGenerator> transform(HibernatePackageInfo o) {
				return new SubIterableWrapper<Generator, JavaGenerator>(this.transform_(o));
			}
			protected Iterable<Generator> transform_(HibernatePackageInfo o) {
				return o.getGeneratorContainer().getGenerators();
			}
		};
	}

	
	protected Iterable<HibernatePackageInfo> getClassRefPackageInfos_() {
		return new FilteringIterable<HibernatePackageInfo>(
				new TransformationIterable<HibernateClassRef, HibernatePackageInfo>(
						new SubIterableWrapper<ClassRef,HibernateClassRef>(this.getClassRefs())) {
					@Override
					protected HibernatePackageInfo transform(HibernateClassRef classRef) {
						return classRef.getJavaPackageInfo();
					}
				},
				NotNullFilter.INSTANCE
		);
	}
	

}
