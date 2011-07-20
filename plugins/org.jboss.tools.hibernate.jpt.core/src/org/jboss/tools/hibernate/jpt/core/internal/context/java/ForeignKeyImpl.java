/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.AttributeMapping;
import org.eclipse.jpt.jpa.core.context.ManyToManyMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;

/**
 * @author Dmitry Geraskov
 *
 */
public class ForeignKeyImpl extends AbstractJavaJpaContextNode implements ForeignKey {

	private ForeignKeyAnnotation annotation;
	
	private String name;
	
	private String inverseName;

	public ForeignKeyImpl(JavaJpaContextNode parent, ForeignKeyAnnotation annotation) {
		super(parent);
		this.annotation = annotation;
		this.name = annotation.getName();
		this.inverseName = annotation.getInverseName();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setName_(annotation.getName());
		this.setInverseName(annotation.getInverseName());
	}
	
	private ForeignKeyAnnotation getResourceForeignKey() {
		return annotation;
	}

	// ***** name
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.getResourceForeignKey().setName(name);
		this.firePropertyChanged(FOREIGN_KEY_NAME, old, name);
	}
	
	public void setName_(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(FOREIGN_KEY_NAME, old, name);
	}
	
	// ***** inverseName
	
	public String getInverseName() {
		return inverseName;
	}
	
	public void setInverseName(String inverseName) {
		String old = this.inverseName;
		this.inverseName = inverseName;
		this.getResourceForeignKey().setInverseName(inverseName);
		this.firePropertyChanged(FOREIGN_KEY_INVERSE_NAME, old, inverseName);
	}
	
	public void setInverseName_(String inverseName) {
		String old = this.inverseName;
		this.inverseName = inverseName;
		this.firePropertyChanged(FOREIGN_KEY_INVERSE_NAME, old, inverseName);
	}

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.annotation.getTextRange(astRoot);
	}

	@Override
	public ForeignKeyAnnotation getForeignKeyAnnotation() {
		return annotation;
	}

	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.validateName(messages, reporter, astRoot);
		this.validateInverseName(messages, reporter, astRoot);
	}

	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	private void validateName(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		validateForeignKeyName(messages, this.name, getResourceForeignKey().getNameTextRange(astRoot));
	}
	
	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	private void validateInverseName(List<IMessage> messages,
			IReporter reporter, CompilationUnit astRoot) {
		if (getParent() instanceof ManyToManyMapping
				&& ((ManyToManyMapping)getParent()).isRelationshipOwner()){
				validateForeignKeyName(messages, this.inverseName, getResourceForeignKey().getInverseNameTextRange(astRoot));
		} else {
			//according to @ForeinKey javadoc inverseName ignored in other places
		}
	}
		
	private void validateForeignKeyName(List<IMessage> messages, String name, TextRange range) {
		if (name == null || name.trim().length() == 0) {
			messages.add(creatErrorMessage(Messages.NAME_CANT_BE_EMPTY, range));
		} else {
			AttributeMapping mapping = (AttributeMapping) getParent();
			Table table = mapping.getTypeMapping().getPrimaryDbTable();
			if (!mapping.validatesAgainstDatabase() || table == null ){
				return;
			}
			Iterator<org.eclipse.jpt.jpa.db.ForeignKey> fks = table.getForeignKeys().iterator();
			while (fks.hasNext()) {
				org.eclipse.jpt.jpa.db.ForeignKey fk = fks.next();
				if (name.equals(fk.getIdentifier())){
					return;
				}
			}
			creatErrorMessage(Messages.UNRESOLVED_FOREIGN_KEY_NAME, range);
		}
	}
	
	protected IMessage creatErrorMessage(String strmessage, TextRange range){
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY,
				strmessage, new String[0], getResource());
		message.setLineNo(range.getLineNumber());
		message.setOffset(range.getOffset());
		message.setLength(range.getLength());
		return message;
	}

}
