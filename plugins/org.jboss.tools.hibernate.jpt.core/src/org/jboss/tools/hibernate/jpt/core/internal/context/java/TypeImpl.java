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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
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
	
	public HibernatePersistenceUnit getPersistenceUnit() {
		return (HibernatePersistenceUnit) this.getParent().getPersistenceUnit();
	}
	
	public TextRange getTypeTextRange(CompilationUnit astRoot) {
		return this.typeResource.getTypeTextRange(astRoot);
	}

	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	protected void validateType(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		//TODO implement TypeDefs package-level support
		if (type != null) {
			TextRange range = getTypeTextRange(astRoot) == null ? TextRange.Empty.instance() : getTypeTextRange(astRoot);
			if (type.trim().length() == 0) {
				messages.add(creatErrorMessage(TYPE_CANT_BE_EMPTY, new String[]{}, range));
			} else if (!getPersistenceUnit().hasTypeDef(type))	{
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(type);
					if (lwType == null || !lwType.isClass()){
						messages.add(creatErrorMessage(TYPE_CLASS_NOT_FOUND, new String[]{type}, range));
					} else {
						 if (!JpaUtil.isTypeImplementsInterface(getJpaProject().getJavaProject(), lwType, "org.hibernate.usertype.UserType")){//$NON-NLS-1$
							messages.add(creatErrorMessage(USER_TYPE_INTERFACE, new String[]{type}, range));
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

}
