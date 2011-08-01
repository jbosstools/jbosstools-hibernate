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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
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
	
	protected final List<JavaTypeDef> typeDefs;

	public HibernateJavaTypeDefContainerImpl(JavaJpaContextNode parent) {
		super(parent);
		this.typeDefs = new ArrayList<JavaTypeDef>();
	}
	
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}
	
	public void initialize(JavaResourceAnnotatedElement jrpe) {
		this.javaResourcePersistentElement = jrpe;
		this.initializeTypeDefs();
	}
	
	public void update(JavaResourceAnnotatedElement jrpm) {
		this.javaResourcePersistentElement = jrpm;
		this.updateTypeDefs();
	}

	public JavaTypeDef addTypeDef(int index) {
		JavaTypeDef newTypeDef = getJpaFactory().buildJavaTypeDef(this);
		this.typeDefs.add(index, newTypeDef);
		TypeDefAnnotation typeDefAnnotation = (TypeDefAnnotation)this.javaResourcePersistentElement
			.addAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME, TypeDefsAnnotation.ANNOTATION_NAME);
		newTypeDef.initialize(typeDefAnnotation);
		fireItemAdded(TYPE_DEFS_LIST, index, newTypeDef);
		return newTypeDef;
	}
	
	protected void addTypeDef(JavaTypeDef typeDef) {
		this.addTypeDef(typeDefsSize(), typeDef);
	}
	
	protected void addTypeDef(int index, JavaTypeDef typeDef) {
		addItemToList(index, typeDef, this.typeDefs, TYPE_DEFS_LIST);
	}

	public ListIterator<JavaTypeDef> typeDefs() {
		return new CloneListIterator<JavaTypeDef>(typeDefs);
	}

	public int typeDefsSize() {
		return this.typeDefs.size();
	}

	public void moveTypeDef(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.typeDefs, targetIndex, sourceIndex);
		this.javaResourcePersistentElement.moveAnnotation(targetIndex, sourceIndex, TypeDefsAnnotation.ANNOTATION_NAME);
		fireItemMoved(TYPE_DEFS_LIST, targetIndex, sourceIndex);		
	}

	public void removeTypeDef(int index) {
		JavaTypeDef removedTypeDef = this.typeDefs.remove(index);
		this.javaResourcePersistentElement.removeAnnotation(index, TypeDefAnnotation.ANNOTATION_NAME, TypeDefsAnnotation.ANNOTATION_NAME);
		fireItemRemoved(TYPE_DEFS_LIST, index, removedTypeDef);
	}

	public void removeTypeDef(JavaTypeDef typeDef) {
		removeTypeDef(this.typeDefs.indexOf(typeDef));		
	}

	protected void removeTypeDef_(JavaTypeDef typeDef) {
		removeItemFromList(typeDef, this.typeDefs, TYPE_DEFS_LIST);
	}

	protected void initializeTypeDefs() {
		for (Iterator<NestableAnnotation> stream = this.javaResourcePersistentElement.annotations(
				TypeDefAnnotation.ANNOTATION_NAME,
				TypeDefsAnnotation.ANNOTATION_NAME);
		stream.hasNext(); ) {
			this.typeDefs.add(buildTypeDef((TypeDefAnnotation) stream.next()));
		}
	}
	
	protected JavaTypeDef buildTypeDef(TypeDefAnnotation typeDefResource) {
		JavaTypeDef typeDef = getJpaFactory().buildJavaTypeDef(this);
		typeDef.initialize(typeDefResource);
		return typeDef;
	}
	
	protected void addTypeDefsTo(ArrayList<JavaTypeDef> typeDefs) {
		for (JavaTypeDef typeDef : typeDefs) {
			typeDefs.add(typeDef);
		}
	}
	
	protected void updateTypeDefs() {
		ListIterator<JavaTypeDef> typeDefs = typeDefs();
		Iterator<NestableAnnotation> resourceTypeDefs =
			this.javaResourcePersistentElement.annotations(
					TypeDefAnnotation.ANNOTATION_NAME,
					TypeDefsAnnotation.ANNOTATION_NAME);

		while (typeDefs.hasNext()) {
			JavaTypeDef typeDef = typeDefs.next();
			if (resourceTypeDefs.hasNext()) {
				typeDef.update((TypeDefAnnotation) resourceTypeDefs.next());
			}
			else {
				removeTypeDef_(typeDef);
			}
		}

		while (resourceTypeDefs.hasNext()) {
			addTypeDef(buildTypeDef((TypeDefAnnotation) resourceTypeDefs.next()));
		}
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.validateTypeDefs(messages, reporter, astRoot);
	}


	protected void validateTypeDefs(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		ListIterator<JavaTypeDef> typeDefs = typeDefs();
		while (typeDefs.hasNext()) {
			typeDefs.next().validate(messages, reporter, astRoot);
		}	
	}

	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter,
			CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		ListIterator<JavaTypeDef> typeDefs = typeDefs();
		while (typeDefs.hasNext()) {
			result = typeDefs.next()
				.javaCompletionProposals(pos, filter, astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.javaResourcePersistentElement.getTextRange(astRoot);
	}
}
