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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.common.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ElementIndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.NestedIndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.common.core.resource.java.JavaResourceModel;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.IndexedDeclarationAnnotationAdapter;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class SourceParameterAnnotation extends SourceAnnotation implements
	ParameterAnnotation {

	private final DeclarationAnnotationElementAdapter<String> nameDeclarationAdapter;
	private final AnnotationElementAdapter<String> nameAdapter;
	private String name;

	private final DeclarationAnnotationElementAdapter<String> valueDeclarationAdapter;
	private final AnnotationElementAdapter<String> valueAdapter;
	private String value;
	private CompilationUnit astRoot;


	public SourceParameterAnnotation(JavaResourceModel parent, AnnotatedElement member, IndexedDeclarationAnnotationAdapter idaa) {
		super(parent, member, idaa, new ElementIndexedAnnotationAdapter(member, idaa));
		this.nameDeclarationAdapter = this.buildNameAdapter(idaa);
		this.nameAdapter = this.buildAdapter(this.nameDeclarationAdapter);
		this.valueDeclarationAdapter = this.buildValueAdapter(idaa);
		this.valueAdapter = this.buildAdapter(this.valueDeclarationAdapter);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	private AnnotationElementAdapter<String> buildAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, daea);
	}

	private DeclarationAnnotationElementAdapter<String> buildNameAdapter(DeclarationAnnotationAdapter adapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(adapter, NAME_PROPERTY);
	}

	private DeclarationAnnotationElementAdapter<String> buildValueAdapter(DeclarationAnnotationAdapter adapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(adapter, VALUE_PROPERTY);
	}

	public void initialize(CompilationUnit astRoot) {
		this.astRoot = astRoot;
		this.name = this.buildName(astRoot);
		this.value = this.buildValue(astRoot);
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		this.astRoot = astRoot;
		this.syncName(this.buildName(astRoot));
		this.syncValue(this.buildValue(astRoot));
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.name);
	}


	// ********** ParameterAnnotation implementation **********

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

	public TextRange getNameTextRange() {
		return this.getElementTextRange(this.nameDeclarationAdapter, getAstAnnotation(astRoot));
	}

	// ***** value
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		if (this.attributeValueHasChanged(this.value, value)) {
			this.value = value;
			this.valueAdapter.setValue(value);
		}
	}

	private void syncValue(String value) {
		String old = this.value;
		this.value = value;
		this.firePropertyChanged(VALUE_PROPERTY, old, value);
	}

	private String buildValue(CompilationUnit astRoot) {
		return this.valueAdapter.getValue(astRoot);
	}

	public TextRange getValueTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.valueDeclarationAdapter, getAstAnnotation(astRoot));
	}

	// ********** static methods **********

	public static SourceParameterAnnotation createParameter(JavaResourceModel parent, AnnotatedElement member,  DeclarationAnnotationAdapter annotationAdapter, String elementName, int index) {
		return new SourceParameterAnnotation(parent, member, buildParameterAnnotationAdapter(annotationAdapter, elementName, index));
	}

	private static IndexedDeclarationAnnotationAdapter buildParameterAnnotationAdapter(DeclarationAnnotationAdapter annotationAdapter, String elementName, int index) {
		return new NestedIndexedDeclarationAnnotationAdapter(annotationAdapter, elementName, index, Hibernate.PARAMETER);
	}

}
