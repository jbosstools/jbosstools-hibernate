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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.Generator;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaGenerator;
import org.eclipse.jpt.core.resource.java.GeneratorAnnotation;
import org.eclipse.jpt.core.utility.TextRange;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaGenericGeneratorImpl extends AbstractJavaGenerator 
											implements JavaGenericGenerator {
	
	private String strategy;
	
	protected GeneratorAnnotation generatorResource;

	/**
	 * @param parent
	 */
	public JavaGenericGeneratorImpl(JavaJpaContextNode parent) {
		super(parent);
	}
	
	protected GenericGeneratorAnnotation getGeneratorResource() {
		return (GenericGeneratorAnnotation) generatorResource;
	}

	public Integer getDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}
	
	protected GeneratorAnnotation getResourceGenerator() {
		return this.generatorResource;
	}

	public void initializeFromResource(GenericGeneratorAnnotation generator) {
		generatorResource = generator;
		this.name = generator.getName();
		this.specifiedInitialValue = generator.getInitialValue();
		this.specifiedAllocationSize = generator.getAllocationSize();
		this.strategy = generator.getStrategy();
	}

	public void update(GenericGeneratorAnnotation generator) {
		this.generatorResource = generator;
		this.setName_(generator.getName());
		this.setSpecifiedInitialValue_(generator.getInitialValue());
		this.setSpecifiedAllocationSize_(generator.getAllocationSize());
		this.setSpecifiedStrategy_(generator.getStrategy());
		//getPersistenceUnit().addGenerator(this);		
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.generatorResource.setName(name);
		this.firePropertyChanged(Generator.NAME_PROPERTY, old, name);
	}
	
	public void setSpecifiedInitialValue(Integer specifiedInitialValue) {
		Integer old = this.specifiedInitialValue;
		this.specifiedInitialValue = specifiedInitialValue;
		this.generatorResource.setInitialValue(specifiedInitialValue);
		this.firePropertyChanged(Generator.SPECIFIED_INITIAL_VALUE_PROPERTY, old, specifiedInitialValue);
	}
	
	public void setSpecifiedAllocationSize(Integer specifiedAllocationSize) {
		Integer old = this.specifiedAllocationSize;
		this.specifiedAllocationSize = specifiedAllocationSize;
		this.generatorResource.setAllocationSize(specifiedAllocationSize);
		this.firePropertyChanged(Generator.SPECIFIED_ALLOCATION_SIZE_PROPERTY, old, specifiedAllocationSize);
	}
	
	public TextRange getSelectionTextRange(CompilationUnit astRoot) {
		return this.generatorResource.getTextRange(astRoot);
	}
	
	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.generatorResource.getNameTextRange(astRoot);
	}
	
	public String getStrategy() {
		return strategy;
	}

	public void setSpecifiedStrategy(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		getGeneratorResource().setStrategy(strategy);
		firePropertyChanged(GENERIC_STRATEGY_PROPERTY, oldStrategy, strategy);
	}
	
	protected void setSpecifiedStrategy_(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		firePropertyChanged(GENERIC_STRATEGY_PROPERTY, oldStrategy, strategy);
	}

	protected String getCatalog() {
		return null;
	}

	protected String getSchema() {
		return null;
	}

}
