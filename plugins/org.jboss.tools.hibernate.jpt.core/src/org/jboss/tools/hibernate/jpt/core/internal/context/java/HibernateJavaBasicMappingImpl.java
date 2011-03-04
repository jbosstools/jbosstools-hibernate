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
import java.util.Vector;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaBasicMapping;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GeneratedAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaBasicMappingImpl extends AbstractJavaBasicMapping
implements HibernateJavaBasicMapping {
	
	protected final HibernateJavaTypeDefContainer typeDefContainer;

	protected GenerationTime specifiedGenerationTime;
	
	protected JavaIndex index;

	protected JavaType type;

	public HibernateJavaBasicMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(parent);
	}
	
	@Override
	public void addSupportingAnnotationNamesTo(Vector<String> names) {
		names.add(Hibernate.GENERATED);
		names.add(Hibernate.INDEX);
		names.add(Hibernate.TYPE);
	}
	
	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.typeDefContainer.initialize(this.getResourcePersistentAttribute());
		this.specifiedGenerationTime = this.getResourceGenerationTime();
		this.initializeIndex();
		this.initializeType();
	}
	
	@Override
	protected void update() {
		super.update();
		this.typeDefContainer.update(this.getResourcePersistentAttribute());
		this.setGenerationTime_(this.getResourceGenerationTime());
		this.updateIndex();
		this.updateType();
	}
	
	public HibernateJavaTypeDefContainer getTypeDefContainer() {
		return this.typeDefContainer;
	}

	public GeneratedAnnotation getResourceGenerated() {
		return (GeneratedAnnotation) getResourcePersistentAttribute().getAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	public GeneratedAnnotation addResourceGenerated() {
		return (GeneratedAnnotation) getResourcePersistentAttribute().addAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	public void removeResourceGenerated() {
		getResourcePersistentAttribute().removeAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	protected GenerationTime getResourceGenerationTime(){
		GeneratedAnnotation geneatedAnnotation = getResourceGenerated();
		return geneatedAnnotation == null ? null : geneatedAnnotation.getValue();
	}
	
	public GenerationTime getGenerationTime() {
		return this.specifiedGenerationTime;
	}
	
	public void setGenerationTime(GenerationTime newValue) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newValue;
		if (newValue != null){
			GeneratedAnnotation annotation = getResourceGenerated() != null
						? getResourceGenerated()
						: addResourceGenerated();
			annotation.setValue(newValue);
		} else {
			removeResourceGenerated();
		}
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newValue);
	}

	public void setGenerationTime_(GenerationTime newGenerationTime) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newGenerationTime;
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newGenerationTime);
	}
	
	public void removeResourceIndex() {
		getResourcePersistentAttribute().removeAnnotation(IndexAnnotation.ANNOTATION_NAME);
	}
	
	// *** index
	
	protected void initializeIndex() {
		IndexAnnotation indexResource = getResourceIndex();
		if (indexResource != null) {
			this.index = buildIndex(indexResource);
		}
	}
	
	protected void updateIndex() {
		IndexAnnotation indexResource = getResourceIndex();
		if (indexResource == null) {
			if (getIndex() != null) {
				setIndex(null);
			}
		}
		else {
			if (getIndex() == null) {
				setIndex(buildIndex(indexResource));
			}
			else {
				getIndex().update(indexResource);
			}
		}
	}
	
	public JavaIndex addIndex() {
		if (getIndex() != null) {
			throw new IllegalStateException("index already exists"); //$NON-NLS-1$
		}
		this.index = getJpaFactory().buildIndex(this);
		IndexAnnotation indexResource = (IndexAnnotation) getResourcePersistentAttribute().addAnnotation(IndexAnnotation.ANNOTATION_NAME);
		this.index.initialize(indexResource);
		firePropertyChanged(INDEX_PROPERTY, null, this.index);
		return this.index;
	}

	public JavaIndex getIndex() {
		return this.index;
	}
	
	protected void setIndex(JavaIndex newIndex) {
		JavaIndex oldIndex = this.index;
		this.index = newIndex;
		firePropertyChanged(INDEX_PROPERTY, oldIndex, newIndex);
	}

	public void removeIndex() {
		if (getIndex() == null) {
			throw new IllegalStateException("index does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaIndex oldIndex = this.index;
		this.index = null;
		this.getResourcePersistentAttribute().removeAnnotation(IndexAnnotation.ANNOTATION_NAME);
		firePropertyChanged(INDEX_PROPERTY, oldIndex, null);
	}
	
	protected JavaIndex buildIndex(IndexAnnotation indexResource) {
		JavaIndex index = getJpaFactory().buildIndex(this);
		index.initialize(indexResource);
		return index;
	}
	
	protected IndexAnnotation getResourceIndex() {
		return (IndexAnnotation) this.getResourcePersistentAttribute().getAnnotation(IndexAnnotation.ANNOTATION_NAME);
	}
	
	// *** type
	
	protected void initializeType() {
		TypeAnnotation typeResource = getTypeResource();
		if (typeResource != null) {
			this.type = buildType(typeResource);
		}
	}
	
	protected void updateType() {
		TypeAnnotation typeResource = getTypeResource();
		if (typeResource == null) {
			if (getType() != null) {
				setType(null);
			}
		}
		else {
			if (getType() == null) {
				setType(buildType(typeResource));
			}
			else {
				getType().update(typeResource);
			}
		}
	}
	
	public JavaType addType() {
		if (getType() != null) {
			throw new IllegalStateException("type already exists"); //$NON-NLS-1$
		}
		this.type = getJpaFactory().buildType(this);
		TypeAnnotation typeResource = (TypeAnnotation) getResourcePersistentAttribute().addAnnotation(TypeAnnotation.ANNOTATION_NAME);
		this.type.initialize(typeResource);
		firePropertyChanged(TYPE_PROPERTY, null, this.type);
		return this.type;
	}

	public JavaType getType() {
		return this.type;
	}
	
	protected void setType(JavaType newType) {
		JavaType oldType = this.type;
		this.type = newType;
		firePropertyChanged(TYPE_PROPERTY, oldType, newType);
	}

	public void removeType() {
		if (getType() == null) {
			throw new IllegalStateException("type does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaType oldType = this.type;
		this.type = null;
		this.getResourcePersistentAttribute().removeAnnotation(TypeAnnotation.ANNOTATION_NAME);
		firePropertyChanged(TYPE_PROPERTY, oldType, null);
	}
	
	protected JavaType buildType(TypeAnnotation typeResource) {
		JavaType type = getJpaFactory().buildType(this);
		type.initialize(typeResource);
		return type;
	}
	
	protected TypeAnnotation getTypeResource() {
		return (TypeAnnotation) this.getResourcePersistentAttribute().getAnnotation(TypeAnnotation.ANNOTATION_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jpt.core.internal.context.java.AbstractJavaBasicMapping#
	 * javaCompletionProposals(int, org.eclipse.jpt.utility.Filter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterator<String> javaCompletionProposals(int pos,
			Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter,
				astRoot);
		if (result != null) {
			return result;
		}
		result = this.getTypeDefContainer().javaCompletionProposals(pos,
				filter, astRoot);
		if (result != null) {
			return result;
		}
		if (this.getType() != null) {
			result = this.getType().javaCompletionProposals(pos, filter,
					astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jpt.core.internal.context.java.AbstractJavaBasicMapping#validate
	 * (java.util.List,
	 * org.eclipse.wst.validation.internal.provisional.core.IReporter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.typeDefContainer.validate(messages, reporter, astRoot);
		if (this.index != null){
			this.index.validate(messages, reporter, astRoot);
		}
		if (this.type != null){
			this.type.validate(messages, reporter, astRoot);
		}
	}
	
}
