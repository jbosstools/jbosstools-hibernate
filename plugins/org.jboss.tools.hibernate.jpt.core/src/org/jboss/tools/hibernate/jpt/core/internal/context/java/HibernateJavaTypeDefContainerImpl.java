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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.internal.iterators.SubIteratorWrapper;
import org.eclipse.jpt.common.utility.internal.iterators.SuperIteratorWrapper;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.ContextContainerTools;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.jpa.core.resource.java.NestableAnnotation;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefsAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaTypeDefContainerImpl extends
		AbstractJavaJpaContextNode implements HibernateJavaTypeDefContainer {

	protected JavaResourceAnnotatedElement javaResourcePersistentElement;
	
	protected final Vector<JavaTypeDef> typeDefs = new Vector<JavaTypeDef>();
	protected TypeDefContainerAdapter typeDefContainerAdapter = new TypeDefContainerAdapter();

	
	public HibernateJavaTypeDefContainerImpl(JavaJpaContextNode parent, JavaResourceAnnotatedElement javaResourcePersistentElement) {
		super(parent);
		this.javaResourcePersistentElement = javaResourcePersistentElement;
		this.initializeTypeDefs();
	}
	
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}

	protected JavaResourceAnnotatedElement getResourceAnnotatedElement() {
		return this.javaResourcePersistentElement;
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
		this.updateNodes(this.getTypeDefs());
	}

	// ********** type defs **********

	public ListIterator<JavaTypeDef> typeDefs() {
		return this.getTypeDefs().iterator();
	}

	protected ListIterable<JavaTypeDef> getTypeDefs() {
		return new LiveCloneListIterable<JavaTypeDef>(this.typeDefs);
	}

	public int typeDefsSize() {
		return this.typeDefs.size();
	}

	public JavaTypeDef addTypeDef() {
		return this.addTypeDef(this.typeDefs.size());
	}

	public JavaTypeDef addTypeDef(int index) {
		TypeDefAnnotation annotation = this.buildTypeDefAnnotation(index);
		return this.addTypeDef_(index, annotation);
	}

	protected TypeDefAnnotation buildTypeDefAnnotation(int index) {
		return (TypeDefAnnotation) this.getResourceAnnotatedElement().addAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME, TypeDefsAnnotation.ANNOTATION_NAME);
	}

	public void removeTypeDef(JavaTypeDef typeDef) {
		this.removeTypeDef(this.typeDefs.indexOf(typeDef));
	}

	public void removeTypeDef(int index) {
		this.getResourceAnnotatedElement().removeAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME, TypeDefsAnnotation.ANNOTATION_NAME);
		this.removeTypeDef_(index);
	}

	protected void removeTypeDef_(int index) {
		this.removeItemFromList(index, this.typeDefs, TYPE_DEFS_LIST);
	}

	public void moveTypeDef(int targetIndex, int sourceIndex) {
		this.getResourceAnnotatedElement().moveAnnotation(targetIndex, sourceIndex, TypeDefsAnnotation.ANNOTATION_NAME);
		this.moveItemInList(targetIndex, sourceIndex, this.typeDefs, TYPE_DEFS_LIST);
	}

	protected void initializeTypeDefs() {
		for (TypeDefAnnotation annotation : this.getTypeDefAnnotations()) {
			this.typeDefs.add(this.buildTypeDef(annotation));
		}
	}

	protected JavaTypeDef buildTypeDef(TypeDefAnnotation typeDefAnnotation) {
		return this.getJpaFactory().buildJavaTypeDef(this, typeDefAnnotation);
	}

	protected void syncTypeDefs() {
		ContextContainerTools.synchronizeWithResourceModel(this.typeDefContainerAdapter);
	}

	protected Iterable<TypeDefAnnotation> getTypeDefAnnotations() {
		return CollectionTools.iterable(this.typeDefAnnotations());
	}

	protected Iterator<TypeDefAnnotation> typeDefAnnotations() {
		return new SuperIteratorWrapper<TypeDefAnnotation>(this.nestableTypeDefAnnotations());
	}

	protected Iterator<TypeDefAnnotation> nestableTypeDefAnnotations() {
		return new SubIteratorWrapper<NestableAnnotation, TypeDefAnnotation>(this.nestableTypeDefAnnotations_());
	}

	protected Iterator<NestableAnnotation> nestableTypeDefAnnotations_() {
		return this.getResourceAnnotatedElement().annotations(TypeDefAnnotation.ANNOTATION_NAME, TypeDefsAnnotation.ANNOTATION_NAME);
	}

	protected void moveTypeDef_(int index, JavaTypeDef typeDef) {
		this.moveItemInList(index, typeDef, this.typeDefs, TYPE_DEFS_LIST);
	}

	protected JavaTypeDef addTypeDef_(int index, TypeDefAnnotation typeDefAnnotation) {
		JavaTypeDef typeDef = this.buildTypeDef(typeDefAnnotation);
		this.addItemToList(index, typeDef, this.typeDefs, TYPE_DEFS_LIST);
		return typeDef;
	}

	protected void removeTypeDef_(JavaTypeDef typeDef) {
		this.removeTypeDef_(this.typeDefs.indexOf(typeDef));
	}
	
	// ********** validation **********

	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		for (JavaTypeDef typeDef : typeDefs) {
			typeDef.validate(messages, reporter, astRoot);
		}
	}
	
	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.javaResourcePersistentElement.getTextRange(astRoot);
	}
	
	protected class TypeDefContainerAdapter
		implements ContextContainerTools.Adapter<JavaTypeDef, TypeDefAnnotation>
	{
		public Iterable<JavaTypeDef> getContextElements() {
			return HibernateJavaTypeDefContainerImpl.this.getTypeDefs();
		}
		public Iterable<TypeDefAnnotation> getResourceElements() {
			return HibernateJavaTypeDefContainerImpl.this.getTypeDefAnnotations();
		}
		public TypeDefAnnotation getResourceElement(JavaTypeDef contextElement) {
			return contextElement.getTypeDefAnnotation();
		}
		public void moveContextElement(int index, JavaTypeDef element) {
			HibernateJavaTypeDefContainerImpl.this.moveTypeDef_(index, element);
		}
		public void addContextElement(int index, TypeDefAnnotation resourceElement) {
			HibernateJavaTypeDefContainerImpl.this.addTypeDef_(index, resourceElement);
		}
		public void removeContextElement(JavaTypeDef element) {
			HibernateJavaTypeDefContainerImpl.this.removeTypeDef_(element);
		}
}
}
