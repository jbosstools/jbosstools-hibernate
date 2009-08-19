/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaGenerator;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.GenericJavaIdMapping;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.jpt.utility.internal.iterators.EmptyListIterator;
import org.eclipse.jpt.utility.internal.iterators.SingleElementIterator;
import org.eclipse.jpt.utility.internal.iterators.SingleElementListIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGeneratorHolder;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaIdMapping extends GenericJavaIdMapping 
implements GenericGeneratorHolder {
	
	protected JavaGenericGenerator genericGenerator;
	
	/**
	 * @param parent
	 */
	public HibernateJavaIdMapping(JavaPersistentAttribute parent) {
		super(parent);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.initializeGenericGenerator();
	}
	
	protected void initializeGenericGenerator() {
		GenericGeneratorAnnotation genericGeneratorResource = getResourceGenericGenerator();
		if (genericGeneratorResource != null) {
			this.genericGenerator = buildGenericGenerator(genericGeneratorResource);
		}
	}
	
	protected GenericGeneratorAnnotation getResourceGenericGenerator() {
		return (GenericGeneratorAnnotation) this.getResourcePersistentAttribute().getSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
	}
	
	protected JavaGenericGenerator buildGenericGenerator(GenericGeneratorAnnotation genericGeneratorResource) {
		JavaGenericGenerator generator = ((HibernateJpaFactory) getJpaFactory()).buildJavaGenericGenerator(this);
		generator.initialize(genericGeneratorResource);
		return generator;
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<JavaGenerator> generators() {
		return new CompositeIterator<JavaGenerator>(super.generators(),
			(getGenericGenerator() == null) ? EmptyIterator.instance() 
											: new SingleElementIterator(getGenericGenerator()));
	}

	public JavaGenericGenerator addGenericGenerator(int index) {
		if (getGenericGenerator() != null) {
			throw new IllegalStateException("genericGenerator already exists"); //$NON-NLS-1$
		}
		this.genericGenerator = ((HibernateJpaFactory)getJpaFactory()).buildJavaGenericGenerator(this);
		GenericGeneratorAnnotation genericGeneratorResource = (GenericGeneratorAnnotation)getResourcePersistentAttribute()
								.addSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
		this.genericGenerator.initialize(genericGeneratorResource);
		firePropertyChanged(GENERIC_GENERATORS_LIST, null, this.genericGenerator);
		return this.genericGenerator;
	}

	private JavaGenericGenerator getGenericGenerator() {
		return genericGenerator;
	}

	private void removeGenericGenerator() {
		if (getGenericGenerator() == null) {
			throw new IllegalStateException("genericGenerator does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaGenericGenerator oldGenericGenerator = this.genericGenerator;
		this.genericGenerator = null;
		this.getResourcePersistentAttribute().removeSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
		firePropertyChanged(GENERIC_GENERATORS_LIST, oldGenericGenerator,null);
	}
	
	private void setGenericGenerator(JavaGenericGenerator newGenericGenerator) {
		JavaGenericGenerator oldGenericGenerator = this.genericGenerator;
		this.genericGenerator = newGenericGenerator;
		firePropertyChanged(GENERIC_GENERATORS_LIST, oldGenericGenerator, newGenericGenerator);
	}
	
	@Override
	public void update() {
		super.update();
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
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateGenericGenerator(messages, reporter, astRoot);
	}
	
	private void validateGenericGenerator(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
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

	public ListIterator<GenericGenerator> genericGenerators() {
		return genericGenerator == null ? EmptyListIterator.<GenericGenerator>instance()				
					: new SingleElementListIterator<GenericGenerator>(genericGenerator);
	}

	public int genericGeneratorsSize() {
		return genericGenerator == null ? 0 : 1;
	}

	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		throw new UnsupportedOperationException();
	}

	public void removeGenericGenerator(int index) {
		if (genericGeneratorsSize() < index + 1){
			throw new IndexOutOfBoundsException();
		}
		removeGenericGenerator();
	}

	public void removeGenericGenerator(GenericGenerator generator) {
		if (this.genericGenerator == generator){
			removeGenericGenerator();
		}
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}

	@Override
	public String getDefaultColumnName() {
		NamingStrategy namingStrategy = getJpaProject().getNamingStrategy();
		if (namingStrategy != null && getPersistentAttribute().getName() != null){
				return namingStrategy.propertyToColumnName(getPersistentAttribute().getName());
		}
		return super.getDefaultColumnName();
	}
}
