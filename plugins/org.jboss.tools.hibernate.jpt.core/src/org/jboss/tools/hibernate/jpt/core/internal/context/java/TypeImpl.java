/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeImpl extends AbstractJavaJpaContextNode implements JavaType, Messages {

	private TypeAnnotation typeResource;
	
	private String type;
	
	public TypeImpl(JavaJpaContextNode parent) {
		super(parent);
	}

	public void initialize(TypeAnnotation indexResource) {
		this.typeResource = indexResource;
		this.type = indexResource.getType();
	}
	
	public void update(TypeAnnotation indexResource) {
		this.typeResource = indexResource;
		this.setType_(indexResource.getType());
	}

	// ***** name
	
	public String getType() {
		return type;
	}
	
	public void setType(String name) {
		String old = this.type;
		this.type = name;
		this.getTypeResource().setType(name);
		this.firePropertyChanged(TYPE_TYPE, old, name);
	}
	
	public void setType_(String name) {
		String old = this.type;
		this.type = name;
		this.firePropertyChanged(TYPE_TYPE, old, name);
	}
	
	public TypeAnnotation getTypeResource() {
		return typeResource;
	}

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.typeResource.getTextRange(astRoot);
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateType(messages, reporter, astRoot);
	}

	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	protected void validateType(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		//TODO implement TypeDefs first as type could be a TypeDef
		/*if (type != null) {
			int lineNum = getValidationTextRange(astRoot) == null ? 0 : getValidationTextRange(astRoot).getLineNumber();
			if (type.trim().length() == 0) {
				messages.add(creatErrorMessage(TYPE_CANT_BE_EMPTY, new String[]{}, lineNum));
			} else if (!persistentUnit.hasTypeDef(contains(type)))	{
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(type);
					if (lwType == null || !lwType.isClass()){
						messages.add(creatErrorMessage(STRATEGY_CLASS_NOT_FOUND, new String[]{type}, lineNum));
					} else {
						 if (!isImplementsUserTypeInterface(lwType)){
							messages.add(creatErrorMessage(USER_TYPE_INTERFACE, new String[]{type}, lineNum));
						 }
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
			}
		}*/
	}
	
	/**
	 * 
	 * @param lwType
	 * @return <code>true</code> if type implements UserType interface.
	 * @throws JavaModelException
	 */
	protected boolean isImplementsUserTypeInterface(IType type) throws JavaModelException{
		if (type == null) return false;
		String[] interfaces = type.getSuperInterfaceNames();
		for (String interface_ : interfaces) {
			if ("org.hibernate.usertype.UserType".equals(interface_)) //$NON-NLS-1$
				return true;
		}
		if (type.getSuperclassName() != null){
			IType parentType = getJpaProject().getJavaProject().findType(type.getSuperclassName());
			if (parentType != null){
				if (isImplementsUserTypeInterface(parentType)){
					return true;
				}
			}			
		}
		for (String interface_ : interfaces) {
			IType parentType = getJpaProject().getJavaProject().findType(interface_);
			if (isImplementsUserTypeInterface(parentType)){
				return true;
			}
		}
		return false;
	}
	
	protected IMessage creatErrorMessage(String strmessage, String[] params, int lineNum){
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY, 
			strmessage, params, getResource());
			message.setLineNo(lineNum);
		return message;
	}

}
