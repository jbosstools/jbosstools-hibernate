/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class SourceGenericGeneratorsAnnotation extends SourceAnnotation<Member> implements
		GenericGeneratorsAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<GenericGeneratorAnnotation> genericGenerators = new Vector<GenericGeneratorAnnotation>();


	public SourceGenericGeneratorsAnnotation(JavaResourceNode parent, Member member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		AnnotationContainerTools.initialize(this, astRoot);
	}

	public void update(CompilationUnit astRoot) {
		AnnotationContainerTools.update(this, astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.genericGenerators);
	}
	
	// ********** AnnotationContainer implementation **********

	public String getContainerAnnotationName() {
		return this.getAnnotationName();
	}

	public org.eclipse.jdt.core.dom.Annotation getContainerJdtAnnotation(CompilationUnit astRoot) {
		return this.getJdtAnnotation(astRoot);
	}

	public String getElementName() {
		return Hibernate.GENERIC_GENERATORS__VALUE;
	}

	public String getNestableAnnotationName() {
		return GenericGeneratorAnnotation.ANNOTATION_NAME;
	}

	public ListIterator<GenericGeneratorAnnotation> nestedAnnotations() {
		return new CloneListIterator<GenericGeneratorAnnotation>(this.genericGenerators);
	}

	public int nestedAnnotationsSize() {
		return this.genericGenerators.size();
	}

	public GenericGeneratorAnnotation addNestedAnnotationInternal() {
		GenericGeneratorAnnotation genericGenerator = this.buildGenericGenerator(this.genericGenerators.size());
		this.genericGenerators.add(genericGenerator);
		return genericGenerator;
	}

	private GenericGeneratorAnnotation buildGenericGenerator(int index) {
		return GenericGeneratorAnnotationImpl.createNestedGenericGenerator(this, member, index, this.daa);
	}
	
	public void nestedAnnotationAdded(int index, GenericGeneratorAnnotation nestedAnnotation) {
		this.fireItemAdded(GENERIC_GENERATORS_LIST, index, nestedAnnotation);
	}

	public GenericGeneratorAnnotation moveNestedAnnotationInternal(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.genericGenerators, targetIndex, sourceIndex).get(targetIndex);
	}

	public void nestedAnnotationMoved(int targetIndex, int sourceIndex) {
		this.fireItemMoved(GENERIC_GENERATORS_LIST, targetIndex, sourceIndex);
	}

	public GenericGeneratorAnnotation removeNestedAnnotationInternal(int index) {
		return this.genericGenerators.remove(index);
	}

	public void nestedAnnotationRemoved(int index, GenericGeneratorAnnotation nestedAnnotation) {
		this.fireItemRemoved(GENERIC_GENERATORS_LIST, index, nestedAnnotation);
	}
	
	public static class GenericGeneratorsAnnotationDefinition implements AnnotationDefinition {

		// singleton
		private static final AnnotationDefinition INSTANCE = new GenericGeneratorsAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure single instance.
		 */
		private GenericGeneratorsAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member member) {
			return new SourceGenericGeneratorsAnnotation(parent, member);
		}

		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, IAnnotation jdtAnnotation) {
			throw new UnsupportedOperationException();
		}

		public String getAnnotationName() {
			return GenericGeneratorsAnnotation.ANNOTATION_NAME;
		}

	}

}
