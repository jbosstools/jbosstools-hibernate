/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.common.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterable.SubListIterableWrapper;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.context.Generator;
import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaGeneratorContainerImpl extends
	GenericJavaGeneratorContainer implements
	HibernateGenericGeneratorContainer {

	protected final ContextListContainer<JavaDbGenericGenerator, GenericGeneratorAnnotation> genericGeneratorContainer;
	
	public HibernateJavaGeneratorContainerImpl(JavaGeneratorContainer.Parent parentAdapter) {
		super(parentAdapter);
		this.genericGeneratorContainer = this.buildGenericGeneratorContainer();
	}

	@Override
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}

	// ********** synchronize/update **********
	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.syncGenericGenerators();
	}

	@Override
	public void update() {
		super.update();
		this.updateModels(this.getGenericGenerators());
	}
	
	@Override
	protected Iterable<Generator> getGenerators_() {
		return IterableTools.concatenate(
				super.getGenerators_(),
				this.getGenericGenerators()
			);
	}

	// ******************* Generic Generators ****************
	public ListIterable<JavaDbGenericGenerator> getGenericGenerators() {
		return this.genericGeneratorContainer;
	}

	public int getGenericGeneratorsSize() {
		return this.genericGeneratorContainer.size();
	}

	public JavaDbGenericGenerator addGenericGenerator() {
		return this.addGenericGenerator(this.getGenericGeneratorsSize());
	}

	public JavaDbGenericGenerator addGenericGenerator(int index) {
		GenericGeneratorAnnotation annotation = this.addGenericGeneratorAnnotation(index);
		return this.genericGeneratorContainer.addContextElement(index, annotation);
	}
	
	protected GenericGeneratorAnnotation addGenericGeneratorAnnotation(int index) {
		return (GenericGeneratorAnnotation) this.parent.getResourceAnnotatedElement().addAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME);
	}

	public void removeGenericGenerator(GenericGenerator genericGenerator) {
		this.removeGenericGenerator(this.genericGeneratorContainer.indexOf((JavaDbGenericGenerator) genericGenerator));
	}

	public void removeGenericGenerator(int index) {
		this.parent.getResourceAnnotatedElement().removeAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME);
		this.genericGeneratorContainer.remove(index);
	}

	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		this.parent.getResourceAnnotatedElement().moveAnnotation(targetIndex, sourceIndex, GenericGeneratorAnnotation.ANNOTATION_NAME);
		this.genericGeneratorContainer.move(targetIndex, sourceIndex);
	}

	protected JavaDbGenericGenerator buildGenericGenerator(GenericGeneratorAnnotation genericGeneratorAnnotation) {
		return this.getJpaFactory().buildJavaGenericGenerator(this, genericGeneratorAnnotation);
	}

	protected void syncGenericGenerators() {
		this.genericGeneratorContainer.synchronizeWithResourceModel();
	}

	protected ListIterable<GenericGeneratorAnnotation> getGenericGeneratorAnnotations() {
		return new SubListIterableWrapper<NestableAnnotation, GenericGeneratorAnnotation>(this.getNestableGenericGeneratorAnnotations_());
	}

	protected ListIterable<NestableAnnotation> getNestableGenericGeneratorAnnotations_() {
		return this.parent.getResourceAnnotatedElement().getAnnotations(GenericGeneratorAnnotation.ANNOTATION_NAME);
	}

	protected ContextListContainer<JavaDbGenericGenerator, GenericGeneratorAnnotation> buildGenericGeneratorContainer() {
		return this.buildSpecifiedContextListContainer(GENERIC_GENERATORS_LIST, new GenericGeneratorContainer());
	}

	/**
	 * generic generator container
	 */
	protected class GenericGeneratorContainer
		extends AbstractContainerAdapter<JavaDbGenericGenerator, GenericGeneratorAnnotation>
	{
		@Override
		public JavaDbGenericGenerator buildContextElement(GenericGeneratorAnnotation resourceElement) {
			return HibernateJavaGeneratorContainerImpl.this.buildGenericGenerator(resourceElement);
		}
		@Override
		public ListIterable<GenericGeneratorAnnotation> getResourceElements() {
			return HibernateJavaGeneratorContainerImpl.this.getGenericGeneratorAnnotations();
		}
		@Override
		public GenericGeneratorAnnotation extractResourceElement(JavaDbGenericGenerator contextElement) {
			return contextElement.getGeneratorAnnotation();
		}
	}

}
