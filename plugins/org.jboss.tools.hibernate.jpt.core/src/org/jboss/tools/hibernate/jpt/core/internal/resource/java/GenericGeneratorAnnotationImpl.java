/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.MemberAnnotationAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.MemberIndexedAnnotationAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.NestedIndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationContainer;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.IndexedAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.IndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.StringTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorAnnotationImpl extends SourceAnnotation<Member> 
					implements GenericGeneratorAnnotation {
	
	private static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final DeclarationAnnotationElementAdapter<String> nameDeclarationAdapter;
	private final AnnotationElementAdapter<String> nameAdapter;
	private String name;
	
	private final DeclarationAnnotationElementAdapter<String> strategyDeclarationAdapter;
	private final AnnotationElementAdapter<String> strategyAdapter;
	private String strategy;	
	
	final Vector<NestableParameterAnnotation> parameters = new Vector<NestableParameterAnnotation>();
	final ParametersAnnotationContainer parametersContainer = new ParametersAnnotationContainer();
	
	/**
	 * @param parent
	 * @param member
	 */
	public GenericGeneratorAnnotationImpl(JavaResourceNode parent, Member member,
			DeclarationAnnotationAdapter daa, AnnotationAdapter annotationAdapter) {
		super(parent, member, daa, annotationAdapter);
		this.nameDeclarationAdapter = this.buildNameAdapter(daa);
		this.nameAdapter = new ShortCircuitAnnotationElementAdapter<String>(member, nameDeclarationAdapter);
		this.strategyDeclarationAdapter = this.buildStrategyAdapter(daa);
		this.strategyAdapter = new ShortCircuitAnnotationElementAdapter<String>(member, strategyDeclarationAdapter);
	}
	
	public void initialize(CompilationUnit astRoot) {
		this.name = this.name(astRoot);
		this.strategy = this.strategy(astRoot);	
		AnnotationContainerTools.initialize(this.parametersContainer, astRoot);
	}
	
	public void update(CompilationUnit astRoot) {
		this.setStrategy(this.strategy(astRoot));
		this.setName(this.name(astRoot));
		AnnotationContainerTools.update(this.parametersContainer, astRoot);
	}
	
	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public String getStrategy() {
		return this.strategy;
	}
	
	public void setStrategy(String newStrategy) {
		if (attributeValueHasNotChanged(this.strategy, newStrategy)) {
			return;
		}
		String oldStrategy = this.strategy;
		this.strategy = newStrategy;
		this.strategyAdapter.setValue(newStrategy);
		firePropertyChanged(STRATEGY_PROPERTY, oldStrategy, newStrategy);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		if (attributeValueHasNotChanged(this.name, newName)) {
			return;
		}
		String oldName = this.name;
		this.name = newName;
		this.nameAdapter.setValue(newName);
		firePropertyChanged(NAME_PROPERTY, oldName, newName);
	}
	
	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(nameDeclarationAdapter, astRoot);
	}
	
	public TextRange getStrategyTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(strategyDeclarationAdapter, astRoot);
	}
	
	public Integer getAllocationSize() {
		return null;
	}

	public TextRange getAllocationSizeTextRange(CompilationUnit astRoot) {
		return null;
	}

	public Integer getInitialValue() {
		return null;
	}

	public TextRange getInitialValueTextRange(CompilationUnit astRoot) {
		return null;
	}

	public void setAllocationSize(Integer allocationSize) {
		throw new UnsupportedOperationException();		
	}

	public void setInitialValue(Integer initialValue) {
		throw new UnsupportedOperationException();			
	}
	
	// ********** java annotations -> persistence model **********
	protected String strategy(CompilationUnit astRoot) {
		//TODO: get Generator instead of String
		//use buildJavaGenericGenerator method before this will be done
		return this.strategyAdapter.getValue(astRoot);
	}
	
	protected String name(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}	
	

	//************************ parameters ***********************
	
	public NestableParameterAnnotation addParameter(int index) {
		return (NestableParameterAnnotation) AnnotationContainerTools.addNestedAnnotation(index, this.parametersContainer);
	}
	
	NestableParameterAnnotation addParameterInternal() {
		NestableParameterAnnotation parameter = this.buildParameter(this.parameters.size());
		this.parameters.add(parameter);
		return parameter;
	}
	
	NestableParameterAnnotation buildParameter(int index) {
		return SourceParameterAnnotation.createGenericGeneratorParameter(this, this.member, this.daa, index);
	}
	
	ListIterator<NestableParameterAnnotation> nestableParameters() {
		return new CloneListIterator<NestableParameterAnnotation>(this.parameters);
	}
	
	void parameterAdded(int index, NestableParameterAnnotation parameter) {
		this.fireItemAdded(PARAMETERS_LIST, index, parameter);
	}
	
	NestableParameterAnnotation moveParameterInternal(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.parameters, targetIndex, sourceIndex).get(targetIndex);
	}

	void parameterMoved(int targetIndex, int sourceIndex) {
		this.fireItemMoved(PARAMETERS_LIST, targetIndex, sourceIndex);
	}

	public int indexOfParameter(ParameterAnnotation parameter) {
		return this.parameters.indexOf(parameter);
	}

	public void moveParameter(int targetIndex, int sourceIndex) {
		AnnotationContainerTools.moveNestedAnnotation(targetIndex, sourceIndex, this.parametersContainer);
	}

	public ParameterAnnotation parameterAt(int index) {
		return this.parameters.get(index);
	}

	public ListIterator<ParameterAnnotation> parameters() {
		return new CloneListIterator<ParameterAnnotation>(this.parameters);
	}

	public int parametersSize() {
		return parameters.size();
	}

	public void removeParameter(int index) {
		AnnotationContainerTools.removeNestedAnnotation(index, this.parametersContainer);	
	}
	
	NestableParameterAnnotation removeParameterInternal(int index) {
		return this.parameters.remove(index);
	}

	void parameterRemoved(int index, NestableParameterAnnotation parameter) {
		this.fireItemRemoved(PARAMETERS_LIST, index, parameter);
	}
	
	// ********** NestableAnnotation implementation **********
	public void initializeFrom(NestableAnnotation oldAnnotation) {
		GenericGeneratorAnnotation oldGenericGenerator = (GenericGeneratorAnnotation) oldAnnotation;
		this.setName(oldGenericGenerator.getName());
		this.setStrategy(oldGenericGenerator.getStrategy());
		//this.setAllocationSize(oldGenericGenerator.getAllocationSize());
		//this.setInitialValue(oldGenericGenerator.getInitialValue());
		for (ParameterAnnotation oldParameter : CollectionTools.iterable(oldGenericGenerator.parameters())) {
			NestableParameterAnnotation newParameter = this.addParameter(oldGenericGenerator.indexOfParameter(oldParameter));
			newParameter.initializeFrom((NestableParameterAnnotation) oldParameter);
		}
	}
	

	/**
	 * convenience implementation of method from NestableAnnotation interface
	 * for subclasses
	 */
	public void moveAnnotation(int newIndex) {
		this.getIndexedAnnotationAdapter().moveAnnotation(newIndex);
	}

	private IndexedAnnotationAdapter getIndexedAnnotationAdapter() {
		return (IndexedAnnotationAdapter) this.annotationAdapter;
	}
	
	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		sb.append(name);
	}
	
	private DeclarationAnnotationElementAdapter<String> buildNameAdapter(DeclarationAnnotationAdapter daa) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daa, Hibernate.GENERIC_GENERATOR__NAME, false);
	}
	
	private DeclarationAnnotationElementAdapter<String> buildStrategyAdapter(DeclarationAnnotationAdapter daa) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daa, Hibernate.GENERIC_GENERATOR__STRATEGY, false);
	}	
	
	/**
	 * adapt the AnnotationContainer interface to the override's join columns
	 */
	class ParametersAnnotationContainer
		implements AnnotationContainer<NestableParameterAnnotation>
	{
		public String getContainerAnnotationName() {
			return GenericGeneratorAnnotationImpl.this.getAnnotationName();
		}

		public org.eclipse.jdt.core.dom.Annotation getContainerJdtAnnotation(CompilationUnit astRoot) {
			return GenericGeneratorAnnotationImpl.this.getJdtAnnotation(astRoot);
		}

		public String getElementName() {
			return Hibernate.GENERIC_GENERATOR__PARAMETERS;
		}

		public String getNestableAnnotationName() {
			return ParameterAnnotation.ANNOTATION_NAME;
		}

		public ListIterator<NestableParameterAnnotation> nestedAnnotations() {
			return GenericGeneratorAnnotationImpl.this.nestableParameters();
		}

		public int nestedAnnotationsSize() {
			return GenericGeneratorAnnotationImpl.this.parametersSize();
		}

		public NestableParameterAnnotation addNestedAnnotationInternal() {
			return GenericGeneratorAnnotationImpl.this.addParameterInternal();
		}

		public void nestedAnnotationAdded(int index, NestableParameterAnnotation nestedAnnotation) {
			GenericGeneratorAnnotationImpl.this.parameterAdded(index, nestedAnnotation);
		}

		public NestableParameterAnnotation moveNestedAnnotationInternal(int targetIndex, int sourceIndex) {
			return GenericGeneratorAnnotationImpl.this.moveParameterInternal(targetIndex, sourceIndex);
		}

		public void nestedAnnotationMoved(int targetIndex, int sourceIndex) {
			GenericGeneratorAnnotationImpl.this.parameterMoved(targetIndex, sourceIndex);
		}

		public NestableParameterAnnotation removeNestedAnnotationInternal(int index) {
			return GenericGeneratorAnnotationImpl.this.removeParameterInternal(index);
		}

		public void nestedAnnotationRemoved(int index, NestableParameterAnnotation nestedAnnotation) {
			GenericGeneratorAnnotationImpl.this.parameterRemoved(index, nestedAnnotation);
		}

		@Override
		public String toString() {
			return StringTools.buildToStringFor(this);
		}
	}

	public static GenericGeneratorAnnotation createNestedGenericGenerator(
			JavaResourceNode parent, Member member,
			int index, DeclarationAnnotationAdapter attributeOverridesAdapter) {
		IndexedDeclarationAnnotationAdapter idaa = buildNestedHibernateDeclarationAnnotationAdapter(index, attributeOverridesAdapter);
		IndexedAnnotationAdapter annotationAdapter = new MemberIndexedAnnotationAdapter(member, idaa);
		return new GenericGeneratorAnnotationImpl(parent, member, idaa, annotationAdapter);
	}
	
	private static IndexedDeclarationAnnotationAdapter buildNestedHibernateDeclarationAnnotationAdapter(int index, DeclarationAnnotationAdapter hibernateGenericGeneratorsAdapter) {
		return new NestedIndexedDeclarationAnnotationAdapter(hibernateGenericGeneratorsAdapter, index, Hibernate.GENERIC_GENERATOR);
	}

	public static class GenericGeneratorAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final GenericGeneratorAnnotationDefinition INSTANCE = new GenericGeneratorAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private GenericGeneratorAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member member) {
			return new GenericGeneratorAnnotationImpl(parent, member,
				DECLARATION_ANNOTATION_ADAPTER, new MemberAnnotationAdapter(member, DECLARATION_ANNOTATION_ADAPTER));
		}
		
		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent, Member member) {
			throw new UnsupportedOperationException();
		}
		
		public String getAnnotationName() {
			return GenericGeneratorAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember arg0,
				IAnnotation arg1) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(JavaResourcePersistentMember arg0) {
			throw new UnsupportedOperationException();
		}
	}

}
