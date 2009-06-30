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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaGenerator;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.GenericJavaIdMapping;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.jpt.utility.internal.iterators.SingleElementIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;

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

	public JavaGenericGenerator addGenericGenerator() {
		if (getGenericGenerator() != null) {
			throw new IllegalStateException("genericGenerator already exists"); //$NON-NLS-1$
		}
		this.genericGenerator = ((HibernateJpaFactory)getJpaFactory()).buildJavaGenericGenerator(this);
		GenericGeneratorAnnotation genericGeneratorResource = (GenericGeneratorAnnotation)getResourcePersistentAttribute()
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
		this.getResourcePersistentAttribute().removeSupportingAnnotation(GenericGeneratorAnnotation.ANNOTATION_NAME);
		firePropertyChanged(GENERIC_GENERATOR_PROPERTY, oldGenericGenerator,null);
	}
	
	public void setGenericGenerator(JavaGenericGenerator newGenericGenerator) {
		JavaGenericGenerator oldGenericGenerator = this.genericGenerator;
		this.genericGenerator = newGenericGenerator;
		firePropertyChanged(GENERIC_GENERATOR_PROPERTY, oldGenericGenerator, newGenericGenerator);
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
