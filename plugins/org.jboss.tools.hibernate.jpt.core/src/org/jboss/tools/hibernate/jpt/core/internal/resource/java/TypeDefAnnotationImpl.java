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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.common.core.internal.utility.jdt.ASTTools;
import org.eclipse.jpt.common.core.internal.utility.jdt.CombinationIndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ElementIndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleTypeStringExpressionConverter;
import org.eclipse.jpt.common.core.internal.utility.jdt.TypeStringExpressionConverter;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.common.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.common.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.IndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.IndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeDefAnnotationImpl extends SourceAnnotation
					implements TypeDefAnnotation {

	private static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);
	private static final DeclarationAnnotationAdapter CONTAINER_DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(Hibernate.TYPE_DEFS);

	
	private DeclarationAnnotationElementAdapter<String> nameDeclarationAdapter;
	private AnnotationElementAdapter<String> nameAdapter;
	private String name;

	private DeclarationAnnotationElementAdapter<String> typeClassDeclarationAdapter;
	private AnnotationElementAdapter<String> typeClassAdapter;
	private String typeClass;

	String fullyQualifiedTypeClassName;

	private DeclarationAnnotationElementAdapter<String> defForTypeDeclarationAdapter;
	private AnnotationElementAdapter<String> defaultForTypeAdapter;
	private String defaultForType;

	String fullyQualifiedDefaultForTypeClassName;

	final Vector<ParameterAnnotation> parameters = new Vector<ParameterAnnotation>();
	final ParametersAnnotationContainer parametersContainer = new ParametersAnnotationContainer();

	/**
	 * @param parent
	 * @param member
	 */
	public TypeDefAnnotationImpl(JavaResourceNode parent, AnnotatedElement member,
			DeclarationAnnotationAdapter daa, AnnotationAdapter annotationAdapter) {
		super(parent, member, daa, annotationAdapter);
		this.nameDeclarationAdapter = this.buildNameDeclarationAdapter();
		this.nameAdapter = this.buildNameAdapter();
		this.typeClassDeclarationAdapter = this.buildTypeClassDeclarationAdapter();
		this.typeClassAdapter = this.buildTypeClassAdapter();
		this.defForTypeDeclarationAdapter = this.buildDefForTypeDeclarationAdapter();
		this.defaultForTypeAdapter = this.buildDefForTypeAdapter();
	}

	public void initialize(CompilationUnit astRoot) {
		this.name = this.buildName(astRoot);
		this.typeClass = this.buildTypeClass(astRoot);
		this.fullyQualifiedTypeClassName = this.buildFullyQualifiedTypeClassName(astRoot);
		this.defaultForType = this.buildDefaultForType(astRoot);
		this.fullyQualifiedDefaultForTypeClassName = this.buildFullyQualifiedDefaultForTypeClassName(astRoot);
		this.parametersContainer.initializeFromContainerAnnotation(this.getAstAnnotation(astRoot));
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		this.syncName(this.buildName(astRoot));
		this.syncTypeClass(this.buildTypeClass(astRoot));
		this.syncFullyQualifiedTypeClassName(this.buildFullyQualifiedTypeClassName(astRoot));
		this.syncDefaultForType(this.buildDefaultForType(astRoot));
		this.syncFullyQualifiedDefaultForTypeClassName(this.buildFullyQualifiedDefaultForTypeClassName(astRoot));
		this.parametersContainer.synchronize(this.getAstAnnotation(astRoot));
	}
	
	// ********** TypeDefAnnotation implementation **********

	// ***** name

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

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

	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.nameDeclarationAdapter, astRoot);
	}

	protected String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}

	// ***** type class
	public String getTypeClass() {
		return this.typeClass;
	}

	public void setTypeClass(String typeClass) {
		if (this.attributeValueHasChanged(this.typeClass, typeClass)) {
			this.typeClass = typeClass;
			this.typeClassAdapter.setValue(typeClass);
		}
	}

	private void syncTypeClass(String astTypeClass) {
		String old = this.typeClass;
		this.typeClass = astTypeClass;
		this.firePropertyChanged(TYPE_CLASS_PROPERTY, old, astTypeClass);
	}

	private String buildTypeClass(CompilationUnit astRoot) {
		return this.typeClassAdapter.getValue(astRoot);
	}

	public TextRange getTypeClassTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(typeClassDeclarationAdapter, astRoot);
	}

	// ***** fully-qualified type entity class name
	public String getFullyQualifiedTypeClassName() {
		return this.fullyQualifiedTypeClassName;
	}

	private void syncFullyQualifiedTypeClassName(String name) {
		String old = this.fullyQualifiedTypeClassName;
		this.fullyQualifiedTypeClassName = name;
		this.firePropertyChanged(FULLY_QUALIFIED_TYPE_CLASS_NAME_PROPERTY, old, name);
	}

	private String buildFullyQualifiedTypeClassName(CompilationUnit astRoot) {
		return (this.typeClass == null) ? null : ASTTools.resolveFullyQualifiedName(this.typeClassAdapter.getExpression(astRoot));
	}

	// ***** default for type class
	public String getDefaultForType() {
		return this.defaultForType;
	}

	public void setDefaultForType(String defaultForType) {
		if (this.attributeValueHasChanged(this.defaultForType, defaultForType)) {
			this.defaultForType = defaultForType;
			this.defaultForTypeAdapter.setValue(defaultForType);
		}
	}

	private void syncDefaultForType(String astDefaultForType) {
		String old = this.defaultForType;
		this.defaultForType = astDefaultForType;
		this.firePropertyChanged(DEF_FOR_TYPE_PROPERTY, old, astDefaultForType);
	}

	private String buildDefaultForType(CompilationUnit astRoot) {
		return this.defaultForTypeAdapter.getValue(astRoot);
	}

	public TextRange getDefaultForTypeTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(defForTypeDeclarationAdapter, astRoot);
	}

	// ***** fully-qualified default for type entity class name
	public String getFullyQualifiedDefaultForTypeClassName() {
		return this.fullyQualifiedDefaultForTypeClassName;
	}

	private void syncFullyQualifiedDefaultForTypeClassName(String name) {
		String old = this.fullyQualifiedDefaultForTypeClassName;
		this.fullyQualifiedDefaultForTypeClassName = name;
		this.firePropertyChanged(FULLY_QUALIFIED_DEFAULT_FOR_TYPE_CLASS_NAME_PROPERTY, old, name);
	}

	private String buildFullyQualifiedDefaultForTypeClassName(CompilationUnit astRoot) {
		return (this.defaultForType == null) ? null : ASTTools.resolveFullyQualifiedName(this.defaultForTypeAdapter.getExpression(astRoot));
	}
	//************************ parameters ***********************
	@Override
	public ListIterable<ParameterAnnotation> getParameters() {
		return this.parametersContainer.getNestedAnnotations();
	}

	@Override
	public int getParametersSize() {
		return this.parametersContainer.getNestedAnnotationsSize();
	}

	@Override
	public ParameterAnnotation parameterAt(int index) {
		return this.parametersContainer.getNestedAnnotation(index);
	}

	@Override
	public ParameterAnnotation addParameter(int index) {
		return this.parametersContainer.addNestedAnnotation(index);
	}

	@Override
	public void moveParameter(int targetIndex, int sourceIndex) {
		this.parametersContainer.moveNestedAnnotation(targetIndex, sourceIndex);
	}
	
	@Override
	public void removeParameter(int index) {
		this.parametersContainer.removeNestedAnnotation(index);
	}

	ParameterAnnotation buildParameter(int index) {
		return SourceParameterAnnotation.createParameter(this, this.annotatedElement, this.daa, Hibernate.GENERIC_GENERATOR__PARAMETERS, index);
	}


	private DeclarationAnnotationElementAdapter<String> buildNameDeclarationAdapter() {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daa, Hibernate.TYPE_DEF__NAME);
	}
	
	private AnnotationElementAdapter<String> buildNameAdapter() {
		return this.buildStringElementAdapter(this.nameDeclarationAdapter);
	}

	/**
	 * adapt the AnnotationContainer interface to the override's join columns
	 */
	class ParametersAnnotationContainer
		extends AnnotationContainer<ParameterAnnotation>
	{
		
		@Override
		protected String getNestedAnnotationsListName() {
			return PARAMETERS_LIST;
		}
		@Override
		protected String getElementName() {
			return Hibernate.TYPE_DEF__PARAMETERS;
		}
		@Override
		protected String getNestedAnnotationName() {
			return ParameterAnnotation.ANNOTATION_NAME;
		}
		@Override
		protected ParameterAnnotation buildNestedAnnotation(int index) {
			return TypeDefAnnotationImpl.this.buildParameter(index);
		}
	}

	private DeclarationAnnotationElementAdapter<String> buildTypeClassDeclarationAdapter() {
		return new ConversionDeclarationAnnotationElementAdapter<String>(daa,
				Hibernate.TYPE_DEF__TYPE_CLASS,
				SimpleTypeStringExpressionConverter.instance());//primitives are not allowed!
	}
	
	private AnnotationElementAdapter<String> buildTypeClassAdapter() {
		return this.buildStringElementAdapter(this.typeClassDeclarationAdapter);
	}

	private DeclarationAnnotationElementAdapter<String> buildDefForTypeDeclarationAdapter() {
		return new ConversionDeclarationAnnotationElementAdapter<String>(daa,
				Hibernate.TYPE_DEF__DEF_FOR_TYPE,
				TypeStringExpressionConverter.instance());//primitives are allowed!
	}
	
	private AnnotationElementAdapter<String> buildDefForTypeAdapter() {
		return this.buildStringElementAdapter(this.defForTypeDeclarationAdapter);
	}
	
	public static NestableAnnotation buildTypeDefAnnotationAnnotation(
			JavaResourceAnnotatedElement parent,
			AnnotatedElement annotatedElement, int index) {
		IndexedDeclarationAnnotationAdapter idaa = buildTypeDefDeclarationAnnotationAdapter(index);
		IndexedAnnotationAdapter iaa = buildTypeDefAnnotationAdapter(annotatedElement, idaa);
		return new TypeDefAnnotationImpl(
			parent,
			annotatedElement,
			idaa,
			iaa);
	}
	
	protected static IndexedAnnotationAdapter buildTypeDefAnnotationAdapter(AnnotatedElement annotatedElement, IndexedDeclarationAnnotationAdapter idaa) {
		return new ElementIndexedAnnotationAdapter(annotatedElement, idaa);
	}
	
	private static IndexedDeclarationAnnotationAdapter buildTypeDefDeclarationAnnotationAdapter(int index) {
		IndexedDeclarationAnnotationAdapter idaa =
				new CombinationIndexedDeclarationAnnotationAdapter(
						DECLARATION_ANNOTATION_ADAPTER,
						CONTAINER_DECLARATION_ANNOTATION_ADAPTER,
						index,
						ANNOTATION_NAME);
		return idaa;
	}



}
