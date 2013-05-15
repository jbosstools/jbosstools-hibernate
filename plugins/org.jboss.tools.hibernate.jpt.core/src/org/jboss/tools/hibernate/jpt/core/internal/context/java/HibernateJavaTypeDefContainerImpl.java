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

import org.eclipse.jpt.common.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.common.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.iterable.SubListIterableWrapper;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaContextModel;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaTypeDefContainerImpl extends
		AbstractJavaContextModel<JpaContextModel> implements HibernateJavaTypeDefContainer {

	protected JavaResourceAnnotatedElement javaResourceannotatedElement;
	
	protected final ContextListContainer<JavaTypeDef, TypeDefAnnotation> typeDefContainer;

	public HibernateJavaTypeDefContainerImpl(JpaContextModel parent, JavaResourceAnnotatedElement javaResourcePersistentElement) {
		super(parent);
		this.javaResourceannotatedElement = javaResourcePersistentElement;
		this.typeDefContainer = this.buildTypeDefContainer();
	}
	
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}

	protected JavaResourceAnnotatedElement getResourceAnnotatedElement() {
		return this.javaResourceannotatedElement;
	}
	
	// ********** synchronize/update **********

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.syncTypeDefs();
	}

	@Override
	public void update() {
		super.update();
		this.updateModels(this.getTypeDefs());
	}

	// ********** type defs **********
	public ListIterable<JavaTypeDef> getTypeDefs() {
		return this.typeDefContainer;
	}

	public int getTypeDefsSize() {
		return this.typeDefContainer.size();
	}

	public JavaTypeDef addTypeDef() {
		return this.addTypeDef(this.getTypeDefsSize());
	}

	public JavaTypeDef addTypeDef(int index) {
		TypeDefAnnotation annotation = this.addTypeDefAnnotation(index);
		return this.typeDefContainer.addContextElement(index, annotation);
	}

	protected TypeDefAnnotation addTypeDefAnnotation(int index) {
		return (TypeDefAnnotation) this.javaResourceannotatedElement.addAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME);
	}

	public void removeTypeDef(JavaTypeDef namedQuery) {
		this.removeTypeDef(this.typeDefContainer.indexOf((JavaTypeDef) namedQuery));
	}

	public void removeTypeDef(int index) {
		this.javaResourceannotatedElement.removeAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME);
		this.typeDefContainer.remove(index);
	}

	public void moveTypeDef(int targetIndex, int sourceIndex) {
		this.javaResourceannotatedElement.moveAnnotation(targetIndex, sourceIndex, TypeDefAnnotation.ANNOTATION_NAME);
		this.typeDefContainer.move(targetIndex, sourceIndex);
	}

	protected JavaTypeDef buildTypeDef(TypeDefAnnotation namedQueryAnnotation) {
		return this.getJpaFactory().buildJavaTypeDef(this, namedQueryAnnotation);
	}

	protected void syncTypeDefs() {
		this.typeDefContainer.synchronizeWithResourceModel();
	}

	protected ListIterable<TypeDefAnnotation> getTypeDefAnnotations() {
		return new SubListIterableWrapper<NestableAnnotation, TypeDefAnnotation>(this.getNestableTypeDefAnnotations_());
	}

	protected ListIterable<NestableAnnotation> getNestableTypeDefAnnotations_() {
		return this.javaResourceannotatedElement.getAnnotations(TypeDefAnnotation.ANNOTATION_NAME);
	}

	protected ContextListContainer<JavaTypeDef, TypeDefAnnotation> buildTypeDefContainer() {
		return this.buildSpecifiedContextListContainer(TYPE_DEFS_LIST, new TypeDefContainer());
	}

	/**
	 * named query container
	 */
	protected class TypeDefContainer
		extends AbstractContainerAdapter<JavaTypeDef, TypeDefAnnotation>
	{
		@Override
		public JavaTypeDef buildContextElement(TypeDefAnnotation resourceElement) {
			return HibernateJavaTypeDefContainerImpl.this.buildTypeDef(resourceElement);
		}
		@Override
		public ListIterable<TypeDefAnnotation> getResourceElements() {
			return HibernateJavaTypeDefContainerImpl.this.getTypeDefAnnotations();
		}
		@Override
		public TypeDefAnnotation extractResourceElement(JavaTypeDef contextElement) {
			return contextElement.getTypeDefAnnotation();
		}
	}

	// ********** validation **********

	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		for (JavaTypeDef typeDef : getTypeDefs()) {
			typeDef.validate(messages, reporter);
		}
	}
	
	public TextRange getValidationTextRange() {
		return this.javaResourceannotatedElement.getTextRange();
	}

}
