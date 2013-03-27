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

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.AttributeMapping;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.context.ManyToManyMapping;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaContextModel;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class ForeignKeyImpl extends AbstractJavaContextModel<JpaContextModel> implements ForeignKey {

	private ForeignKeyAnnotation annotation;
	
	private String name;
	
	private String inverseName;

	public ForeignKeyImpl(JpaContextModel parent, ForeignKeyAnnotation annotation) {
		super(parent);
		this.annotation = annotation;
		this.name = annotation.getName();
		this.inverseName = annotation.getInverseName();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setName_(annotation.getName());
		this.setInverseName_(annotation.getInverseName());
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

	public TextRange getValidationTextRange() {
		return this.annotation.getTextRange();
	}

	@Override
	public ForeignKeyAnnotation getForeignKeyAnnotation() {
		return annotation;
	}

	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		this.validateName(messages, reporter);
		this.validateInverseName(messages, reporter);
	}

	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	private void validateName(List<IMessage> messages, IReporter reporter) {
		validateForeignKeyName(messages, this.name, getResourceForeignKey().getNameTextRange());
	}
	
	/**
	 * @param messages
	 * @param reporter
	 * @param astRoot
	 */
	private void validateInverseName(List<IMessage> messages,
			IReporter reporter) {
		if (getParent() instanceof ManyToManyMapping
				&& ((ManyToManyMapping)getParent()).isRelationshipOwner()){
				validateForeignKeyName(messages, this.inverseName, getResourceForeignKey().getInverseNameTextRange());
		} else {
			//according to @ForeinKey javadoc inverseName ignored in other places
		}
	}
		
	private void validateForeignKeyName(List<IMessage> messages, String name, TextRange range) {
		if (name == null || name.trim().length() == 0) {
			messages.add(HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY, Messages.NAME_CANT_BE_EMPTY,
					getResource(), range));
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
			messages.add(HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY, Messages.UNRESOLVED_FOREIGN_KEY_NAME,
					getResource(), range));
		}
	}
}
