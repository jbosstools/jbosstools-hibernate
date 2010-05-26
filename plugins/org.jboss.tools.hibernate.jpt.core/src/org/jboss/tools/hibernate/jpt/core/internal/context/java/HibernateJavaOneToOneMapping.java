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
import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.context.java.JavaRelationshipReference;
import org.eclipse.jpt.core.internal.context.java.GenericJavaOneToOneMapping;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKeyHolder;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaOneToOneMapping extends GenericJavaOneToOneMapping
implements ForeignKeyHolder {

	protected ForeignKey foreignKey;

	public HibernateJavaOneToOneMapping(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	@Override
	public String getJoinTableDefaultName() {
		return NamingStrategyMappingTools.buildJoinTableDefaultName(this);
	}
	
	@Override
	protected JavaRelationshipReference buildRelationshipReference() {
		return new HibernateJavaOneToOneRelationshipReference(this);
	}
	
	public Iterator<String> supportingAnnotationNames() {
		return new CompositeIterator<String>(super.supportingAnnotationNames(),
			Hibernate.FOREIGN_KEY);
	}
	
	@Override
	protected HibernateJpaFactory getJpaFactory() {
		return (HibernateJpaFactory) super.getJpaFactory();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.initializeForeignKey();
	}
	
	@Override
	protected void update() {
		super.update();
		this.updateForeignKey();
	}
	

	
	// *** foreignKey
	
	protected void initializeForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource != null) {
			this.foreignKey = buildForeignKey(foreignKeyResource);
		}
	}
	
	protected void updateForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource == null) {
			if (getForeignKey() != null) {
				setForeignKey(null);
			}
		}
		else {
			if (getForeignKey() == null) {
				setForeignKey(buildForeignKey(foreignKeyResource));
			}
			else {
				getForeignKey().update(foreignKeyResource);
			}
		}
	}
	
	public ForeignKey addForeignKey() {
		if (getForeignKey() != null) {
			throw new IllegalStateException("foreignKey already exists"); //$NON-NLS-1$
		}
		this.foreignKey = getJpaFactory().buildForeignKey(this);
		ForeignKeyAnnotation foreignKeyResource = (ForeignKeyAnnotation) getResourcePersistentAttribute().addSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		this.foreignKey.initialize(foreignKeyResource);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, null, this.foreignKey);
		return this.foreignKey;
	}

	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}
	
	protected void setForeignKey(ForeignKey newForeignKey) {
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = newForeignKey;
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, newForeignKey);
	}

	public void removeForeignKey() {
		if (getForeignKey() == null) {
			throw new IllegalStateException("foreignKey does not exist, cannot be removed"); //$NON-NLS-1$
		}
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = null;
		this.getResourcePersistentAttribute().removeSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, null);
	}
	
	protected ForeignKey buildForeignKey(ForeignKeyAnnotation foreignKeyResource) {
		ForeignKey foreignKey = getJpaFactory().buildForeignKey(this);
		foreignKey.initialize(foreignKeyResource);
		return foreignKey;
	}
	
	protected ForeignKeyAnnotation getResourceForeignKey() {
		return (ForeignKeyAnnotation) this.getResourcePersistentAttribute().getSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
	}
	
	public Table getForeignKeyDbTable() {
		return getTypeMapping().getPrimaryDbTable();
	}
	
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.validateForeignKey(messages, astRoot);
	}

	protected void validateForeignKey(List<IMessage> messages, CompilationUnit astRoot) {
		Table table = getTypeMapping().getPrimaryDbTable();
		if (!shouldValidateAgainstDatabase() || foreignKey == null || table == null ){
			return;
		}		
		Iterator<org.eclipse.jpt.db.ForeignKey> fks = table.foreignKeys();
		while (fks.hasNext()) {
			org.eclipse.jpt.db.ForeignKey fk = (org.eclipse.jpt.db.ForeignKey) fks.next();
			if (foreignKey.getName().equals(fk.getIdentifier())){
				return;
			}
		}
		TextRange textRange = this.getResourceForeignKey().getNameTextRange(astRoot);
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY, 
				Messages.UNRESOLVED_FOREIGN_KEY_NAME, new String[] {foreignKey.getName(), getTypeMapping().getPrimaryTableName()},
				this.foreignKey);
		message.setLineNo(textRange.getLineNumber());
		message.setOffset(textRange.getOffset());
		message.setLength(textRange.getLength());
		messages.add(message);		
	}

}

