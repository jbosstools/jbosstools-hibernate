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
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.iterable.ArrayListIterable;
import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.type.TypeFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeImpl extends AbstractJavaJpaContextNode implements JavaType, Messages {

	private TypeAnnotation annotation;
	
	private String type;
	
	public TypeImpl(JpaContextNode parent, TypeAnnotation annotation) {
		super(parent);
		this.annotation = annotation;
		this.type = annotation.getType();
	}
	
	public void synchronizeWithResourceModel() {
		this.setType_(annotation.getType());
	}

	// ***** name
	
	public String getType() {
		return type;
	}
	
	public void setType(String name) {
		String old = this.type;
		this.type = name;
		this.getTypeAnnotation().setType(name);
		this.firePropertyChanged(TYPE_TYPE, old, name);
	}
	
	public void setType_(String name) {
		String old = this.type;
		this.type = name;
		this.firePropertyChanged(TYPE_TYPE, old, name);
	}
	
	public TypeAnnotation getTypeAnnotation() {
		return annotation;
	}

	public TextRange getValidationTextRange() {
		return this.annotation.getTextRange();
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		validateType(messages, reporter);
	}
	
	@Override
	public HibernatePersistenceUnit getPersistenceUnit() {
		return (HibernatePersistenceUnit) this.getParent().getPersistenceUnit();
	}
	
	public TextRange getTypeTextRange() {
		return this.annotation.getTypeTextRange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode
	 * #getJavaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterable<String> getCompletionProposals(int pos) {
		Iterable<String> result = super.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		TextRange typeRange = getTypeTextRange();
		if (typeRange != null && typeRange.touches(pos)) {
			return new ArrayListIterable<String>(getPersistenceUnit().uniqueTypeDefNames());
//			return getJavaCandidateNames();
		}
		return null;
	}

//	private Iterable<String> getJavaCandidateNames(Filter<String> filter) {
//		return StringTools.convertToJavaStringLiterals(this
//				.getCandidateNames(filter));
//	}
//
//	private Iterable<String> getCandidateNames(Filter<String> filter) {
//		return new FilteringIterable<String>(Arrays.asList(getPersistenceUnit()
//				.uniqueTypeDefNames()), filter);
//	}

	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	protected void validateType(List<IMessage> messages, IReporter reporter) {
		//TODO implement TypeDefs package-level support
		if (type != null) {
			TextRange range = getTypeTextRange() == null ? TextRange.Empty.instance() : getTypeTextRange();
			if (type.trim().length() == 0) {
				messages.add(HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						TYPE_CANT_BE_EMPTY, this, range));
			} else if (TypeFactory.basic(type) == null && !getPersistenceUnit().hasTypeDef(type))	{
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(type);
					if (lwType == null || !lwType.isClass()){
						messages.add(HibernateJpaValidationMessage.buildMessage(
								IMessage.HIGH_SEVERITY,TYPE_CLASS_NOT_FOUND, new String[]{type}, this, range));
					} else {
						Boolean isImplements = JpaUtil.isTypeImplementsOneOfInterfaces(getJpaProject().getJavaProject(), lwType,
								 JavaTypeDef.POSSIBLE_INTERFACES);
						if (isImplements == null){
							messages.add(HibernateJpaValidationMessage.buildMessage(
									IMessage.HIGH_SEVERITY,INCONSISTENT_TYPE_HIERARCHY, new String[]{type}, this, range));
						} else if (!isImplements){
							messages.add(HibernateJpaValidationMessage.buildMessage(
									IMessage.HIGH_SEVERITY,IMPLEMENT_USER_TYPE_INTERFACE, new String[]{type}, this, range));
						 }
					}
				} catch (JavaModelException e) {
					// just ignore it!
				}
			}
		}
	}

}
