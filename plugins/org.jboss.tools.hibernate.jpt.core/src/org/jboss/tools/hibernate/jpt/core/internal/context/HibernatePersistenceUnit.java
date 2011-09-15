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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.NotNullFilter;
import org.eclipse.jpt.common.utility.internal.iterables.CompositeIterable;
import org.eclipse.jpt.common.utility.internal.iterables.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterables.TransformationIterable;
import org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.Generator;
import org.eclipse.jpt.jpa.core.context.GeneratorContainer;
import org.eclipse.jpt.jpa.core.context.Query;
import org.eclipse.jpt.jpa.core.context.QueryContainer;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.internal.context.persistence.AbstractPersistenceUnit;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.HibernatePersistenceUnitProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaPackageInfo;
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
	public HibernatePersistenceUnitProperties getHibernatePersistenceUnitProperties() {
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
	 * Return the non-<code>null</code> class ref package infos,
	 * both specified and implied.
	 */
	protected Iterable<JavaPackageInfo> getClassRefPackageInfos() {
		return new FilteringIterable<JavaPackageInfo>(
					this.getClassRefPackageInfos_(),
					NotNullFilter.<JavaPackageInfo>instance()
				);
	}
	
	protected Iterable<JavaPackageInfo> getClassRefPackageInfos_() {
		return new TransformationIterable<ClassRef, JavaPackageInfo>(this.getClassRefs()) {
			@Override
			protected JavaPackageInfo transform(ClassRef classRef) {
				return ((HibernateClassRef)classRef).getJavaPackageInfo();
			}
		};
	}
	
	/**
	 * Need to collect also Queries from package-info.java
	 */
	protected void addJavaQueriesTo(ArrayList<Query> queryList) {
		super.addJavaQueriesTo(queryList);
		this.addPackageJavaQueriesTo(this.getClassRefPackageInfos(), queryList);
	}

	protected void addPackageJavaQueriesTo(Iterable<JavaPackageInfo> packageInfos, ArrayList<Query> queryList) {
		for (JavaPackageInfo packageInfo : packageInfos) {
			if (packageInfo instanceof HibernatePackageInfo) {
				this.addQueriesTo(((HibernatePackageInfo) packageInfo).getQueryContainer(), queryList);
			}
		}
	}
	
	@Override
	protected void addQueriesTo(QueryContainer queryContainer,
			ArrayList<Query> queryList) {
		super.addQueriesTo(queryContainer, queryList);
		if (queryContainer instanceof HibernateJavaQueryContainer) {
			CollectionTools.addAll(queryList, ((HibernateJavaQueryContainer)queryContainer).hibernateNamedQueries());
			CollectionTools.addAll(queryList, ((HibernateJavaQueryContainer)queryContainer).hibernateNamedNativeQueries());
		}
	}
	
	/**
	 * Include "overridden" Java generators.
	 */
	protected void addJavaGeneratorsTo(ArrayList<Generator> generatorList) {
		super.addJavaGeneratorsTo(generatorList);
		this.addPackageJavaGeneratorsTo(this.getClassRefPackageInfos(), generatorList);
	}
	
	protected void addPackageJavaGeneratorsTo(Iterable<JavaPackageInfo> pacakgeInfos, ArrayList<Generator> generatorList) {
		for (JavaPackageInfo packageInfo : pacakgeInfos) {
			if (packageInfo instanceof HibernatePackageInfo) {
				this.addGeneratorsTo(((HibernatePackageInfo) packageInfo).getGeneratorContainer(), generatorList);
			}
		}
	}
	
	@Override
	protected void addGeneratorsTo(GeneratorContainer generatorContainer,
			ArrayList<Generator> generatorList) {
		super.addGeneratorsTo(generatorContainer, generatorList);
		//it could be orm generators container
		//which is not implemented for hibernate platform
		if (generatorContainer instanceof HibernateGeneratorContainer) {
			CollectionTools.addAll(generatorList, ((HibernateGeneratorContainer)generatorContainer).genericGenerators());
		}
	}
	
	/**
	 * Return the names of all the Java classes and packages in the JPA project that are
	 * mapped (i.e. have the appropriate annotation etc.) but not specified
	 * in the persistence unit.
	 */
	@SuppressWarnings("unchecked")
	protected Iterable<String> getImpliedClassNames_() {
		return new CompositeIterable<String>(super.getImpliedClassNames_(),
				new FilteringIterable<String>(this.getJpaProject().getMappedJavaSourcePackagesNames()) {
					@Override
					protected boolean accept(String mappedPackageName) {
						return !HibernatePersistenceUnit.this.specifiesPackageInfo(mappedPackageName);
					}
				}
		);

	}
	
	/**
	 * Ignore implied class refs and jar files.
	 */
	public boolean specifiesPackageInfo(String typeName) {
		for (ClassRef classRef : this.getSpecifiedClassRefs()) {
			if (classRef.isFor(typeName)) {
				return true;
			}
		}
		/*for (MappingFileRef mappingFileRef : this.getMappingFileRefs()) {
			if (mappingFileRef.getPersistentType(typeName) != null) {
				return true;
			}
		}*/
		return false;
	}

	// ********** Validation ***********************************************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		validateHibernateConfigurationFileExists(messages, reporter);
	}

	protected void validateHibernateConfigurationFileExists(List<IMessage> messages, IReporter reporter) {
		String configFile = this.hibernateProperties.getConfigurationFile();
		if (configFile != null && configFile.length() > 0){
			IPath path = new Path(configFile);

			if (new File(path.toOSString()).exists()) return;

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

			IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
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



}
