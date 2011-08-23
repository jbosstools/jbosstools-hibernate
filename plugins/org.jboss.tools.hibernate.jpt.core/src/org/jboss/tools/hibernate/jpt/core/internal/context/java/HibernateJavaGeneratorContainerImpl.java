/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
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
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.internal.iterables.SubIterableWrapper;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.ContextContainerTools;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.resource.java.NestableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorsAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateJavaGeneratorContainerImpl extends
	GenericJavaGeneratorContainer implements
	HibernateJavaGeneratorContainer {

	protected final Vector<JavaGenericGenerator> genericGenerators = new Vector<JavaGenericGenerator>();
	protected GenericGeneratorContainerAdapter genericGeneratorsContainerAdapter = new GenericGeneratorContainerAdapter();

	public HibernateJavaGeneratorContainerImpl(JavaJpaContextNode parent, Owner owner) {
		super(parent, owner);
		this.initializeGenericGenerators();
	}

	@Override
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.syncGenericGenerators();
	}

	@Override
	public void update() {
		super.update();
		this.updateNodes(this.getGenericGenerators());
	}

	// ******************* Generic Generators ****************

	@Override
	public ListIterator<JavaGenericGenerator> genericGenerators() {
		return this.getGenericGenerators().iterator();
	}

	protected ListIterable<JavaGenericGenerator> getGenericGenerators() {
		return new LiveCloneListIterable<JavaGenericGenerator>(this.genericGenerators);
	}

	@Override
	public int genericGeneratorsSize() {
		return this.genericGenerators.size();
	}


	public JavaGenericGenerator addGenericGenerator() {
		return this.addGenericGenerator(this.genericGenerators.size());
	}

	@Override
	public JavaGenericGenerator addGenericGenerator(int index) {
		GenericGeneratorAnnotation annotation = this.buildGenericGeneratorAnnotation(index);
		return this.addGenericGenerator_(index, annotation);
	}

	protected GenericGeneratorAnnotation buildGenericGeneratorAnnotation(int index) {
		return (GenericGeneratorAnnotation) this.owner.getResourceAnnotatedElement().addAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
	}

	@Override
	public void removeGenericGenerator(GenericGenerator generator) {
		removeGenericGenerator(this.genericGenerators.indexOf(generator));
	}

	@Override
	public void removeGenericGenerator(int index) {
		this.owner.getResourceAnnotatedElement().removeAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		this.removeGenericGenerator_(index);
	}

	/**
	 * @param index
	 */
	protected void removeGenericGenerator_(int index) {
		this.removeItemFromList(index, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}

	@Override
	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		this.owner.getResourceAnnotatedElement().moveAnnotation(targetIndex, sourceIndex, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		this.moveItemInList(targetIndex, sourceIndex, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}


	protected void initializeGenericGenerators() {
		for (GenericGeneratorAnnotation annotation : this.getGenericGeneratorAnnotations()) {
			this.genericGenerators.add(this.buildGenericGenerator(annotation));
		}
	}

	protected JavaGenericGenerator buildGenericGenerator(GenericGeneratorAnnotation genericGeneratorAnnotation) {
		return this.getJpaFactory().buildJavaGenericGenerator(this, genericGeneratorAnnotation);
	}

	protected void syncGenericGenerators() {
		ContextContainerTools.synchronizeWithResourceModel(this.genericGeneratorsContainerAdapter);
	}


	protected Iterable<GenericGeneratorAnnotation> getGenericGeneratorAnnotations() {
		return new SubIterableWrapper<NestableAnnotation, GenericGeneratorAnnotation>(
				CollectionTools.iterable(this.genericGeneratorAnnotations())
			);
	}

	protected Iterator<NestableAnnotation> genericGeneratorAnnotations() {
		return this.owner.getResourceAnnotatedElement().annotations(GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
	}


	protected void moveGenericGenerator_(int index, JavaGenericGenerator genericGenerator) {
		this.moveItemInList(index, genericGenerator, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}

	protected JavaGenericGenerator addGenericGenerator_(int index, GenericGeneratorAnnotation ggAnnotation) {
		JavaGenericGenerator generator = this.buildGenericGenerator(ggAnnotation);
		this.addItemToList(index, generator, this.genericGenerators, GENERIC_GENERATORS_LIST);
		return generator;
	}

	protected void removeGenericGenerator_(JavaGenericGenerator rgenericGenerator) {
		this.removeGenericGenerator_(this.genericGenerators.indexOf(rgenericGenerator));
	}


	/**
	 * generic generator container adapter
	 */
	protected class GenericGeneratorContainerAdapter
		implements ContextContainerTools.Adapter<JavaGenericGenerator, GenericGeneratorAnnotation>
	{
		@Override
		public Iterable<JavaGenericGenerator> getContextElements() {
			return HibernateJavaGeneratorContainerImpl.this.getGenericGenerators();
		}
		@Override
		public Iterable<GenericGeneratorAnnotation> getResourceElements() {
			return HibernateJavaGeneratorContainerImpl.this.getGenericGeneratorAnnotations();
		}
		@Override
		public GenericGeneratorAnnotation getResourceElement(JavaGenericGenerator contextElement) {
			return contextElement.getGeneratorAnnotation();
		}
		@Override
		public void moveContextElement(int index, JavaGenericGenerator element) {
			HibernateJavaGeneratorContainerImpl.this.moveGenericGenerator_(index, element);
		}
		@Override
		public void addContextElement(int index, GenericGeneratorAnnotation resourceElement) {
			HibernateJavaGeneratorContainerImpl.this.addGenericGenerator_(index, resourceElement);
		}
		@Override
		public void removeContextElement(JavaGenericGenerator element) {
			HibernateJavaGeneratorContainerImpl.this.removeGenericGenerator_(element);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaGeneratorContainer#javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter,
			CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		ListIterator<JavaGenericGenerator> genericGenerators = genericGenerators();
		while (genericGenerators.hasNext()) {
			result = genericGenerators.next()
			.javaCompletionProposals(pos, filter, astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

}
