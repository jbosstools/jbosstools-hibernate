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
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.iterable.ArrayListIterable;
import org.eclipse.jpt.common.utility.internal.iterable.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.context.orm.EntityMappings;
import org.eclipse.jpt.jpa.core.internal.context.ContextContainerTools;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaDbGenerator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 * 
 */
public class JavaDbGenericGeneratorImpl extends AbstractJavaDbGenerator<GenericGeneratorAnnotation>
implements JavaDbGenericGenerator, Messages {
	
	private String strategy;

	protected final Vector<JavaParameter> parameters = new Vector<JavaParameter>();
	protected final ParameterContainerAdapter parameterContainerAdapter = new ParameterContainerAdapter();

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
	public JavaDbGenericGeneratorImpl(HibernateGenericGeneratorContainer parent, GenericGeneratorAnnotation generatorAnnotation) {
		super(parent, generatorAnnotation);
		this.strategy = generatorAnnotation.getStrategy();
		this.initializeParameters();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setStrategy_(this.generatorAnnotation.getStrategy());
		this.syncParameters();
	}

	@Override
	public void update() {
		super.update();
		this.updateNodes(this.getParameters());
	}
	
	// ********** metadata conversion **********
	@Override
	public HibernateGenericGeneratorContainer getParent() {
		return (HibernateGenericGeneratorContainer) super.getParent();
	}
	
	public void convertTo(EntityMappings entityMappings) {
		//what is this?
	}

	public void delete() {
		this.getParent().removeGenericGenerator(this);
	}
	
	// ********** misc **********

	public Class<GenericGenerator> getType() {
		return GenericGenerator.class;
	}

	// ********** strategy **********
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

	public TextRange getStrategyTextRange(){
		return this.generatorAnnotation.getStrategyTextRange();
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
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		validateStrategy(messages, reporter);
	}

	/**
	 * Method validates GenericGenerator.strategy. Generator strategy either a predefined Hibernate
	 * strategy or a fully qualified class name.
	 * 
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	protected void validateStrategy(List<IMessage> messages, IReporter reporter){
		if (this.strategy != null) {
			TextRange range = getStrategyTextRange() == null ? TextRange.Empty.instance() : getStrategyTextRange();
			if (this.strategy.trim().length() == 0) {
				messages.add(HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY, STRATEGY_CANT_BE_EMPTY, getResource(), range));
			} else if (!generatorClasses.contains(this.strategy)){
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(this.strategy);
					if (lwType == null || !lwType.isClass()){
						messages.add(HibernateJpaValidationMessage.buildMessage(
								IMessage.HIGH_SEVERITY,
								STRATEGY_CLASS_NOT_FOUND,
								new String[]{this.strategy},
								getResource(),
								range));
					} else {
						if (!JpaUtil.isTypeImplementsInterface(getJpaProject().getJavaProject(), lwType, "org.hibernate.id.IdentifierGenerator")){//$NON-NLS-1$
							messages.add(HibernateJpaValidationMessage.buildMessage(
									IMessage.HIGH_SEVERITY,
									STRATEGY_INTERFACE,
									new String[]{this.strategy},
									getResource(),
									range));
						}
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
			}
		}
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}

	//************************ parameters ***********************
	public ListIterable<JavaParameter> getParameters() {
		return new LiveCloneListIterable<JavaParameter>(this.parameters);
	}

	public int getParametersSize() {
		return this.parameters.size();
	}

	public JavaParameter addParameter() {
		return this.addParameter(this.parameters.size());
	}

	public JavaParameter addParameter(int index) {
		ParameterAnnotation annotation = this.generatorAnnotation.addParameter(index);
		return this.addParameter_(index, annotation);
	}

	public void removeParameter(Parameter parameter) {
		this.removeParameter(this.parameters.indexOf(parameter));
	}

	public void removeParameter(int index) {
		this.generatorAnnotation.removeParameter(index);
		this.removeParameter_(index);
	}

	protected void removeParameter_(int index) {
		this.removeItemFromList(index, this.parameters, PARAMETERS_LIST);
	}

	public void moveParameter(int targetIndex, int sourceIndex) {
		this.generatorAnnotation.moveParameter(targetIndex, sourceIndex);
		this.moveItemInList(targetIndex, sourceIndex, this.parameters, PARAMETERS_LIST);
	}

	protected void initializeParameters() {
		for (ParameterAnnotation param : this.generatorAnnotation.getParameters()) {
			this.parameters.add(this.buildParameter(param));
		}
	}

	protected JavaParameter buildParameter(ParameterAnnotation parameterAnnotation) {
		return this.getJpaFactory().buildJavaParameter(this, parameterAnnotation);
	}

	protected void syncParameters() {
		ContextContainerTools.synchronizeWithResourceModel(this.parameterContainerAdapter);
	}

	protected Iterable<ParameterAnnotation> getParameterAnnotations() {
		return this.generatorAnnotation.getParameters();
	}

	protected void moveParameter_(int index, JavaParameter parameter) {
		this.moveItemInList(index, parameter, this.parameters, PARAMETERS_LIST);
	}

	protected JavaParameter addParameter_(int index, ParameterAnnotation parameterAnnotation) {
		JavaParameter parameter = this.buildParameter(parameterAnnotation);
		this.addItemToList(index, parameter, this.parameters, PARAMETERS_LIST);
		return parameter;
	}

	protected void removeParameter_(JavaParameter parameter) {
		this.removeParameter_(this.parameters.indexOf(parameter));
	}

	/**
	 * parameter container adapter
	 */
	protected class ParameterContainerAdapter
		implements ContextContainerTools.Adapter<JavaParameter, ParameterAnnotation>
	{
		public Iterable<JavaParameter> getContextElements() {
			return JavaDbGenericGeneratorImpl.this.getParameters();
		}
		public Iterable<ParameterAnnotation> getResourceElements() {
			return JavaDbGenericGeneratorImpl.this.getParameterAnnotations();
		}
		public ParameterAnnotation getResourceElement(JavaParameter contextElement) {
			return contextElement.getParameterAnnotation();
		}
		public void moveContextElement(int index, JavaParameter element) {
			JavaDbGenericGeneratorImpl.this.moveParameter_(index, element);
		}
		public void addContextElement(int index, ParameterAnnotation resourceElement) {
			JavaDbGenericGeneratorImpl.this.addParameter_(index, resourceElement);
		}
		public void removeContextElement(JavaParameter element) {
			JavaDbGenericGeneratorImpl.this.removeParameter_(element);
		}
	}
	
	@Override
	public int buildDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode#javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterable<String> getCompletionProposals(int pos) {
		Iterable<String> result = super.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		TextRange strategyRange = getStrategyTextRange();
		if (strategyRange != null && strategyRange.touches(pos)) {
			return new ArrayListIterable<String>(generatorClasses.toArray(new String[generatorClasses.size()]));
		}
		return null;
	}

//	private Iterable<String> getJavaCandidateNames(Filter<String> filter) {
//		return StringTools.convertToJavaStringLiterals(this
//				.getCandidateNames(filter));
//	}
//
//	private Iterable<String> getCandidateNames(Filter<String> filter) {
//		return new FilteringIterable<String>(generatorClasses, filter);
//	}
//	
}
