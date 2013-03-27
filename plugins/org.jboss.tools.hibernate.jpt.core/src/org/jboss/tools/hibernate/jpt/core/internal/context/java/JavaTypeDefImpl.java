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

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterable.LiveCloneListIterable;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.internal.context.ContextContainerTools;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaContextModel;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaTypeDefImpl extends AbstractJavaContextModel<JpaContextModel> implements JavaTypeDef, Messages {
	
	protected TypeDefAnnotation typeDefAnnotation;
	
	protected String name;
	
	protected String typeClass;
	
	protected String defaultForTypeClass;
	
	protected final Vector<JavaParameter> parameters = new Vector<JavaParameter>();
	protected final ParameterContainerAdapter parameterContainerAdapter = new ParameterContainerAdapter();

	public JavaTypeDefImpl(JpaContextModel parent, TypeDefAnnotation typeDefAnnotation) {
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
		this.updateModels(this.getParameters());
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
		return IterableTools.cloneLive((List<JavaParameter>)this.parameters);
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
		for (ParameterAnnotation param : this.typeDefAnnotation.getParameters()) {
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
		return this.typeDefAnnotation.getParameters();
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

	public TextRange getValidationTextRange() {
		return this.getSelectionTextRange();
	}

	public TextRange getSelectionTextRange() {
		return this.typeDefAnnotation.getTextRange();
	}
	
	public TextRange getNameTextRange() {
		return this.typeDefAnnotation.getNameTextRange();
	}
	
	public TextRange getTypeClassTextRange() {
		return this.typeDefAnnotation.getTypeClassTextRange();
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		
		if (StringTools.isBlank(this.name)){
			messages.add(
					HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,
							NAME_CANT_BE_EMPTY,
							this,
							this.getNameTextRange())
				
			);
		} else {
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
										this.getNameTextRange())
						);
						break;
					}
				}
			}
		}
		
		if (!StringTools.isBlank(this.typeClass)){
			IType lwType = null;
			try {
				lwType = getJpaProject().getJavaProject().findType(typeClass);
				if (lwType == null || !lwType.isClass()){
					messages.add(HibernateJpaValidationMessage.buildMessage(
							IMessage.HIGH_SEVERITY,TYPE_CLASS_NOT_FOUND, new String[]{typeClass}, this, this.getTypeClassTextRange()));
				} else {
					if (!JpaUtil.isTypeImplementsOneOfInterfaces(getJpaProject().getJavaProject(), lwType,
							 JavaTypeDef.POSSIBLE_INTERFACES)){
						messages.add(HibernateJpaValidationMessage.buildMessage(
								IMessage.HIGH_SEVERITY,IMPLEMENT_USER_TYPE_INTERFACE, new String[]{typeClass}, this, this.getTypeClassTextRange()));
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
									this.getNameTextRange())
					);
					break;
				}
			}
		}
		
	}

}
