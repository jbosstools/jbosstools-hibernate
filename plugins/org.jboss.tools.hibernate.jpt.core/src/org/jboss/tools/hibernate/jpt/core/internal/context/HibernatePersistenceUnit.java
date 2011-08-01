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
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
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
					IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY,
							NOT_A_FILE, new String[]{configFile}, getResource());
					message.setLineNo(prop.getValidationTextRange().getLineNumber());
					messages.add(message);
				}
			} else {
				Property prop = getProperty(BasicHibernateProperties.HIBERNATE_CONFIG_FILE);
				IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY,
						CONFIG_FILE_NOT_FOUND, new String[]{configFile}, getResource());
				message.setLineNo(prop.getValidationTextRange().getLineNumber());
				messages.add(message);
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

		public LocalMessage(int severity, String message,
				String[] strings, Object resource) {
			super(Messages.class.getName(), severity, message, strings, resource);
		}
	}
//	
//	// ********** specified package-info refs **********
//
//	public ListIterator<PackageInfoRef> specifiedPackageInfoRefs() {
//		return this.getSpecifiedPackageInfoRefs().iterator();
//	}
//
//	protected ListIterable<PackageInfoRef> getSpecifiedPackageInfoRefs() {
//		return new LiveCloneListIterable<PackageInfoRef>(this.specifiedPackageInfoRefs);
//	}
//
//	public int specifiedPackageInfoRefsSize() {
//		return this.specifiedPackageInfoRefs.size();
//	}
//
//	public PackageInfoRef addSpecifiedPackageInfoRef(String packageName) {
//		return this.addSpecifiedPackageInfoRef(this.specifiedPackageInfoRefs.size(), packageName);
//	}
//
//	public PackageInfoRef addSpecifiedPackageInfoRef(int index, String packageName) {
//		XmlPackageInfoRef xmlPackageInfoRef = this.buildXmlPackageInfoRef(packageName);
//		PackageInfoRef classRef = this.addSpecifiedPackageInfoRef_(index, xmlPackageInfoRef);
//		this.xmlPersistenceUnit.getClasses().add(index, xmlPackageInfoRef);
//		return classRef;
//	}
//
//	protected XmlPackageInfoRef buildXmlPackageInfoRef(String packageName) {
//		XmlPackageInfoRef ref = PersistenceFactory.eINSTANCE.createXmlPackageInfoRef();
//		ref.setJavaClass(packageName);
//		return ref;
//	}
//
//	protected PackageInfoRef buildPackageInfoRef(XmlPackageInfoRef xmlPackageInfoRef) {
//		return this.getContextNodeFactory().buildPackageInfoRef(this, xmlPackageInfoRef);
//	}
//
//	public void removeSpecifiedPackageInfoRef(PackageInfoRef classRef) {
//		this.removeSpecifiedPackageInfoRef(this.specifiedPackageInfoRefs.indexOf(classRef));
//	}
//
//	public void removeSpecifiedPackageInfoRef(int index) {
//		this.removeSpecifiedPackageInfoRef_(index);
//		this.xmlPersistenceUnit.getClasses().remove(index);
//	}
//
//	/**
//	 * dispose the class ref
//	 */
//	protected void removeSpecifiedPackageInfoRef_(int index) {
//		this.removeItemFromList(index, this.specifiedPackageInfoRefs, SPECIFIED_CLASS_REFS_LIST).dispose();
//	}
//
//	protected void initializeSpecifiedPackageInfoRefs() {
//		for (XmlPackageInfoRef xmlJavaPackageInfoRef : this.getXmlPackageInfoRefs()) {
//			this.specifiedPackageInfoRefs.add(this.buildPackageInfoRef(xmlJavaPackageInfoRef));
//		}
//	}
//
//	protected void syncSpecifiedPackageInfoRefs() {
//		ContextContainerTools.synchronizeWithResourceModel(this.specifiedPackageInfoRefContainerAdapter);
//	}
//
//	protected Iterable<XmlPackageInfoRef> getXmlPackageInfoRefs() {
//		// clone to reduce chance of concurrency problems
//		return new LiveCloneIterable<XmlPackageInfoRef>(this.xmlPersistenceUnit.getClasses());
//	}
//
//	protected void moveSpecifiedPackageInfoRef_(int index, PackageInfoRef classRef) {
//		this.moveItemInList(index, classRef, this.specifiedPackageInfoRefs, SPECIFIED_CLASS_REFS_LIST);
//	}
//
//	protected PackageInfoRef addSpecifiedPackageInfoRef_(int index, XmlPackageInfoRef xmlPackageInfoRef) {
//		PackageInfoRef classRef = this.buildPackageInfoRef(xmlPackageInfoRef);
//		this.addItemToList(index, classRef, this.specifiedPackageInfoRefs, SPECIFIED_CLASS_REFS_LIST);
//		return classRef;
//	}
//
//	protected void removeSpecifiedPackageInfoRef_(PackageInfoRef classRef) {
//		this.removeSpecifiedPackageInfoRef_(this.specifiedPackageInfoRefs.indexOf(classRef));
//	}
//	
//	/**
//	 * specified class ref container adapter
//	 */
//	protected class SpecifiedPackageInfoRefContainerAdapter
//		implements ContextContainerTools.Adapter<PackageInfoRef, XmlPackageInfoRef>
//	{
//		public Iterable<PackageInfoRef> getContextElements() {
//			return HibernatePersistenceUnit.this.getSpecifiedPackageInfoRefs();
//		}
//		public Iterable<XmlPackageInfoRef> getResourceElements() {
//			return HibernatePersistenceUnit.this.getXmlPackageInfoRefs();
//		}
//		public XmlPackageInfoRef getResourceElement(PackageInfoRef contextElement) {
//			return contextElement.getXmlPackageInfoRef();
//		}
//		public void moveContextElement(int index, PackageInfoRef element) {
//			HibernatePersistenceUnit.this.moveSpecifiedPackageInfoRef_(index, element);
//		}
//		public void addContextElement(int index, XmlPackageInfoRef resourceElement) {
//			HibernatePersistenceUnit.this.addSpecifiedPackageInfoRef_(index, resourceElement);
//		}
//		public void removeContextElement(PackageInfoRef element) {
//			HibernatePersistenceUnit.this.removeSpecifiedPackageInfoRef_(element);
//		}
//	}
//	
//	// ********** virtual package-info refs **********
//
//	public Iterator<PackageInfoRef> impliedPackageInfoRefs() {
//		return this.getImpliedPackageInfoRefs().iterator();
//	}
//
//	protected Iterable<PackageInfoRef> getImpliedPackageInfoRefs() {
//		return new LiveCloneIterable<PackageInfoRef>(this.impliedPackageInfoRefs);
//	}
//
//	public int impliedPackageInfoRefsSize() {
//		return this.impliedPackageInfoRefs.size();
//	}
//
//	protected PackageInfoRef addImpliedPackageInfoRef(String packageName) {
//		PackageInfoRef classRef = this.buildPackageInfoRef(packageName);
//		this.addItemToCollection(classRef, this.impliedPackageInfoRefs, IMPLIED_CLASS_REFS_COLLECTION);
//		return classRef;
//	}
//
//	protected PackageInfoRef buildPackageInfoRef(String packageName) {
//		return this.getContextNodeFactory().buildPackageInfoRef(this, packageName);
//	}
//
//	protected void removeImpliedPackageInfoRef(PackageInfoRef classRef) {
//		this.impliedPackageInfoRefs.remove(classRef);
//		classRef.dispose();
//		this.fireItemRemoved(IMPLIED_CLASS_REFS_COLLECTION, classRef);
//	}
//
//	protected void updateImpliedPackageInfoRefs() {
//		ContextContainerTools.update(this.impliedPackageInfoRefContainerAdapter);
//	}
//
//	protected Iterable<String> getImpliedClassNames() {
//		return this.excludesUnlistedClasses() ?
//				EmptyIterable.<String>instance() :
//				this.getImpliedClassNames_();
//	}
//
//	/**
//	 * Return the names of all the Java classes in the JPA project that are
//	 * mapped (i.e. have the appropriate annotation etc.) but not specified
//	 * in the persistence unit.
//	 */
//	protected Iterable<String> getImpliedClassNames_() {
//		return new FilteringIterable<String>(this.getJpaProject().getMappedJavaSourceClassNames()) {
//				@Override
//				protected boolean accept(String mappedClassName) {
//					return ! HibernatePersistenceUnit.this.specifiesPersistentType(mappedClassName);
//				}
//			};
//	}
//
//	/**
//	 * Virtual class ref container adapter.
//	 * <p>
//	 * <strong>NB:</strong> The context class ref is matched with a resource
//	 * class by name.
//	 * <p>
//	 * This is used during <strong>both</strong> <em>sync</em> and
//	 * <em>update</em> because the list of implied class refs can be modified
//	 * in either situation. In particular, we cannot simply rely on
//	 * <em>update</em> because there are situations where a <em>sync</em> is
//	 * triggered but a follow-up <em>update</em> is not. (Of course, any
//	 * change discovered here will trigger an <em>update</em>.)
//	 * <p>
//	 * The most obvious example is when the JPA project is configured to
//	 * discover annotated classes and a Java class is annotated for the first
//	 * time (via code editing, not via the context model). This will trigger
//	 * a <em>sync</em>; but, since the unannotated class is not yet in the
//	 * context model and, as a result, the context model's state is untouched,
//	 * an <em>update</em> will not be triggered.
//	 * <p>
//	 * Obviously, other context model changes can change this collection (e.g.
//	 * setting whether the persistence unit excludes unlisted classes); o the
//	 * collection must also be synchronized during <em>update</em>.
//	 */
//	protected class ImpliedPackageInfoRefContainerAdapter
//		implements ContextContainerTools.Adapter<PackageInfoRef, String>
//	{
//		public Iterable<PackageInfoRef> getContextElements() {
//			return HibernatePersistenceUnit.this.getImpliedPackageInfoRef();
//		}
//		public Iterable<String> getResourceElements() {
//			return HibernatePersistenceUnit.this.getImpliedPackageInfoNames();
//		}
//		public String getResourceElement(PackageInfoRef contextElement) {
//			return contextElement.getPackageName();
//		}
//		public void moveContextElement(int index, PackageInfoRef element) {
//			// ignore moves - we don't care about the order of the implied package-info refs
//		}
//		public void addContextElement(int index, String resourceElement) {
//			// ignore the index - we don't care about the order of the implied package-info refs
//			HibernatePersistenceUnit.this.addImpliedPackageInfoRef(resourceElement);
//		}
//		public void removeContextElement(PackageInfoRef element) {
//			HibernatePersistenceUnit.this.removeImpliedPackageInfoRef(element);
//		}
//	}

}
