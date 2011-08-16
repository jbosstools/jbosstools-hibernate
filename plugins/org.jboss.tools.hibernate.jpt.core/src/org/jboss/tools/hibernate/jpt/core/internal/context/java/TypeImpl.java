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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.iterables.FilteringIterable;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
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
	
	public TypeImpl(JavaJpaContextNode parent, TypeAnnotation annotation) {
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

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.annotation.getTextRange(astRoot);
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateType(messages, reporter, astRoot);
	}
	
	@Override
	public HibernatePersistenceUnit getPersistenceUnit() {
		return (HibernatePersistenceUnit) this.getParent().getPersistenceUnit();
	}
	
	public TextRange getTypeTextRange(CompilationUnit astRoot) {
		return this.annotation.getTypeTextRange(astRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode
	 * #javaCompletionProposals(int, org.eclipse.jpt.common.utility.Filter,
	 * org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public Iterator<String> javaCompletionProposals(int pos,
			Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter,
				astRoot);
		if (result != null) {
			return result;
		}
		TextRange typeRange = getTypeTextRange(astRoot);
		if (typeRange != null && typeRange.touches(pos)) {
			return getJavaCandidateNames(filter).iterator();
		}
		return null;
	}

	private Iterable<String> getJavaCandidateNames(Filter<String> filter) {
		return StringTools.convertToJavaStringLiterals(this
				.getCandidateNames(filter));
	}

	private Iterable<String> getCandidateNames(Filter<String> filter) {
		return new FilteringIterable<String>(Arrays.asList(getPersistenceUnit()
				.uniqueTypeDefNames()), filter);
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
				messages.add(HibernateJpaValidationMessage.buildMessage(
						IMessage.HIGH_SEVERITY,
						TYPE_CANT_BE_EMPTY, this, range));
			} else if (!getPersistenceUnit().hasTypeDef(type))	{
				IType lwType = null;
				try {
					lwType = getJpaProject().getJavaProject().findType(type);
					if (lwType == null || !lwType.isClass()){
						messages.add(HibernateJpaValidationMessage.buildMessage(
								IMessage.HIGH_SEVERITY,TYPE_CLASS_NOT_FOUND, new String[]{type}, this, range));
					} else {
						 if (!JpaUtil.isTypeImplementsInterface(getJpaProject().getJavaProject(), lwType, JavaTypeDef.USER_TYPE_INTERFACE)){
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
