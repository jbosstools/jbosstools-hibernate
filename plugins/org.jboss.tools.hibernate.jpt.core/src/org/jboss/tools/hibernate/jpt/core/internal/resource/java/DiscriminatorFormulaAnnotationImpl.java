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

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.eclipse.jpt.core.utility.jdt.Type;

/**
 * @author Dmitry Geraskov
 *
 */
public class DiscriminatorFormulaAnnotationImpl extends SourceAnnotation<Type> implements
		DiscriminatorFormulaAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> VALUE_ADAPTER = buildValueAdapter(DECLARATION_ANNOTATION_ADAPTER);
	private final AnnotationElementAdapter<String> valueAdapter;
	private String value;
	
	protected DiscriminatorFormulaAnnotationImpl(JavaResourceNode parent, Type type) {
		super(parent, type, DECLARATION_ANNOTATION_ADAPTER);
		this.valueAdapter = this.buildAdapter(VALUE_ADAPTER);
	}	

	public void initialize(CompilationUnit astRoot) {
		this.value = this.buildValue(astRoot);		
	}

	public void update(CompilationUnit astRoot) {
		this.setValue(this.buildValue(astRoot));		
	}
	
	// ***** value	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (this.attributeValueHasNotChanged(this.value, value)) {
			return;
		}
		String old = this.value;
		this.value = value;
		this.valueAdapter.setValue(value);
		this.firePropertyChanged(VALUE_PROPERTY, old, value);
	}
	
	private String buildValue(CompilationUnit astRoot) {
		return this.valueAdapter.getValue(astRoot);
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
		return ConversionDeclarationAnnotationElementAdapter.forStrings(adapter, VALUE_PROPERTY);
	}


	public static class DiscriminatorFormulaAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final DiscriminatorFormulaAnnotationDefinition INSTANCE = new DiscriminatorFormulaAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private DiscriminatorFormulaAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member type) {
			return new DiscriminatorFormulaAnnotationImpl(parent, (Type) type);
		}
		
		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent, Type type) {
			throw new UnsupportedOperationException();
		}
		
		public String getAnnotationName() {
			return DiscriminatorFormulaAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember arg0,
				IAnnotation arg1) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent) {
			return new NullDiscriminatorFormulaAnnotation((JavaResourcePersistentType) parent);
		}

	}

}
