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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorAnnotationImpl extends SourceAnnotation<Member> 
					implements GenericGeneratorAnnotation {
	
	private final AnnotationElementAdapter<String> nameAdapter;
	
	private final AnnotationElementAdapter<String> strategyAdapter;

	private static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> NAME_ADAPTER = buildNameAdapter();
	
	private static final DeclarationAnnotationElementAdapter<String> STRATEGY_ADAPTER = buildStrategyAdapter();

	private String name;
	
	private String strategy;
	
	private Integer initialValue = 1;
	
	private Integer allocationSize = 1;
	
	/**
	 * @param parent
	 * @param member
	 */
	public GenericGeneratorAnnotationImpl(JavaResourcePersistentMember parent, Member member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
		this.nameAdapter = new ShortCircuitAnnotationElementAdapter<String>(member, NAME_ADAPTER);
		this.strategyAdapter = new ShortCircuitAnnotationElementAdapter<String>(member, STRATEGY_ADAPTER);
	}
	
	public void initialize(CompilationUnit astRoot) {
		this.name = this.name(astRoot);
		this.strategy = this.strategy(astRoot);		
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
		return this.getElementTextRange(NAME_ADAPTER, astRoot);
	}
	
	public TextRange getStrategyTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(STRATEGY_ADAPTER, astRoot);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#getAllocationSize()
	 */
	public Integer getAllocationSize() {
		return allocationSize;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#getAllocationSizeTextRange(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	public TextRange getAllocationSizeTextRange(CompilationUnit astRoot) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#getInitialValue()
	 */
	public Integer getInitialValue() {
		return initialValue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#getInitialValueTextRange(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	public TextRange getInitialValueTextRange(CompilationUnit astRoot) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#setAllocationSize(java.lang.Integer)
	 */
	public void setAllocationSize(Integer allocationSize) {
		this.allocationSize = allocationSize;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.resource.java.GeneratorAnnotation#setInitialValue(java.lang.Integer)
	 */
	public void setInitialValue(Integer initialValue) {
		this.initialValue = initialValue;
		
	}
	
	// ********** java annotations -> persistence model **********
	public void updateFromJava(CompilationUnit astRoot) {
		this.setStrategy(this.strategy(astRoot));
		this.setName(this.name(astRoot));
	}

	protected String strategy(CompilationUnit astRoot) {
		//TODO: get Generator instead of String
		//use buildJavaGenericGenerator method before thi will be done
		return this.strategyAdapter.getValue(astRoot);
	}
	
	protected String name(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotation#buildJavaGenericGenerator(org.eclipse.jpt.core.context.java.JavaJpaContextNode)
	 */
	public JavaGenericGenerator buildJavaGenericGenerator(JavaJpaContextNode parent) {
		JavaGenericGenerator generator = new JavaGenericGeneratorImpl(parent);
		generator.initializeFromResource(this);
		return generator;
	}
	
	//for Dali 2.1
	public void update(CompilationUnit astRoot) {
		updateFromJava(astRoot);		
	}
	
	// ********** static methods **********
	private static DeclarationAnnotationElementAdapter<String> buildNameAdapter() {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(DECLARATION_ANNOTATION_ADAPTER, Hibernate.GENERIC_GENERATOR__NAME, false);
	}
	
	private static DeclarationAnnotationElementAdapter<String> buildStrategyAdapter() {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(DECLARATION_ANNOTATION_ADAPTER, Hibernate.GENERIC_GENERATOR__STRATEGY, false);
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
			return new GenericGeneratorAnnotationImpl(parent, member);
		}
		
		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent, Member member) {
			return null;
		}
		
		public String getAnnotationName() {
			return ANNOTATION_NAME;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jpt.core.resource.java.AnnotationDefinition#buildAnnotation(org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember, org.eclipse.jdt.core.IAnnotation)
		 */
		public Annotation buildAnnotation(JavaResourcePersistentMember arg0,
				IAnnotation arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jpt.core.resource.java.AnnotationDefinition#buildNullAnnotation(org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember)
		 */
		public Annotation buildNullAnnotation(JavaResourcePersistentMember arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
