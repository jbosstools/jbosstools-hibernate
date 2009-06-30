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
import org.eclipse.jpt.core.context.java.JavaNamedQuery;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.internal.context.java.GenericJavaEntity;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.java.NamedQueriesAnnotation;
import org.eclipse.jpt.core.resource.java.NamedQueryAnnotation;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaEntity extends GenericJavaEntity implements GenericGeneratorHolder {

	protected JavaGenericGenerator genericGenerator;
	
	public HibernateJavaEntity(JavaPersistentType parent) {
		super(parent);
	}
	
	@Override
	public void initialize(JavaResourcePersistentType resourcePersistentType) {
		super.initialize(resourcePersistentType);
		this.initializeGenericGenerator();
	}
	
	@Override
	public Iterator<String> correspondingAnnotationNames() {
		return new CompositeIterator<String>(Hibernate.GENERIC_GENERATOR, super.correspondingAnnotationNames());
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
	
	@Override
	protected void initializeNamedQueries() {
		super.initializeNamedQueries();
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(Hibernate.NAMED_QUERY, Hibernate.NAMED_QUERIES); stream.hasNext(); ) {
			this.namedQueries.add(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) stream.next()));
		}
	}
	
	protected JavaNamedQuery buildHibernateNamedQuery(HibernateNamedQueryAnnotation namedQueryResource) {
		JavaNamedQuery namedQuery = getJpaFactory().buildHibernateJavaNamedQuery(this);
		namedQuery.initialize(namedQueryResource);
		return namedQuery;
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
	
	@Override
	public void update(JavaResourcePersistentType resourcePersistentType) {
		super.update(resourcePersistentType);
		updateGenericGenerator();
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

	@Override
	protected void updateNamedQueries() {
		ListIterator<JavaNamedQuery> queries = namedQueries();
		ListIterator<NestableAnnotation> resourceNamedQueries = this.javaResourcePersistentType.supportingAnnotations(NamedQueryAnnotation.ANNOTATION_NAME, NamedQueriesAnnotation.ANNOTATION_NAME);
		
		ListIterator<NestableAnnotation> hibernateNamedQueries = this.javaResourcePersistentType.supportingAnnotations(Hibernate.NAMED_QUERY, Hibernate.NAMED_QUERIES);
		
		while (queries.hasNext()) {
			JavaNamedQuery namedQuery = queries.next();
			if (namedQuery instanceof HibernateJavaNamedQuery) {
				if (hibernateNamedQueries.hasNext()) {
					namedQuery.update((HibernateNamedQueryAnnotation) hibernateNamedQueries.next());
				} else {
					removeNamedQuery_(namedQuery);
				}
			} else {
				if (resourceNamedQueries.hasNext()) {
					namedQuery.update((NamedQueryAnnotation) resourceNamedQueries.next());
				} else {
					removeNamedQuery_(namedQuery);
				}
			}			
		}
		
		while (resourceNamedQueries.hasNext()) {
			addNamedQuery(buildNamedQuery((NamedQueryAnnotation) resourceNamedQueries.next()));
		}
		
		while (hibernateNamedQueries.hasNext()) {
			addNamedQuery(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) hibernateNamedQueries.next()));
		}
	}

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
