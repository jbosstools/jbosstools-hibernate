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
import org.eclipse.jpt.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
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

	public void update(CompilationUnit astRoot) {
		this.setName(this.buildName(astRoot));
		this.setInverseName(this.buildInverseName(astRoot));
	}

	public String getInverseName() {
		return inverseName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		if (this.attributeValueHasNotChanged(this.name, newName)) {
			return;
		}
		String old = this.name;
		this.name = newName;
		this.nameAdapter.setValue(newName);
		this.firePropertyChanged(NAME_PROPERTY, old, newName);
	}
	
	public void setInverseName(String newInverseName) {
		if (this.attributeValueHasNotChanged(this.inverseName, newInverseName)) {
			return;
		}
		String old = this.inverseName;
		this.inverseName = newInverseName;
		this.inverseNameAdapter.setValue(newInverseName);
		this.firePropertyChanged(INVERSE_NAME_PROPERTY, old, newInverseName);
	}
	
	private String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}
	
	private String buildInverseName(CompilationUnit astRoot) {
		return this.inverseNameAdapter.getValue(astRoot);
	}
	
	private static DeclarationAnnotationElementAdapter<String> buildDeclarationAnnotationAdapter(String property) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(DECLARATION_ANNOTATION_ADAPTER, property, true);
	}
	
	AnnotationElementAdapter<String> buildElementAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.member, daea);
	}

	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(NAME_ADAPTER, astRoot);
	}
	
	public TextRange getInverseNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(INVERSE_NAME_ADAPTER, astRoot);
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

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member member) {
			return new ForeignKeyAnnotationImpl(parent, member);
		}
		
		public String getAnnotationName() {
			return ForeignKeyAnnotation.ANNOTATION_NAME;
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
