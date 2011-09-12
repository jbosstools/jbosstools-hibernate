/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
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
public class SourceGenericGeneratorsAnnotation extends SourceAnnotation<AnnotatedElement> implements
		GenericGeneratorsAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<GenericGeneratorAnnotation> genericGenerators = new Vector<GenericGeneratorAnnotation>();


	public SourceGenericGeneratorsAnnotation(JavaResourceNode parent, AnnotatedElement element) {
		super(parent, element, DECLARATION_ANNOTATION_ADAPTER);
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
				this.genericGenerators.isEmpty();
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.genericGenerators);
	}

	// ********** AnnotationContainer implementation **********
	public String getElementName() {
		return Hibernate.GENERIC_GENERATORS__VALUE;
	}

	public String getNestedAnnotationName() {
		return GenericGeneratorAnnotation.ANNOTATION_NAME;
	}

	public Iterable<GenericGeneratorAnnotation> getNestedAnnotations() {
		return new LiveCloneIterable<GenericGeneratorAnnotation>(this.genericGenerators);
	}

	public int getNestedAnnotationsSize() {
		return this.genericGenerators.size();
	}

	public void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation) {
		this.nestStandAloneAnnotation(standAloneAnnotation, this.genericGenerators.size());
	}

	private void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation, int index) {
		standAloneAnnotation.convertToNested(this, this.daa, index);
	}

	public void addNestedAnnotation(int index, NestableAnnotation annotation) {
		this.genericGenerators.add(index, (GenericGeneratorAnnotation) annotation);
	}

	public void convertLastNestedAnnotationToStandAlone() {
		this.genericGenerators.remove(0).convertToStandAlone();
	}

	public GenericGeneratorAnnotation addNestedAnnotation() {
		return this.addNestedAnnotation(this.genericGenerators.size());
	}

	private GenericGeneratorAnnotation addNestedAnnotation(int index) {
		GenericGeneratorAnnotation genericGenerator = this.buildGenericGenerator(index);
		this.genericGenerators.add(genericGenerator);
		return genericGenerator;
	}

	public void syncAddNestedAnnotation(org.eclipse.jdt.core.dom.Annotation astAnnotation) {
		int index = this.genericGenerators.size();
		GenericGeneratorAnnotation genericGenerator = this.addNestedAnnotation(index);
		genericGenerator.initialize((CompilationUnit) astAnnotation.getRoot());
		this.fireItemAdded(GENERIC_GENERATORS_LIST, index, genericGenerator);
	}

	private GenericGeneratorAnnotation buildGenericGenerator(int index) {
		return GenericGeneratorAnnotationImpl.createNestedGenericGenerator(this.parent, this.annotatedElement, index, this.daa);
	}

	public GenericGeneratorAnnotation moveNestedAnnotation(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.genericGenerators, targetIndex, sourceIndex).get(targetIndex);
	}

	public GenericGeneratorAnnotation removeNestedAnnotation(int index) {
		return this.genericGenerators.remove(index);
	}

	public void syncRemoveNestedAnnotations(int index) {
		this.removeItemsFromList(index, this.genericGenerators, GENERIC_GENERATORS_LIST);
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

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement annotatedElement) {
			return new SourceGenericGeneratorsAnnotation(parent, annotatedElement);
		}

		public Annotation buildNullAnnotation(JavaResourceAnnotatedElement parent) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, IAnnotation jdtAnnotation) {
			throw new UnsupportedOperationException();
		}

		public String getAnnotationName() {
			return GenericGeneratorsAnnotation.ANNOTATION_NAME;
		}

	}

}
