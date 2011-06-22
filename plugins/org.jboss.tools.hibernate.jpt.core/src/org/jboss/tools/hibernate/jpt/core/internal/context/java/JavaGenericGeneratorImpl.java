/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.iterables.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.Generator;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaGenerator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 * 
 */
public class JavaGenericGeneratorImpl extends AbstractJavaGenerator<GenericGeneratorAnnotation>
implements JavaGenericGenerator, Messages {

	private String strategy;

	protected final Vector<JavaParameter> parameters = new Vector<JavaParameter>();

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
	public JavaGenericGeneratorImpl(JavaJpaContextNode parent, GenericGeneratorAnnotation generatorAnnotation) {
		super(parent, generatorAnnotation);
		this.initializeParameters();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setStrategy_(this.generatorAnnotation.getStrategy());
		this.updateParameters();
	}

	@Override
	public void update() {
		super.update();
		this.setName_(this.generatorAnnotation.getName());
		this.setStrategy_(this.generatorAnnotation.getStrategy());
		this.updateNodes(this.getParameters());
	}

	@Override
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.generatorAnnotation.setName(name);
		this.firePropertyChanged(Generator.NAME_PROPERTY, old, name);
	}

	@Override
	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.generatorAnnotation.getNameTextRange(astRoot);
	}

	public String getStrategy() {
		return this.strategy;
	}

	public void setStrategy(String strategy) {
		getGeneratorAnnotation().setStrategy(strategy);
		setStrategy_(strategy);
	}

	protected void setStrategy_(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		firePropertyChanged(GENERIC_STRATEGY_PROPERTY, oldStrategy, strategy);
	}

	public TextRange getStrategyTextRange(CompilationUnit astRoot){
		return this.generatorAnnotation.getStrategyTextRange(astRoot);
	}

	@Override
	protected String getCatalog() {
		return null;
	}

	@Override
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
		if (this.strategy != null) {
			TextRange range = getStrategyTextRange(astRoot) == null ? TextRange.Empty.instance() : getStrategyTextRange(astRoot);
			if (this.strategy.trim().length() == 0) {
				messages.add(creatErrorMessage(STRATEGY_CANT_BE_EMPTY, new String[]{}, range));
			} else if (!generatorClasses.contains(this.strategy)){
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(this.strategy);
					if (lwType == null || !lwType.isClass()){
						messages.add(creatErrorMessage(STRATEGY_CLASS_NOT_FOUND, new String[]{this.strategy}, range));
					} else {
						if (!JpaUtil.isTypeImplementsInterface(getJpaProject().getJavaProject(), lwType, "org.hibernate.id.IdentifierGenerator")){//$NON-NLS-1$
							messages.add(creatErrorMessage(STRATEGY_INTERFACE, new String[]{this.strategy}, range));
						}
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
			}
		}
	}

	protected IMessage creatErrorMessage(String strmessage, String[] params, TextRange range){
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY,
				strmessage, params, getResource());
		message.setLineNo(range.getLineNumber());
		message.setOffset(range.getOffset());
		message.setLength(range.getLength());
		return message;
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}

	//************************ parameters ***********************

	public JavaParameter addParameter(int index) {
		JavaParameter parameter = getJpaFactory().buildJavaParameter(this);
		this.parameters.add(index, parameter);
		ParameterAnnotation parameterAnnotation = this.getGeneratorAnnotation().addParameter(index);
		parameter.initialize(parameterAnnotation);
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
		this.getGeneratorAnnotation().removeParameter(index);
		fireItemRemoved(GenericGenerator.PARAMETERS_LIST, index, removedParameter);
	}

	protected void removeParameter_(JavaParameter parameter) {
		removeItemFromList(parameter, this.parameters, GenericGenerator.PARAMETERS_LIST);
	}

	public void moveParameter(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.parameters, targetIndex, sourceIndex);
		this.getGeneratorAnnotation().moveParameter(targetIndex, sourceIndex);
		fireItemMoved(GenericGenerator.PARAMETERS_LIST, targetIndex, sourceIndex);
	}
	
	public ListIterable<JavaParameter> getParameters() {
		return new LiveCloneListIterable<JavaParameter>(this.parameters);
	}

	public ListIterator<JavaParameter> parameters() {
		return new CloneListIterator<JavaParameter>(this.parameters);
	}

	public int parametersSize() {
		return this.parameters.size();
	}

	protected void initializeParameters() {
		ListIterator<ParameterAnnotation> resourceParameters = this.generatorAnnotation.parameters();

		while(resourceParameters.hasNext()) {
			this.parameters.add(createParameter(resourceParameters.next()));
		}
	}

	protected void updateParameters() {
		ListIterator<JavaParameter> contextParameters = parameters();
		ListIterator<ParameterAnnotation> resourceParameters = this.generatorAnnotation.parameters();

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

	@Override
	public int buildDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode#javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		TextRange strategyRange = getStrategyTextRange(astRoot);
		if (strategyRange != null && strategyRange.touches(pos)) {
			return getJavaCandidateNames(filter).iterator();
		}
		return null;
	}

	private Iterable<String> getJavaCandidateNames(Filter<String> filter) {
		return StringTools.convertToJavaStringLiterals(this
				.getCandidateNames(filter));
	}

	private Iterable<String> getCandidateNames(Filter<String> filter) {
		return new FilteringIterable<String>(generatorClasses, filter);
	}

}
