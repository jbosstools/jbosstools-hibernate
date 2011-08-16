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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.ContextContainerTools;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDefImpl.ParameterContainerAdapter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaTypeDefImpl extends AbstractJavaJpaContextNode implements JavaTypeDef, Messages {
	
	protected TypeDefAnnotation typeDefAnnotation;
	
	protected String name;
	
	protected String typeClass;
	
	protected String defaultForTypeClass;
	
	protected final Vector<JavaParameter> parameters = new Vector<JavaParameter>();
	protected final ParameterContainerAdapter parameterContainerAdapter = new ParameterContainerAdapter();

	public JavaTypeDefImpl(JavaJpaContextNode parent, TypeDefAnnotation typeDefAnnotation) {
		super(parent);
		this.typeDefAnnotation = typeDefAnnotation;
		this.name = typeDefAnnotation.getName();
		this.typeClass = typeDefAnnotation.getTypeClass();
		this.defaultForTypeClass = typeDefAnnotation.getDefaultForType();
		this.initializeParameters();
	}
	
	public HibernatePersistenceUnit getPersistenceUnit() {
		return (HibernatePersistenceUnit)this.getParent().getPersistenceUnit();
	}
	
	@Override
	public TypeDefAnnotation getTypeDefAnnotation() {
		return this.typeDefAnnotation;
	}
	
	// ********** synchronize/update **********
	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setName_(this.typeDefAnnotation.getName());
		this.setTypeClass_(typeDefAnnotation.getTypeClass());
		this.setDefaultForTypeClass_(typeDefAnnotation.getDefaultForType());
		this.syncParameters();
	}
	
	@Override
	public void update() {
		super.update();
		this.getPersistenceUnit().addTypeDef(this);
		this.updateNodes(this.getParameters());
	}
		
	
	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}
	
	// ********** name **********

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.typeDefAnnotation.setName(name);
		this.setName_(name);
	}

	protected void setName_(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}
	
	// ********** type class **********

	public String getTypeClass() {
		return this.typeClass;
	}

	public void setTypeClass(String typeClass) {
		this.typeDefAnnotation.setTypeClass(typeClass);
		this.setTypeClass_(typeClass);
	}

	protected void setTypeClass_(String typeClass) {
		String old = this.typeClass;
		this.typeClass = typeClass;
		this.firePropertyChanged(TYPE_CLASS_PROPERTY, old, typeClass);
	}

	protected String getResourceTypeClass() {
		return this.typeDefAnnotation.getTypeClass();
	}

	public char getTypeClassEnclosingTypeSeparator() {
		return '.';
	}
	
	// ********** target class **********
	public String getDefaultForTypeClass() {
		return this.defaultForTypeClass;
	}

	public void setDefaultForTypeClass(String defaultForType) {
		this.typeDefAnnotation.setDefaultForType(defaultForType);
		this.setDefaultForTypeClass_(defaultForType);
	}

	protected void setDefaultForTypeClass_(String defaultForType) {
		String old = this.defaultForTypeClass;
		this.defaultForTypeClass = defaultForType;
		this.firePropertyChanged(DEF_FOR_TYPE_PROPERTY, old, defaultForType);
	}

	public char getDefaultForTypeClassEnclosingTypeSeparator() {
		return '.';
	}
	
	//************************ parameters ***********************
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
		ParameterAnnotation annotation = this.typeDefAnnotation.addParameter(index);
		return this.addParameter_(index, annotation);
	}

	public void removeParameter(Parameter parameter) {
		this.removeParameter(this.parameters.indexOf(parameter));
	}

	public void removeParameter(int index) {
		this.typeDefAnnotation.removeParameter(index);
		this.removeParameter_(index);
	}

	protected void removeParameter_(int index) {
		this.removeItemFromList(index, this.parameters, PARAMETERS_LIST);
	}

	public void moveParameter(int targetIndex, int sourceIndex) {
		this.typeDefAnnotation.moveParameter(targetIndex, sourceIndex);
		this.moveItemInList(targetIndex, sourceIndex, this.parameters, PARAMETERS_LIST);
	}

	protected void initializeParameters() {
		for (Iterator<ParameterAnnotation> stream = this.typeDefAnnotation.parameters(); stream.hasNext(); ) {
			this.parameters.add(this.buildParameter(stream.next()));
		}
	}

	protected JavaParameter buildParameter(ParameterAnnotation parameterAnnotation) {
		return this.getJpaFactory().buildJavaParameter(this, parameterAnnotation);
	}

	protected void syncParameters() {
		ContextContainerTools.synchronizeWithResourceModel(this.parameterContainerAdapter);
	}

	protected Iterable<ParameterAnnotation> getParameterAnnotations() {
		return CollectionTools.iterable(this.typeDefAnnotation.parameters());
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
			return JavaTypeDefImpl.this.getParameters();
		}
		public Iterable<ParameterAnnotation> getResourceElements() {
			return JavaTypeDefImpl.this.getParameterAnnotations();
		}
		public ParameterAnnotation getResourceElement(JavaParameter contextElement) {
			return contextElement.getParameterAnnotation();
		}
		public void moveContextElement(int index, JavaParameter element) {
			JavaTypeDefImpl.this.moveParameter_(index, element);
		}
		public void addContextElement(int index, ParameterAnnotation resourceElement) {
			JavaTypeDefImpl.this.addParameter_(index, resourceElement);
		}
		public void removeContextElement(JavaParameter element) {
			JavaTypeDefImpl.this.removeParameter_(element);
		}
	}
	
	// ********** text ranges **********

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.getSelectionTextRange(astRoot);
	}

	public TextRange getSelectionTextRange(CompilationUnit astRoot) {
		return this.typeDefAnnotation.getTextRange(astRoot);
	}
	
	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.typeDefAnnotation.getNameTextRange(astRoot);
	}
	
	public TextRange getTypeClassTextRange(CompilationUnit astRoot) {
		return this.typeDefAnnotation.getTypeClassTextRange(astRoot);
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		
		if (StringTools.stringIsEmpty(this.name)){
			messages.add(
					HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,
							NAME_CANT_BE_EMPTY,
							this,
							this.getNameTextRange(astRoot))
				
			);
		}
		
		if (!StringTools.stringIsEmpty(this.typeClass)){
			IType lwType = null;
			try {
				lwType = getJpaProject().getJavaProject().findType(typeClass);
				if (lwType == null || !lwType.isClass()){
					messages.add(HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,TYPE_CLASS_NOT_FOUND, new String[]{typeClass}, this, this.getTypeClassTextRange(astRoot)));
				} else {
					 if (!JpaUtil.isTypeImplementsInterface(getJpaProject().getJavaProject(), lwType, USER_TYPE_INTERFACE)){
						messages.add(HibernateJpaValidationMessage.buildMessage(
								IMessage.HIGH_SEVERITY,IMPLEMENT_USER_TYPE_INTERFACE, new String[]{typeClass}, this, this.getTypeClassTextRange(astRoot)));
					 }
				}
			} catch (JavaModelException e) {
				// just ignore it!
			}
		}
		
		
		for (ListIterator<JavaTypeDef> stream = this.getPersistenceUnit().typeDefs(); stream.hasNext(); ) {
			JavaTypeDef typeDef = stream.next();
			if (this != typeDef){
				if (this.name.equals(typeDef.getName())) {
					messages.add(
							HibernateJpaValidationMessage.buildMessage(
									IMessage.HIGH_SEVERITY,
									TYPE_DEF_DUPLICATE_NAME,
									new String[]{this.name},
									this,
									this.getNameTextRange(astRoot))
					);
					break;
				}
			}
		}
		
	}

}
