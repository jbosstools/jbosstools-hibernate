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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.Generator;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaGenerator;
import org.eclipse.jpt.core.resource.java.GeneratorAnnotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.id.Assigned;
import org.hibernate.id.ForeignGenerator;
import org.hibernate.id.GUIDGenerator;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.IncrementGenerator;
import org.hibernate.id.SelectGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.SequenceHiLoGenerator;
import org.hibernate.id.SequenceIdentityGenerator;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.id.UUIDHexGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaGenericGeneratorImpl extends AbstractJavaGenerator 
											implements JavaGenericGenerator, Messages {
	
	private String strategy;
	
	protected GeneratorAnnotation generatorResource;
	
	private static List<String> generatorClasses = new ArrayList<String>();
	
	//see org.hibernate.id.IdentifierGeneratorFactory.GENERATORS
	static{
		generatorClasses.add( "uuid");
		generatorClasses.add( "hilo");
		generatorClasses.add( "assigned");
		generatorClasses.add( "identity");
		generatorClasses.add( "select");
		generatorClasses.add( "sequence");
		generatorClasses.add( "seqhilo");
		generatorClasses.add( "increment");
		generatorClasses.add( "foreign");
		generatorClasses.add( "guid");
		generatorClasses.add( "uuid.hex");
		generatorClasses.add( "sequence-identity");
	}

	/**
	 * @param parent
	 */
	public JavaGenericGeneratorImpl(JavaJpaContextNode parent) {
		super(parent);
	}
	
	protected GenericGeneratorAnnotation getGeneratorResource() {
		return (GenericGeneratorAnnotation) generatorResource;
	}

	public int getDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}
	
	protected GeneratorAnnotation getResourceGenerator() {
		return this.generatorResource;
	}

	public void initialize(GenericGeneratorAnnotation generator) {
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
		this.getPersistenceUnit().addGenerator(this);		
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode#validate(java.util.List, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateStrategy(messages, reporter, astRoot);
	}
	
	protected void validateStrategy(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot){
		if (strategy != null){
			if (strategy.trim().length() == 0){
				IMessage message = new LocalMessage(Messages.class.getName(), IMessage.HIGH_SEVERITY, 
					STRATEGY_CANT_BE_EMPTY, new String[]{}, getResource());
				if (getValidationTextRange(astRoot) != null){
					message.setLineNo(getValidationTextRange(astRoot).getLineNumber());
				}				
				messages.add(message);
			} else if (!generatorClasses.contains(strategy)){				
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(strategy);
					if (lwType == null && strategy.indexOf('.') < 0
							&& astRoot.getPackage() != null){
						String pack = astRoot.getPackage().getName().getFullyQualifiedName();
						if (pack != null && pack.length() > 0)
							lwType = getJpaProject().getJavaProject().findType(pack+'.'+strategy);
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
				if (lwType == null){
					IMessage message = new LocalMessage(Messages.class.getName(), IMessage.HIGH_SEVERITY, 
						STRATEGY_CLASS_NOT_FOUND, new String[]{strategy}, getResource());
					if (getValidationTextRange(astRoot) != null){
						message.setLineNo(getValidationTextRange(astRoot).getLineNumber());
					}				
					messages.add(message);
				}
			}
		}
	}

}
