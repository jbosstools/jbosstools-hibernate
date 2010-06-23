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

import java.util.Vector;

import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaBasicMapping;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GeneratedAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaBasicMappingImpl extends AbstractJavaBasicMapping
implements HibernateJavaBasicMapping {
	
	protected GenerationTime specifiedGenerationTime;
	
	protected JavaIndex index;

	public HibernateJavaBasicMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	@Override
	public void addSupportingAnnotationNamesTo(Vector<String> names) {
		names.add(Hibernate.GENERATED);
		names.add(Hibernate.INDEX);
	}
	
	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.specifiedGenerationTime = this.getResourceGenerationTime();
		this.initializeIndex();
	}
	
	@Override
	protected void update() {
		super.update();
		this.setGenerationTime_(this.getResourceGenerationTime());
		this.updateIndex();
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
	
}
