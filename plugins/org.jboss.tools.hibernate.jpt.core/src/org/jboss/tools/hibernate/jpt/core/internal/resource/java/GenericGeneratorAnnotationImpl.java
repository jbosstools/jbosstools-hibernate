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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.common.core.internal.utility.jdt.CombinationIndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ElementIndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
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
import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorAnnotationImpl extends SourceAnnotation
implements GenericGeneratorAnnotation {

	private static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);
	private static final DeclarationAnnotationAdapter CONTAINER_DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(Hibernate.GENERIC_GENERATORS);
	
	private DeclarationAnnotationElementAdapter<String> nameDeclarationAdapter;
	private AnnotationElementAdapter<String> nameAdapter;
	private String name;
	private TextRange nameTextRange;

	private DeclarationAnnotationElementAdapter<String> strategyDeclarationAdapter;
	private AnnotationElementAdapter<String> strategyAdapter;
	private String strategy;
	private TextRange strategyTextRange;

	final ParametersAnnotationContainer parametersContainer = new ParametersAnnotationContainer();

	/**
	 * @param parent
	 * @param member
	 */
	public GenericGeneratorAnnotationImpl(JavaResourceNode parent, AnnotatedElement member,
			DeclarationAnnotationAdapter daa, AnnotationAdapter annotationAdapter) {
		super(parent, member, daa, annotationAdapter);
		this.nameDeclarationAdapter = this.buildNameDeclarationAdapter();
		this.nameAdapter = buildNameAdapter();
		this.strategyDeclarationAdapter = this.buildStrategyDeclarationAdapter();
		this.strategyAdapter = buildStrategyAdapter();
	}

	@Override
	public void initialize(CompilationUnit astRoot) {
		this.name = this.buildName(astRoot);
		this.nameTextRange = this.buildNameTextRange(astRoot);
		this.strategy = this.buildStrategy(astRoot);
		this.strategyTextRange = this.buildStrategyTextRange(astRoot);
		this.parametersContainer.initializeFromContainerAnnotation(this.getAstAnnotation(astRoot));
	}

	@Override
	public void synchronizeWith(CompilationUnit astRoot) {
		this.syncName(this.buildName(astRoot));
		this.nameTextRange = this.buildNameTextRange(astRoot);
		this.syncStrategy(this.buildStrategy(astRoot));
		this.strategyTextRange = this.buildStrategyTextRange(astRoot);
		this.parametersContainer.synchronize(this.getAstAnnotation(astRoot));
	}

	@Override
	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	// ***** strategy
	@Override
	public String getStrategy() {
		return this.strategy;
	}

	@Override
	public void setStrategy(String newStrategy) {
		if (attributeValueHasChanged(this.strategy, newStrategy)) {
			this.strategy = newStrategy;
			this.strategyAdapter.setValue(newStrategy);
		}
	}

	private void syncStrategy(String strategy) {
		String old = this.strategy;
		this.strategy = strategy;
		this.firePropertyChanged(STRATEGY_PROPERTY, old, strategy);
	}
	
	protected String buildStrategy(CompilationUnit astRoot) {
		return this.strategyAdapter.getValue(astRoot);
	}
	
	@Override
	public TextRange getStrategyTextRange() {
		return this.strategyTextRange;
	}
	
	private TextRange buildStrategyTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.strategyDeclarationAdapter, getAstAnnotation(astRoot));
	}

	// ***** name
	@Override
	public String getName() {
		return this.name;
	}

	@Override
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
	
	protected String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}

	@Override
	public TextRange getNameTextRange() {
		return this.nameTextRange;
	}

	private TextRange buildNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.nameDeclarationAdapter, getAstAnnotation(astRoot));
	}

	@Override
	public Integer getAllocationSize() {
		return null;
	}

	@Override
	public TextRange getAllocationSizeTextRange() {
		return null;
	}

	@Override
	public Integer getInitialValue() {
		return null;
	}

	@Override
	public TextRange getInitialValueTextRange() {
		return null;
	}

	@Override
	public void setAllocationSize(Integer allocationSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInitialValue(Integer initialValue) {
		throw new UnsupportedOperationException();
	}

	protected ShortCircuitAnnotationElementAdapter<String> buildNameAdapter() {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, this.nameDeclarationAdapter);
	}

	protected ShortCircuitAnnotationElementAdapter<String> buildStrategyAdapter() {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, this.strategyDeclarationAdapter);
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

	// ********** misc **********

	@Override
	public boolean isUnset() {
		return super.isUnset() &&
				(this.name == null) &&
				(this.strategy == null) &&
				this.parametersContainer.isEmpty();
	}

	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		sb.append(this.name);
	}

	private DeclarationAnnotationElementAdapter<String> buildNameDeclarationAdapter() {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(this.daa, Hibernate.GENERIC_GENERATOR__NAME);
	}

	private DeclarationAnnotationElementAdapter<String> buildStrategyDeclarationAdapter() {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(this.daa, Hibernate.GENERIC_GENERATOR__STRATEGY);
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
			return Hibernate.GENERIC_GENERATOR__PARAMETERS;
		}
		public String getNestedAnnotationName() {
			return ParameterAnnotation.ANNOTATION_NAME;
		}
		@Override
		protected ParameterAnnotation buildNestedAnnotation(int index) {
			return GenericGeneratorAnnotationImpl.this.buildParameter(index);
		}
	}

	public static NestableAnnotation buildGenericGeneratorAnnotation(
			JavaResourceAnnotatedElement parent,
			AnnotatedElement annotatedElement, int index) {
		IndexedDeclarationAnnotationAdapter idaa = buildGnericGeneratorDeclarationAnnotationAdapter(index);
		IndexedAnnotationAdapter iaa = buildGenericGeneratorAnnotationAdapter(annotatedElement, idaa);
		return new GenericGeneratorAnnotationImpl(
			parent,
			annotatedElement,
			idaa,
			iaa);
	}
	
	protected static IndexedAnnotationAdapter buildGenericGeneratorAnnotationAdapter(AnnotatedElement annotatedElement, IndexedDeclarationAnnotationAdapter idaa) {
		return new ElementIndexedAnnotationAdapter(annotatedElement, idaa);
	}
	
	private static IndexedDeclarationAnnotationAdapter buildGnericGeneratorDeclarationAnnotationAdapter(int index) {
		IndexedDeclarationAnnotationAdapter idaa =
				new CombinationIndexedDeclarationAnnotationAdapter(
						DECLARATION_ANNOTATION_ADAPTER,
						CONTAINER_DECLARATION_ANNOTATION_ADAPTER,
						index,
						ANNOTATION_NAME);
		return idaa;
	}

}
