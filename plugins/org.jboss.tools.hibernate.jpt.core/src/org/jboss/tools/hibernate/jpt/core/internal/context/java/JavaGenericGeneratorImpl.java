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
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.Generator;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaGenerator;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaGenericGeneratorImpl extends AbstractJavaGenerator 
											implements JavaGenericGenerator, Messages {
	
	private String strategy;
	
	protected final List<JavaParameter> parameters;
	
	protected GenericGeneratorAnnotation generatorResource;
	
	public static List<String> generatorClasses = new ArrayList<String>();
	
	/**
	 * @see org.hibernate.id.IdentifierGeneratorFactory.GENERATORS
	 */
	static {
		generatorClasses.add( "uuid"); //$NON-NLS-1$
		generatorClasses.add( "hilo"); //$NON-NLS-1$
		generatorClasses.add( "assigned"); //$NON-NLS-1$
		generatorClasses.add( "identity"); //$NON-NLS-1$
		generatorClasses.add( "select"); //$NON-NLS-1$
		generatorClasses.add( "sequence"); //$NON-NLS-1$
		generatorClasses.add( "seqhilo"); //$NON-NLS-1$
		generatorClasses.add( "increment"); //$NON-NLS-1$
		generatorClasses.add( "foreign"); //$NON-NLS-1$
		generatorClasses.add( "guid"); //$NON-NLS-1$
		generatorClasses.add( "uuid.hex"); //$NON-NLS-1$
		generatorClasses.add( "sequence-identity"); //$NON-NLS-1$
	}

	/**
	 * @param parent
	 */
	public JavaGenericGeneratorImpl(JavaJpaContextNode parent) {
		super(parent);
		this.parameters = new ArrayList<JavaParameter>();
	}
	
	protected GenericGeneratorAnnotation getResourceGenerator() {
		return this.generatorResource;
	}

	public void initialize(GenericGeneratorAnnotation generator) {
		this.generatorResource = generator;
		this.name = generator.getName();
		this.strategy = generator.getStrategy();
		this.initializeParameters();
	}

	public void update(GenericGeneratorAnnotation generator) {
		this.generatorResource = generator;
		this.setName_(generator.getName());
		this.setSpecifiedStrategy_(generator.getStrategy());
		this.updateParameters();
		this.getPersistenceUnit().addGenerator(this);
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.generatorResource.setName(name);
		this.firePropertyChanged(Generator.NAME_PROPERTY, old, name);
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

	public void setStrategy(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		getResourceGenerator().setStrategy(strategy);
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
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateStrategy(messages, reporter, astRoot);
	}
	
	/**
	 * Method validates GenericGenerator.strategy. Generator strategy either a predefined Hibernate
	 * strategy or a fully qualified class name.
	 * 
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	protected void validateStrategy(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot){
		if (strategy != null) {
			int lineNum = getValidationTextRange(astRoot) == null ? 0 : getValidationTextRange(astRoot).getLineNumber();
			if (strategy.trim().length() == 0) {
				messages.add(creatErrorMessage(STRATEGY_CANT_BE_EMPTY, new String[]{}, lineNum));
			} else if (!generatorClasses.contains(strategy)){
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(strategy);
					if (lwType == null || !lwType.isClass()){
						messages.add(creatErrorMessage(STRATEGY_CLASS_NOT_FOUND, new String[]{strategy}, lineNum));
					} else {
						 if (!isImplementsIdentifierInterface(lwType)){
							messages.add(creatErrorMessage(STRATEGY_INTERFACE, new String[]{strategy}, lineNum));
						 }
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @param lwType
	 * @return <code>true</code> if type implements IdentifierGenerator interface.
	 * @throws JavaModelException
	 */
	protected boolean isImplementsIdentifierInterface(IType type) throws JavaModelException{
		if (type == null) return false;
		String[] interfaces = type.getSuperInterfaceNames();
		if (Arrays.binarySearch(interfaces, "org.hibernate.id.IdentifierGenerator") >= 0) {//$NON-NLS-1$
			return true;
		} else if (type.getSuperclassName() != null){
			IType parentType = getJpaProject().getJavaProject().findType(type.getSuperclassName());
			if (parentType != null){
				return isImplementsIdentifierInterface(parentType);
			}			
		}
		return false;
	}
	
	protected IMessage creatErrorMessage(String strmessage, String[] params, int lineNum){
		IMessage message = new LocalMessage(Messages.class.getName(), IMessage.HIGH_SEVERITY, 
			strmessage, params, getResource());
			message.setLineNo(lineNum);
		return message;
	}
	
	@Override
	protected HibernateJpaFactory getJpaFactory() {
		return (HibernateJpaFactory) super.getJpaFactory();
	}
	
	//************************ parameters ***********************

	public JavaParameter addParameter(int index) {
		JavaParameter parameter = getJpaFactory().buildJavaParameter(this);
		this.parameters.add(index, parameter);
		this.getResourceGenerator().addParameter(index);
		this.fireItemAdded(GenericGenerator.PARAMETERS_LIST, index, parameter);
		return parameter;
	}
	
	protected void addParameter(int index, JavaParameter parameter) {
		addItemToList(index, parameter, this.parameters, GenericGenerator.PARAMETERS_LIST);
	}
	
	protected void addParameter(JavaParameter parameter) {
		addParameter(this.parameters.size(), parameter);
	}
	
	public void removeParameter(Parameter parameter) {
		removeParameter(this.parameters.indexOf(parameter));	
	}
	
	public void removeParameter(int index) {
		JavaParameter removedParameter = this.parameters.remove(index);
		this.getResourceGenerator().removeParameter(index);
		fireItemRemoved(GenericGenerator.PARAMETERS_LIST, index, removedParameter);	
	}
	
	protected void removeParameter_(JavaParameter parameter) {
		removeItemFromList(parameter, this.parameters, GenericGenerator.PARAMETERS_LIST);
	}	

	public void moveParameter(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.parameters, targetIndex, sourceIndex);
		this.getResourceGenerator().moveParameter(targetIndex, sourceIndex);
		fireItemMoved(GenericGenerator.PARAMETERS_LIST, targetIndex, sourceIndex);	
	}

	public ListIterator<JavaParameter> parameters() {
		return new CloneListIterator<JavaParameter>(this.parameters);
	}

	public int parametersSize() {
		return parameters.size();
	}	
	
	protected void initializeParameters() {
		ListIterator<ParameterAnnotation> resourceParameters = this.generatorResource.parameters();
		
		while(resourceParameters.hasNext()) {
			this.parameters.add(createParameter(resourceParameters.next()));
		}
	}
	
	protected void updateParameters() {
		ListIterator<JavaParameter> contextParameters = parameters();
		ListIterator<ParameterAnnotation> resourceParameters = this.generatorResource.parameters();
		
		while (contextParameters.hasNext()) {
			JavaParameter parameter = contextParameters.next();
			if (resourceParameters.hasNext()) {
				parameter.update(resourceParameters.next());
			}
			else {
				removeParameter_(parameter);
			}
		}
		
		while (resourceParameters.hasNext()) {
			addParameter(createParameter(resourceParameters.next()));
		}
	}

	protected JavaParameter createParameter(ParameterAnnotation resourceParameter) {
		JavaParameter parameter =  getJpaFactory().buildJavaParameter(this);
		parameter.initialize(resourceParameter);
		return parameter;
	}
	

	public int getDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}

}
