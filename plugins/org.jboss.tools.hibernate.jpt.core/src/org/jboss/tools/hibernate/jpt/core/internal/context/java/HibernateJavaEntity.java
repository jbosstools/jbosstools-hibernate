/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaGenerator;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaQuery;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaEntity;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.ArrayIterator;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaEntity extends AbstractJavaEntity 
implements GenericGeneratorHolder, HibernateQueryContainer {

	protected JavaGenericGenerator genericGenerator;
	
	protected final List<HibernateNamedQuery> hibernateNamedQueries;
	
	protected final List<HibernateNamedNativeQuery> hibernateNamedNativeQueries;
	
	public HibernateJavaEntity(JavaPersistentType parent) {
		super(parent);
		this.hibernateNamedQueries = new ArrayList<HibernateNamedQuery>();
		this.hibernateNamedNativeQueries = new ArrayList<HibernateNamedNativeQuery>();
	}
	
	@Override
	public void initialize(JavaResourcePersistentType resourcePersistentType) {
		super.initialize(resourcePersistentType);
		this.initializeGenericGenerator();
		this.initializeHibernateNamedQueries();
		this.initializeHibernateNamedNativeQueries();
	}
	
	@Override
	public void update(JavaResourcePersistentType resourcePersistentType) {
		super.update(resourcePersistentType);
		this.updateGenericGenerator();
		this.updateHibernateNamedQueries();
		this.updateHibernateNamedNativeQueries();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<JavaQuery> queries() {
		return new CompositeIterator<JavaQuery>(
				super.queries(),
				this.hibernateNamedQueries(),
				this.hibernateNamedNativeQueries());
	}
	
	// ********************* GenericGenerator **************
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<String> correspondingAnnotationNames() {
		return new CompositeIterator<String>(
				new ArrayIterator<String>(Hibernate.GENERIC_GENERATOR, 
				Hibernate.NAMED_QUERY,
				Hibernate.NAMED_QUERIES,
				Hibernate.NAMED_NATIVE_QUERY,
				Hibernate.NAMED_NATIVE_QUERIES),
				super.correspondingAnnotationNames());
	}
	
	public void setGenericGenerator(JavaGenericGenerator newGenericGenerator) {
		JavaGenericGenerator oldGenericGenerator = this.genericGenerator;
		this.genericGenerator = newGenericGenerator;
		firePropertyChanged(GENERIC_GENERATOR_PROPERTY, oldGenericGenerator, newGenericGenerator);
	}
	
	protected HibernateJpaFactory getJpaFactory() {
		return (HibernateJpaFactory) this.getJpaPlatform().getJpaFactory();
	}
	
	protected void initializeGenericGenerator() {
		GenericGeneratorAnnotation genericGeneratorResource = getResourceGenericGenerator();
		if (genericGeneratorResource != null) {
			this.genericGenerator = buildGenericGenerator(genericGeneratorResource);
		}
	}

	protected GenericGeneratorAnnotation getResourceGenericGenerator() {
		return (GenericGeneratorAnnotation) this.javaResourcePersistentType.getSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
	}
	
	protected JavaGenericGenerator buildGenericGenerator(GenericGeneratorAnnotation genericGeneratorResource) {
		JavaGenericGenerator generator = getJpaFactory().buildJavaGenericGenerator(this);
		generator.initialize(genericGeneratorResource);
		return generator;
	}

	public JavaGenericGenerator addGenericGenerator() {
		if (getGenericGenerator() != null) {
			throw new IllegalStateException("genericGenerator already exists"); //$NON-NLS-1$
		}
		this.genericGenerator = getJpaFactory().buildJavaGenericGenerator(this);
		GenericGeneratorAnnotation genericGeneratorResource = (GenericGeneratorAnnotation)javaResourcePersistentType
								.addSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
		this.genericGenerator.initialize(genericGeneratorResource);
		firePropertyChanged(GENERIC_GENERATOR_PROPERTY, null, this.genericGenerator);
		return this.genericGenerator;
	}

	public JavaGenericGenerator getGenericGenerator() {
		return genericGenerator;
	}

	public void removeGenericGenerator() {
		if (getGenericGenerator() == null) {
			throw new IllegalStateException("genericGenerator does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaGenericGenerator oldGenericGenerator = this.genericGenerator;
		this.genericGenerator = null;
		this.javaResourcePersistentType.removeSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
		firePropertyChanged(GENERIC_GENERATOR_PROPERTY, oldGenericGenerator,null);
	}
	
	@Override
	protected void addGeneratorsTo(ArrayList<JavaGenerator> generators) {
		super.addGeneratorsTo(generators);
		if (this.genericGenerator != null) {
			generators.add(this.genericGenerator);
		}
	}
	
	protected void updateGenericGenerator() {
		GenericGeneratorAnnotation genericGeneratorResource = getResourceGenericGenerator();
		if (genericGeneratorResource == null) {
			if (getGenericGenerator() != null) {
				setGenericGenerator(null);
			}
		}
		else {
			if (getGenericGenerator() == null) {
				setGenericGenerator(buildGenericGenerator(genericGeneratorResource));
			}
			else {
				getGenericGenerator().update(genericGeneratorResource);
			}
		}
	}
	// ********************* NamedQuery **************
	public ListIterator<HibernateNamedQuery> hibernateNamedQueries() {
		return new CloneListIterator<HibernateNamedQuery>(this.hibernateNamedQueries);
	}
	
	public int hibernateNamedQueriesSize() {
		return this.hibernateNamedQueries.size();
	}
	
	protected void initializeHibernateNamedQueries() {
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME); stream.hasNext(); ) {
			this.hibernateNamedQueries.add(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) stream.next()));
		}
	}
	
	protected HibernateNamedQuery buildHibernateNamedQuery(HibernateNamedQueryAnnotation namedQueryResource) {
		HibernateNamedQuery hibernateNamedQuery = getJpaFactory().buildHibernateNamedQuery(this);
		hibernateNamedQuery.initialize(namedQueryResource);
		return hibernateNamedQuery;
	}
	
	protected void updateHibernateNamedQueries() {
		ListIterator<HibernateNamedQuery> queries = hibernateNamedQueries();
		ListIterator<NestableAnnotation> resourceNamedQueries = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedQuery hibernateNamedQuery = queries.next();
			if (resourceNamedQueries.hasNext()) {
				hibernateNamedQuery.update((HibernateNamedQueryAnnotation) resourceNamedQueries.next());
			}
			else {
				removeHibernateNamedQuery_(hibernateNamedQuery);
			}
		}
		
		while (resourceNamedQueries.hasNext()) {
			addHibernateNamedQuery(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) resourceNamedQueries.next()));
		}
	}
	
	public HibernateNamedQuery addHibernateNamedQuery(int index) {
		HibernateNamedQuery hibernateNamedQuery = getJpaFactory().buildHibernateNamedQuery(this);
		this.hibernateNamedQueries.add(index, hibernateNamedQuery);
		HibernateNamedQueryAnnotation hibernateNamedQueryAnnotation = (HibernateNamedQueryAnnotation) this.javaResourcePersistentType
			.addSupportingAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		hibernateNamedQuery.initialize(hibernateNamedQueryAnnotation);
		fireItemAdded(HIBERNATE_NAMED_QUERIES_LIST, index, hibernateNamedQuery);
		return hibernateNamedQuery;
	}
	
	protected void addHibernateNamedQuery(int index, HibernateNamedQuery hibernateNamedQuery) {
		addItemToList(index, hibernateNamedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}
	
	protected void addHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		this.addHibernateNamedQuery(this.hibernateNamedQueries.size(), hibernateNamedQuery);
	}
	
	public void removeHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		removeHibernateNamedQuery(this.hibernateNamedQueries.indexOf(hibernateNamedQuery));
	}
	
	public void removeHibernateNamedQuery(int index) {
		HibernateNamedQuery removedHibernateNamedQuery = this.hibernateNamedQueries.remove(index);
		this.javaResourcePersistentType.removeSupportingAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_QUERIES_LIST, index, removedHibernateNamedQuery);
	}	
	
	protected void removeHibernateNamedQuery_(HibernateNamedQuery hibernateNamedQuery) {
		removeItemFromList(hibernateNamedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}
	
	public void moveHibernateNamedQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentType.moveSupportingAnnotation(targetIndex, sourceIndex, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_QUERIES_LIST, targetIndex, sourceIndex);		
	}
	
	// ********************* NamedNativeQuery **************
	public ListIterator<HibernateNamedNativeQuery> hibernateNamedNativeQueries() {
		return new CloneListIterator<HibernateNamedNativeQuery>(this.hibernateNamedNativeQueries);
	}
	
	public int hibernateNamedNativeQueriesSize() {
		return this.hibernateNamedNativeQueries.size();
	}
	
	protected void initializeHibernateNamedNativeQueries() {
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME); stream.hasNext(); ) {
			this.hibernateNamedNativeQueries.add(buildHibernateNamedNativeQuery((HibernateNamedNativeQueryAnnotation) stream.next()));
		}
	}
	
	protected HibernateNamedNativeQuery buildHibernateNamedNativeQuery(HibernateNamedNativeQueryAnnotation namedQueryResource) {
		HibernateNamedNativeQuery hibernateNamedNativeQuery = getJpaFactory().buildHibernateNamedNativeQuery(this);
		hibernateNamedNativeQuery.initialize(namedQueryResource);
		return hibernateNamedNativeQuery;
	}
	
	protected void updateHibernateNamedNativeQueries() {
		ListIterator<HibernateNamedNativeQuery> queries = hibernateNamedNativeQueries();
		ListIterator<NestableAnnotation> resourceNamedNativeQueries = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedNativeQuery hibernateNamedNativeQuery = queries.next();
			if (resourceNamedNativeQueries.hasNext()) {
				hibernateNamedNativeQuery.update((HibernateNamedNativeQueryAnnotation) resourceNamedNativeQueries.next());
			}
			else {
				removeHibernateNamedNativeQuery_(hibernateNamedNativeQuery);
			}
		}
		
		while (resourceNamedNativeQueries.hasNext()) {
			Object test = resourceNamedNativeQueries.next();
			if (!(test instanceof HibernateNamedNativeQueryAnnotation)){
				System.out.println("error!");//$NON-NLS-1$
			}
			addHibernateNamedNativeQuery(buildHibernateNamedNativeQuery((HibernateNamedNativeQueryAnnotation) test));
		}
	}
	
	public HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQuery hibernateNamedNativeQuery = getJpaFactory().buildHibernateNamedNativeQuery(this);
		this.hibernateNamedNativeQueries.add(index, hibernateNamedNativeQuery);
		HibernateNamedNativeQueryAnnotation hibernateNamedNativeQueryAnnotation = (HibernateNamedNativeQueryAnnotation) this.javaResourcePersistentType
			.addSupportingAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		hibernateNamedNativeQuery.initialize(hibernateNamedNativeQueryAnnotation);
		fireItemAdded(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, hibernateNamedNativeQuery);
		return hibernateNamedNativeQuery;
	}
	
	protected void addHibernateNamedNativeQuery(int index, HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		addItemToList(index, hibernateNamedNativeQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}
	
	protected void addHibernateNamedNativeQuery(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		this.addHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.size(), hibernateNamedNativeQuery);
	}
	
	public void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		removeHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.indexOf(hibernateNamedNativeQuery));
	}
	
	public void removeHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQuery removedHibernateNamedNativeQuery = this.hibernateNamedNativeQueries.remove(index);
		this.javaResourcePersistentType.removeSupportingAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, removedHibernateNamedNativeQuery);
	}	
	
	protected void removeHibernateNamedNativeQuery_(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		removeItemFromList(hibernateNamedNativeQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}
	
	public void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedNativeQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentType.moveSupportingAnnotation(targetIndex, sourceIndex, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, targetIndex, sourceIndex);		
	}

	// ************************* validation ***********************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateGenericGenerator(messages, reporter, astRoot);
	}
	
	protected void validateGenericGenerator(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		if (genericGenerator != null){
			genericGenerator.validate(messages, reporter, astRoot);
		}
	}
	
	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter,
			CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		if (this.getGenericGenerator() != null) {
			result = this.getGenericGenerator().javaCompletionProposals(pos, filter, astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
