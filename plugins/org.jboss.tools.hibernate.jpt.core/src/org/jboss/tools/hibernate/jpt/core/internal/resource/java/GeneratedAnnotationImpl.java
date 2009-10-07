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
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.EnumDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Attribute;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class GeneratedAnnotationImpl extends SourceAnnotation<Attribute> implements
GeneratedAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> VALUE_ADAPTER = buildValueAdapter(DECLARATION_ANNOTATION_ADAPTER);
	private final AnnotationElementAdapter<String> valueAdapter;
	private GenerationTime value;
	
	protected GeneratedAnnotationImpl(JavaResourceNode parent, Attribute attribute) {
		super(parent, attribute, DECLARATION_ANNOTATION_ADAPTER);
		this.valueAdapter = this.buildAdapter(VALUE_ADAPTER);
	}	

	public void initialize(CompilationUnit astRoot) {
		this.value = this.buildValue(astRoot);		
	}

	public void update(CompilationUnit astRoot) {
		this.setValue(this.buildValue(astRoot));		
	}
	
	// ***** value	
	public GenerationTime getValue() {
		return value;
	}

	public void setValue(GenerationTime value) {
		if (this.attributeValueHasNotChanged(this.value, value)) {
			return;
		}
		GenerationTime old = this.value;
		this.value = value;
		this.valueAdapter.setValue(GenerationTime.toJavaAnnotationValue(value));
		this.firePropertyChanged(VALUE_PROPERTY, old, value);
	}
	
	private GenerationTime buildValue(CompilationUnit astRoot) {
		return GenerationTime.fromJavaAnnotationValue(this.valueAdapter.getValue(astRoot));
	}

	public TextRange getValueTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(VALUE_ADAPTER, astRoot);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}
	
	AnnotationElementAdapter<String> buildAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.member, daea);
	}
	
	private static DeclarationAnnotationElementAdapter<String> buildValueAdapter(DeclarationAnnotationAdapter adapter) {
		return new EnumDeclarationAnnotationElementAdapter(adapter, Hibernate.GENERATED__VALUE);
	}

	public static class GeneratedAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final GeneratedAnnotationDefinition INSTANCE = new GeneratedAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private GeneratedAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member attribute) {
			return new GeneratedAnnotationImpl(parent, (Attribute) attribute);
		}
		
		public String getAnnotationName() {
			return GeneratedAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember arg0,
				IAnnotation arg1) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent) {
			throw new UnsupportedOperationException();
		}

	}
	
}
