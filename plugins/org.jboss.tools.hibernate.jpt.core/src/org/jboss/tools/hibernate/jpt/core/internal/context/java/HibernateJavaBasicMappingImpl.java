/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jpt.common.utility.internal.iterable.ArrayIterable;
import org.eclipse.jpt.common.utility.internal.iterable.CompositeIterable;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter.Adapter;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaBasicMapping;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GeneratedAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaBasicMappingImpl extends AbstractJavaBasicMapping
implements HibernateJavaBasicMapping {

	protected static final Iterable<JavaConverter.Adapter> HIBERNATE_CONVERTER_ADAPTERS = 
			new CompositeIterable<JavaConverter.Adapter>(new ArrayIterable<JavaConverter.Adapter>(CONVERTER_ADAPTER_ARRAY));

	protected GenerationTime specifiedGenerationTime;

	protected JavaIndex index;

	protected JavaType type;

	public HibernateJavaBasicMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
		this.index = this.buildIndex();
		this.type = this.buildType();
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}

	@Override
	protected Iterable<Adapter> getConverterAdapters() {
		return HIBERNATE_CONVERTER_ADAPTERS;
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.specifiedGenerationTime = this.getResourceGenerationTime();
		this.syncIndex();
		this.syncType();
	}

	@Override
	public void update() {
		super.update();
		this.setGenerationTime_(this.getResourceGenerationTime());
		if (this.index != null){
			this.index.update();
		}
		if (this.type != null){
			this.type.update();
		}
	}

	public GeneratedAnnotation getGeneratedAnnotation() {
		return (GeneratedAnnotation) getResourceAttribute().getAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}

	public GeneratedAnnotation buildGeneratedAnnotation() {
		return (GeneratedAnnotation) getResourceAttribute().addAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}

	public void removeGeneratedAnnotation() {
		getResourceAttribute().removeAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}

	protected GenerationTime getResourceGenerationTime(){
		GeneratedAnnotation geneatedAnnotation = getGeneratedAnnotation();
		return geneatedAnnotation == null ? null : geneatedAnnotation.getValue();
	}

	public GenerationTime getGenerationTime() {
		return this.specifiedGenerationTime;
	}

	public void setGenerationTime(GenerationTime newValue) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newValue;
		if (newValue != null){
			GeneratedAnnotation annotation = getGeneratedAnnotation() != null
			? getGeneratedAnnotation()
					: buildGeneratedAnnotation();
			annotation.setValue(newValue);
		} else {
			removeGeneratedAnnotation();
		}
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newValue);
	}

	public void setGenerationTime_(GenerationTime newGenerationTime) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newGenerationTime;
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newGenerationTime);
	}

	// *** index
	public JavaIndex getIndex() {
		return this.index;
	}
	
	public JavaIndex addIndex() {
		if (getIndex() != null) {
			throw new IllegalStateException("index already exists"); //$NON-NLS-1$
		}
		IndexAnnotation annotation = this.buildIndexAnnotation();
		JavaIndex index = this.buildIndex(annotation);
		this.setIndex(index);
		return index;
	}
	
	protected IndexAnnotation buildIndexAnnotation() {
		return (IndexAnnotation) this.getResourceAttribute().addAnnotation(IndexAnnotation.ANNOTATION_NAME);
	}
	
	public void removeIndex() {
		if (getIndex() == null) {
			throw new IllegalStateException("index does not exist, cannot be removed"); //$NON-NLS-1$
		}
		this.getResourceAttribute().removeAnnotation(IndexAnnotation.ANNOTATION_NAME);
		setIndex(null);
	}

	protected JavaIndex buildIndex() {
		IndexAnnotation annotation = getIndexAnnotation();
		return (annotation == null) ? null : buildIndex(annotation);
	}
	
	protected IndexAnnotation getIndexAnnotation() {
		return (IndexAnnotation) this.getResourceAttribute().getAnnotation(IndexAnnotation.ANNOTATION_NAME);
	}
	
	protected JavaIndex buildIndex(IndexAnnotation annotation) {
		return this.getJpaFactory().buildIndex(this, annotation);
	}

	protected void syncIndex() {
		IndexAnnotation annotation = getIndexAnnotation();
		if (annotation == null) {
			if (getIndex() != null) {
				setIndex(null);
			}
		}
		else {
			if ((getIndex() != null) && (getIndex().getIndexAnnotation() == annotation)) {
				this.index.synchronizeWithResourceModel();
			} else {
				this.setIndex(this.buildIndex(annotation));
			}
		}
	}

	protected void setIndex(JavaIndex newIndex) {
		JavaIndex oldIndex = this.index;
		this.index = newIndex;
		firePropertyChanged(INDEX_PROPERTY, oldIndex, newIndex);
	}


	// ********** type **********

	public JavaType getType() {
		return this.type;
	}

	public JavaType addType() {
		if (this.type != null) {
			throw new IllegalStateException("type already exists: " + this.type); //$NON-NLS-1$
		}
		TypeAnnotation annotation = this.buildTypeAnnotation();
		JavaType value = this.buildType(annotation);
		this.setType(value);
		return value;
	}

	protected TypeAnnotation buildTypeAnnotation() {
		return (TypeAnnotation) this.getResourceAttribute().addAnnotation(TypeAnnotation.ANNOTATION_NAME);
	}

	public void removeType() {
		if (this.type == null) {
			throw new IllegalStateException("generated value does not exist"); //$NON-NLS-1$
		}
		this.getResourceAttribute().removeAnnotation(TypeAnnotation.ANNOTATION_NAME);
		this.setType(null);
	}

	protected JavaType buildType() {
		TypeAnnotation annotation = this.getTypeAnnotation();
		return (annotation == null) ? null : this.buildType(annotation);
	}

	protected TypeAnnotation getTypeAnnotation() {
		return (TypeAnnotation) this.getResourceAttribute().getAnnotation(TypeAnnotation.ANNOTATION_NAME);
	}

	protected JavaType buildType(TypeAnnotation generatedValueAnnotation) {
		return this.getJpaFactory().buildType(this, generatedValueAnnotation);
	}

	protected void syncType() {
		TypeAnnotation annotation = this.getTypeAnnotation();
		if (annotation == null) {
			if (this.type != null) {
				this.setType(null);
			}
		}
		else {
			if ((this.type != null) && (this.type.getTypeAnnotation() == annotation)) {
				this.type.synchronizeWithResourceModel();
			} else {
				this.setType(this.buildType(annotation));
			}
		}
	}

	protected void setType(JavaType value) {
		JavaType old = this.type;
		this.type = value;
		this.firePropertyChanged(TYPE_PROPERTY, old, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaBasicMapping#
	 * javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
//	@Override
//	public Iterable<String> getJavaCompletionProposals(int pos,
//			Filter<String> filter) {
//		Iterable<String> result = super.getJavaCompletionProposals(pos, filter);
//		if (result != null) {
//			return result;
//		}
//		if (this.getType() != null) {
//			result = this.getType().getJavaCompletionProposals(pos, filter);
//			if (result != null) {
//				return result;
//			}
//		}
//		return null;
//	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaBasicMapping#validate
	 * (java.util.List,
	 * org.eclipse.wst.validation.internal.provisional.core.IReporter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		if (this.index != null){
			this.index.validate(messages, reporter);
		}
		if (this.type != null){
			this.type.validate(messages, reporter);
		}
	}

}
