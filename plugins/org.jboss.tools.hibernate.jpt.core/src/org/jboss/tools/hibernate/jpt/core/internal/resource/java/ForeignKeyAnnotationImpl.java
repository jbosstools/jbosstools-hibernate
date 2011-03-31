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
import org.jboss.tools.hibernate.jpt.core.internal.context.java.ForeignKeyAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class ForeignKeyAnnotationImpl extends SourceAnnotation<Member> implements
ForeignKeyAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> NAME_ADAPTER = buildDeclarationAnnotationAdapter(NAME_PROPERTY);
	private final AnnotationElementAdapter<String> nameAdapter;
	private String name;

	private static final DeclarationAnnotationElementAdapter<String> INVERSE_NAME_ADAPTER = buildDeclarationAnnotationAdapter(INVERSE_NAME_PROPERTY);
	private final AnnotationElementAdapter<String> inverseNameAdapter;
	private String inverseName;

	protected ForeignKeyAnnotationImpl(JavaResourceNode parent, Member member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
		this.nameAdapter = this.buildElementAdapter(NAME_ADAPTER);
		this.inverseNameAdapter = this.buildElementAdapter(INVERSE_NAME_ADAPTER);
	}


	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		this.name = this.buildName(astRoot);
		this.inverseName = this.buildInverseName(astRoot);
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		this.syncName(this.buildName(astRoot));
		this.syncInverseName(this.buildInverseName(astRoot));
	}

	// ***** name
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (this.attributeValueHasChanged(this.name, name)) {
			this.name = name;
			this.nameAdapter.setValue(name);
		}
	}

	private void syncName(String astName) {
		String old = this.name;
		this.name = astName;
		this.firePropertyChanged(NAME_PROPERTY, old, astName);
	}

	private String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}

	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(NAME_ADAPTER, astRoot);
	}

	// ***** inverse name
	public String getInverseName() {
		return this.inverseName;
	}

	public void setInverseName(String inverseName) {
		if (this.attributeValueHasChanged(this.inverseName, inverseName)) {
			this.inverseName = inverseName;
			this.inverseNameAdapter.setValue(inverseName);
		}
	}

	private void syncInverseName(String astInverseName) {
		String old = this.inverseName;
		this.inverseName = astInverseName;
		this.firePropertyChanged(INVERSE_NAME_PROPERTY, old, astInverseName);
	}

	private String buildInverseName(CompilationUnit astRoot) {
		return this.inverseNameAdapter.getValue(astRoot);
	}

	public TextRange getInverseNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(INVERSE_NAME_ADAPTER, astRoot);
	}

	private static DeclarationAnnotationElementAdapter<String> buildDeclarationAnnotationAdapter(String property) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(DECLARATION_ANNOTATION_ADAPTER, property);
	}

	AnnotationElementAdapter<String> buildElementAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, daea);
	}


	public static class ForeignKeyAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final ForeignKeyAnnotationDefinition INSTANCE = new ForeignKeyAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private ForeignKeyAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement member) {
			return new ForeignKeyAnnotationImpl(parent, (Member) member);
		}

		public String getAnnotationName() {
			return ForeignKeyAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourceAnnotatedElement parent,
				IAnnotation jdtAnnotation) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(
				JavaResourceAnnotatedElement parent) {
			throw new UnsupportedOperationException();
		}

	}

}
