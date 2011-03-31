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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.jpa.core.context.Embeddable;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.TypeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaTypeDefImpl extends AbstractJavaJpaContextNode implements JavaTypeDef {
	
	protected TypeDefAnnotation typeDefAnnotation;
	
	protected String name;
	
	protected String specifiedTypeClass;
	protected String defaultTypeClass;
	protected String fullyQualifiedTypeClass;
	protected PersistentType resolvedTypeType;
	
	protected String specifiedDefaultForType;
	protected String defaultDefaultForType;
	protected String fullyQualifiedDefaultForType;
	protected PersistentType resolvedTargetType;
	
	protected final List<JavaParameter> parameters;
	

	public JavaTypeDefImpl(JavaJpaContextNode parent) {
		super(parent);
		this.parameters = new ArrayList<JavaParameter>();
	}
	
	public HibernatePersistenceUnit getPersistenceUnit() {
		return (HibernatePersistenceUnit)this.getParent().getPersistenceUnit();
	}
	
	public void initialize(TypeDefAnnotation typeDefAnnotation) {
		this.typeDefAnnotation = typeDefAnnotation;
		
		this.name = typeDefAnnotation.getName();
		
		this.defaultTypeClass = this.buildDefaultTypeClass();
		this.specifiedTypeClass = this.getResourceTypeClass();
		this.fullyQualifiedTypeClass = this.buildFullyQualifiedTypeClass();
		this.resolvedTypeType = this.buildResolvedTypeType();
		
		this.defaultDefaultForType = this.buildDefaultDefaultForType();
		this.specifiedDefaultForType = this.getResourceDefaultForType();
		this.fullyQualifiedDefaultForType = this.buildFullyQualifiedDefaultForType();
		this.resolvedTargetType = this.buildResolvedTargetType();
		
		this.initializeParameters();
	}
	
	public void update(TypeDefAnnotation typeDefAnnotation) {
		this.typeDefAnnotation = typeDefAnnotation;
		
		this.setName_(typeDefAnnotation.getName());
		
		this.setDefaultTypeClass(this.buildDefaultTypeClass());
		this.setSpecifiedTypeClass_(this.getResourceTypeClass());
		this.setFullyQualifiedTypeClass(this.buildFullyQualifiedTypeClass());
		this.resolvedTypeType = this.buildResolvedTypeType();
		
		this.setDefaultDefaultForType(this.buildDefaultDefaultForType());
		this.setSpecifiedDefaultForType_(this.getResourceDefaultForType());
		this.setFullyQualifiedDefaultForType(this.buildFullyQualifiedDefaultForType());
		this.resolvedTargetType = this.buildResolvedTargetType();
		
		this.updateParameters();
		
		this.getPersistenceUnit().addTypeDef(this);
	}
	
	protected IMessage creatErrorMessage(String strmessage, String[] params, int lineNum){
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY, 
			strmessage, params, getResource());
			message.setLineNo(lineNum);
		return message;
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
		String old = this.name;
		this.name = name;
		this.typeDefAnnotation.setName(name);
		this.firePropertyChanged(TYPE_DEF_NAME, old, name);
	}

	protected void setName_(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(TYPE_DEF_NAME, old, name);
	}

	
	// ********** type class **********

	public String getTypeClass() {
		return (this.specifiedTypeClass != null) ? this.specifiedTypeClass : this.defaultTypeClass;
	}

	public String getSpecifiedTypeClass() {
		return this.specifiedTypeClass;
	}

	public void setSpecifiedTypeClass(String typeClass) {
		String old = this.specifiedTypeClass;
		this.specifiedTypeClass = typeClass;
		this.typeDefAnnotation.setTypeClass(typeClass);
		this.firePropertyChanged(SPECIFIED_TYPE_CLASS_PROPERTY, old, typeClass);
	}

	protected void setSpecifiedTypeClass_(String typeClass) {
		String old = this.specifiedTypeClass;
		this.specifiedTypeClass = typeClass;
		this.firePropertyChanged(SPECIFIED_TYPE_CLASS_PROPERTY, old, typeClass);
	}

	protected String getResourceTypeClass() {
		return this.typeDefAnnotation.getTypeClass();
	}

	public String getDefaultTypeClass() {
		return this.defaultTypeClass;
	}

	protected void setDefaultTypeClass(String typeClass) {
		String old = this.defaultTypeClass;
		this.defaultTypeClass = typeClass;
		this.firePropertyChanged(DEFAULT_TYPE_CLASS_PROPERTY, old, typeClass);
	}

	protected String buildDefaultTypeClass() {
		return null;
	}

	public String getFullyQualifiedTypeClass() {
		return this.fullyQualifiedTypeClass;
	}

	protected void setFullyQualifiedTypeClass(String typeClass) {
		String old = this.fullyQualifiedTypeClass;
		this.fullyQualifiedTypeClass = typeClass;
		this.firePropertyChanged(FULLY_QUALIFIED_TYPE_CLASS_PROPERTY, old, typeClass);
	}

	protected String buildFullyQualifiedTypeClass() {
		return (this.specifiedTypeClass == null) ?
			this.defaultTypeClass :
			this.typeDefAnnotation.getFullyQualifiedTypeClassName();
	}

	public PersistentType getResolvedTypeType() {
		return this.resolvedTypeType;
	}
	
	protected PersistentType buildResolvedTypeType() {
		return (this.fullyQualifiedTypeClass == null) ? null : this.getPersistenceUnit().getPersistentType(this.fullyQualifiedTypeClass);
	}

	public char getTypeClassEnclosingTypeSeparator() {
		return '.';
	}
	
	
	// ********** target class **********

	public String getDefaultForType() {
		return (this.specifiedDefaultForType != null) ? this.specifiedDefaultForType : this.defaultDefaultForType;
	}

	public String getSpecifiedDefaultForType() {
		return this.specifiedDefaultForType;
	}

	public void setSpecifiedDefaultForType(String defaultForType) {
		String old = this.specifiedDefaultForType;
		this.specifiedDefaultForType = defaultForType;
		this.typeDefAnnotation.setDefaultForType(defaultForType);
		this.firePropertyChanged(SPECIFIED_DEF_FOR_TYPE_PROPERTY, old, defaultForType);
	}

	protected void setSpecifiedDefaultForType_(String defaultForType) {
		String old = this.specifiedDefaultForType;
		this.specifiedDefaultForType = defaultForType;
		this.firePropertyChanged(SPECIFIED_DEF_FOR_TYPE_PROPERTY, old, defaultForType);
	}

	protected String getResourceDefaultForType() {
		return this.typeDefAnnotation.getDefaultForType();
	}

	public String getDefaultDefaultForType() {
		return this.defaultDefaultForType;
	}

	protected void setDefaultDefaultForType(String defaultForType) {
		String old = this.defaultDefaultForType;
		this.defaultDefaultForType = defaultForType;
		this.firePropertyChanged(DEFAULT_DEF_FOR_TYPE_PROPERTY, old, defaultForType);
	}

	protected String buildDefaultDefaultForType() {
		return void.class.getName();
	}

	public String getFullyQualifiedDefaultForType() {
		return this.fullyQualifiedDefaultForType;
	}

	protected void setFullyQualifiedDefaultForType(String defaultForType) {
		String old = this.fullyQualifiedDefaultForType;
		this.fullyQualifiedDefaultForType = defaultForType;
		this.firePropertyChanged(FULLY_QUALIFIED_DEF_FOR_TYPE_PROPERTY, old, defaultForType);
	}

	protected String buildFullyQualifiedDefaultForType() {
		return (this.specifiedDefaultForType == null) ?
			this.defaultDefaultForType :
			this.typeDefAnnotation.getFullyQualifiedDefaultForTypeClassName();
	}


	public PersistentType getResolvedTargetType() {
		return this.resolvedTargetType;
	}
	
	protected PersistentType buildResolvedTargetType() {
		return (this.fullyQualifiedDefaultForType == null) ? null : this.getPersistenceUnit().getPersistentType(this.fullyQualifiedDefaultForType);
	}

	protected Embeddable buildResolvedTargetEmbeddable() {
		if (this.resolvedTargetType == null) {
			return null;
		}
		TypeMapping typeMapping = this.resolvedTargetType.getMapping();
		return (typeMapping instanceof Embeddable) ? (Embeddable) typeMapping : null;
	}

	protected Entity buildResolvedTargetEntity() {
		if (this.resolvedTargetType == null) {
			return null;
		}
		TypeMapping typeMapping = this.resolvedTargetType.getMapping();
		return (typeMapping instanceof Entity) ? (Entity) typeMapping : null;
	}


	public char getDefaultForTypeEnclosingTypeSeparator() {
		return '.';
	}
	
	//************************ parameters ***********************

	public JavaParameter addParameter(int index) {
		JavaParameter parameter = getJpaFactory().buildJavaParameter(this);
		this.parameters.add(index, parameter);
		this.typeDefAnnotation.addParameter(index);
		this.fireItemAdded(JavaTypeDef.PARAMETERS_LIST, index, parameter);
		return parameter;
	}
	
	protected void addParameter(int index, JavaParameter parameter) {
		addItemToList(index, parameter, this.parameters, JavaTypeDef.PARAMETERS_LIST);
	}
	
	protected void addParameter(JavaParameter parameter) {
		addParameter(this.parameters.size(), parameter);
	}
	
	public void removeParameter(Parameter parameter) {
		removeParameter(this.parameters.indexOf(parameter));	
	}
	
	public void removeParameter(int index) {
		JavaParameter removedParameter = this.parameters.remove(index);
		this.typeDefAnnotation.removeParameter(index);
		fireItemRemoved(JavaTypeDef.PARAMETERS_LIST, index, removedParameter);	
	}
	
	protected void removeParameter_(JavaParameter parameter) {
		removeItemFromList(parameter, this.parameters, JavaTypeDef.PARAMETERS_LIST);
	}	

	public void moveParameter(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.parameters, targetIndex, sourceIndex);
		this.typeDefAnnotation.moveParameter(targetIndex, sourceIndex);
		fireItemMoved(JavaTypeDef.PARAMETERS_LIST, targetIndex, sourceIndex);	
	}

	public ListIterator<JavaParameter> parameters() {
		return new CloneListIterator<JavaParameter>(this.parameters);
	}

	public int parametersSize() {
		return parameters.size();
	}	
	
	protected void initializeParameters() {
		ListIterator<ParameterAnnotation> resourceParameters = this.typeDefAnnotation.parameters();
		
		while(resourceParameters.hasNext()) {
			this.parameters.add(createParameter(resourceParameters.next()));
		}
	}
	
	protected void updateParameters() {
		ListIterator<JavaParameter> contextParameters = parameters();
		ListIterator<ParameterAnnotation> resourceParameters = this.typeDefAnnotation.parameters();
		
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

}
