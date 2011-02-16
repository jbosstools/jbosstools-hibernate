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
import org.eclipse.jpt.core.internal.context.java.AbstractJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaIdMappingImpl extends AbstractJavaIdMapping 
implements HibernateJavaIdMapping {
	
	protected JavaIndex index;
	
	/**
	 * @param parent
	 */
	public HibernateJavaIdMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	@Override
	public HibernateJavaGeneratorContainer getGeneratorContainer() {
		return (HibernateJavaGeneratorContainer)super.getGeneratorContainer();
	}
	
	@Override
	protected void addSupportingAnnotationNamesTo(Vector<String> names) {
		super.addSupportingAnnotationNamesTo(names);
		names.add(Hibernate.INDEX);
	}
	
	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.initializeIndex();
	}
	
	@Override
	public void update() {
		super.update();
		this.updateIndex();
	}
	
	@Override
	public HibernateJavaColumn getColumn() {
		return (HibernateJavaColumn) column;
	}
	
	@Override
	public String getPrimaryKeyColumnName() {
		return this.getColumn().getDBColumnName();
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
