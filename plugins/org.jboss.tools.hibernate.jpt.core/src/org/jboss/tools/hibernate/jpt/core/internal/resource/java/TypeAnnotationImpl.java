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

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.Member;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.Annotation;
import org.eclipse.jpt.jpa.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceNode;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeAnnotationImpl extends SourceAnnotation<Member> implements
TypeAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> TYPE_ADAPTER = buildTypeAdapter(DECLARATION_ANNOTATION_ADAPTER);
	private final AnnotationElementAdapter<String> typeAdapter;
	private String type;

	protected TypeAnnotationImpl(JavaResourceNode parent, Member attribute) {
		super(parent, attribute, DECLARATION_ANNOTATION_ADAPTER);
		this.typeAdapter = this.buildTypeAdapter(TYPE_ADAPTER);
	}

	public void initialize(CompilationUnit astRoot) {
		this.type = this.buildType(astRoot);
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		this.syncType(this.buildType(astRoot));
	}

	// ***** type
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		if (this.attributeValueHasChanged(this.type, type)) {
			this.type = type;
			this.typeAdapter.setValue(type);
		}
	}

	private void syncType(String type) {
		String old = this.type;
		this.type = type;
		this.firePropertyChanged(TYPE_PROPERTY, old, type);
	}

	private String buildType(CompilationUnit astRoot) {
		return this.typeAdapter.getValue(astRoot);
	}

	public TextRange getTypeTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(TYPE_ADAPTER, astRoot);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}


	AnnotationElementAdapter<String> buildTypeAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, daea);
	}

	private static DeclarationAnnotationElementAdapter<String> buildTypeAdapter(DeclarationAnnotationAdapter adapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(adapter, TYPE_PROPERTY);
	}

	public static class TypeAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final TypeAnnotationDefinition INSTANCE = new TypeAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private TypeAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement annotatedElement) {
			return new TypeAnnotationImpl(parent, (Member) annotatedElement);
		}

		public String getAnnotationName() {
			return TypeAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement arg0,
				IAnnotation arg1) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(JavaResourceAnnotatedElement parent) {
			throw new UnsupportedOperationException();
		}

	}


}
