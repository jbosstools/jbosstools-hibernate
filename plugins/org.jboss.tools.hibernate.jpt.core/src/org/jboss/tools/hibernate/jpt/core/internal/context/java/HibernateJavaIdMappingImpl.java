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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaIdMapping;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaIdMappingImpl extends AbstractJavaIdMapping
implements HibernateJavaIdMapping {

	protected final HibernateJavaTypeDefContainer typeDefContainer;

	protected JavaIndex index;

	protected JavaType type;

	/**
	 * @param parent
	 */
	public HibernateJavaIdMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(parent);
	}

	@Override
	public HibernateJavaGeneratorContainer getGeneratorContainer() {
		return (HibernateJavaGeneratorContainer)super.getGeneratorContainer();
	}

	/*@Override
	protected void addSupportingAnnotationNamesTo(Vector<String> names) {
		super.addSupportingAnnotationNamesTo(names);
		names.add(Hibernate.INDEX);
		names.add(Hibernate.TYPE);
	}*/

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.typeDefContainer.synchronizeWithResourceModel();
		this.initializeIndex();
		this.initializeType();
	}

	@Override
	public void update() {
		super.update();
		this.typeDefContainer.update(this.getResourcePersistentAttribute());
		this.updateIndex();
		this.updateType();
	}

	@Override
	public HibernateJavaColumn getColumn() {
		return (HibernateJavaColumn) this.column;
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return this.getColumn().getDBColumnName();
	}

	public HibernateJavaTypeDefContainer getTypeDefContainer() {
		return this.typeDefContainer;
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

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaIdMapping#javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterator<String> javaCompletionProposals(int pos,
			Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		result = this.getTypeDefContainer().javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		return null;
	}

}
