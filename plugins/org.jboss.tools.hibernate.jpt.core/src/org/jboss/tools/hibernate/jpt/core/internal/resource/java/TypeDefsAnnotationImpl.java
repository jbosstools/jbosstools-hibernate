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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.Vector;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneIterable;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.Annotation;
import org.eclipse.jpt.jpa.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.jpa.core.resource.java.NestableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeDefsAnnotationImpl extends SourceAnnotation<AnnotatedElement> implements
		TypeDefsAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<TypeDefAnnotation> typeDefs = new Vector<TypeDefAnnotation>();


	public TypeDefsAnnotationImpl(JavaResourceNode parent, AnnotatedElement member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		AnnotationContainerTools.initialize(this, astRoot);
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		AnnotationContainerTools.synchronize(this, astRoot);
	}

	@Override
	public boolean isUnset() {
		return super.isUnset() &&
				this.typeDefs.isEmpty();
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.typeDefs);
	}

	// ********** AnnotationContainer implementation **********
	public String getElementName() {
		return Hibernate.TYPE_DEFS__VALUE;
	}

	public String getNestedAnnotationName() {
		return TypeDefAnnotation.ANNOTATION_NAME;
	}

	public Iterable<TypeDefAnnotation> getNestedAnnotations() {
		return new LiveCloneIterable<TypeDefAnnotation>(this.typeDefs);
	}

	public int getNestedAnnotationsSize() {
		return this.typeDefs.size();
	}

	public void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation) {
		this.nestStandAloneAnnotation(standAloneAnnotation, this.typeDefs.size());
	}

	private void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation, int index) {
		standAloneAnnotation.convertToNested(this, this.daa, index);
	}

	public void addNestedAnnotation(int index, NestableAnnotation annotation) {
		this.typeDefs.add(index, (TypeDefAnnotation) annotation);
	}

	public void convertLastNestedAnnotationToStandAlone() {
		this.typeDefs.remove(0).convertToStandAlone();
	}

	public TypeDefAnnotation addNestedAnnotation() {
		return this.addNestedAnnotation(this.typeDefs.size());
	}

	private TypeDefAnnotation addNestedAnnotation(int index) {
		TypeDefAnnotation typeDef = this.buildTypeDef(index);
		this.typeDefs.add(typeDef);
		return typeDef;
	}

	public void syncAddNestedAnnotation(org.eclipse.jdt.core.dom.Annotation astAnnotation) {
		int index = this.typeDefs.size();
		TypeDefAnnotation typeDef = this.addNestedAnnotation(index);
		typeDef.initialize((CompilationUnit) astAnnotation.getRoot());
		this.fireItemAdded(TYPE_DEFS_LIST, index, typeDef);
	}

	private TypeDefAnnotation buildTypeDef(int index) {
		return TypeDefAnnotationImpl.createNestedTypeDef(this, this.annotatedElement, index, this.daa);
	}

	public TypeDefAnnotation moveNestedAnnotation(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.typeDefs, targetIndex, sourceIndex).get(targetIndex);
	}

	public TypeDefAnnotation removeNestedAnnotation(int index) {
		return this.typeDefs.remove(index);
	}

	public void syncRemoveNestedAnnotations(int index) {
		this.removeItemsFromList(index, this.typeDefs, TYPE_DEFS_LIST);
	}

	public static class TypeDefsAnnotationDefinition implements AnnotationDefinition {

		// singleton
		private static final AnnotationDefinition INSTANCE = new TypeDefsAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure single instance.
		 */
		private TypeDefsAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement member) {
			return new TypeDefsAnnotationImpl(parent, member);
		}

		public Annotation buildNullAnnotation(JavaResourceAnnotatedElement parent) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, IAnnotation jdtAnnotation) {
			throw new UnsupportedOperationException();
		}

		public String getAnnotationName() {
			return TypeDefsAnnotation.ANNOTATION_NAME;
		}

	}

}
