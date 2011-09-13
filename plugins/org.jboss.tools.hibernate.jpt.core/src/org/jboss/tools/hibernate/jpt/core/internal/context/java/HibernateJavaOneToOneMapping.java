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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaOneToOneMapping;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKeyHolder;
/**
 *
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernateJavaOneToOneMapping extends
		AbstractJavaOneToOneMapping implements ForeignKeyHolder {

	protected ForeignKey foreignKey;

	public HibernateJavaOneToOneMapping(JavaPersistentAttribute parent) {
		super(parent);
		this.foreignKey = buildForeignKey();
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.syncForeignKey();
	}

	@Override
	public void update() {
		super.update();
		if (foreignKey != null){
			this.foreignKey.update();
		}
	}

	// ********************* foreignKey **************

	protected void syncForeignKey() {
		ForeignKeyAnnotation annotation = getForeignKeyAnnotation();
		if (annotation == null) {
			if (getForeignKey() != null) {
				setForeignKey(null);
			}
		}
		else {
			if (getForeignKey() == null) {
				setForeignKey(buildForeignKey(annotation));
			}
			else {
				if ((this.foreignKey != null) && (this.foreignKey.getForeignKeyAnnotation() == annotation)) {
					this.foreignKey.synchronizeWithResourceModel();
				} else {
					this.setForeignKey(this.buildForeignKey(annotation));
				}
			}
		}
	}

	@Override
	public ForeignKey addForeignKey() {
		if (getForeignKey() != null) {
			throw new IllegalStateException("foreignKey already exists"); //$NON-NLS-1$
		}
		ForeignKeyAnnotation annotation = (ForeignKeyAnnotation) this.getResourcePersistentAttribute().addAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		ForeignKey foreignKey = buildForeignKey(annotation);
		setForeignKey(foreignKey);
		return this.foreignKey;
	}

	@Override
	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}

	protected void setForeignKey(ForeignKey newForeignKey) {
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = newForeignKey;
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, newForeignKey);
	}

	@Override
	public void removeForeignKey() {
		if (getForeignKey() == null) {
			throw new IllegalStateException("foreignKey does not exist, cannot be removed"); //$NON-NLS-1$
		}
		this.getResourcePersistentAttribute().removeAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		setForeignKey(null);
	}
	
	protected ForeignKey buildForeignKey() {
		ForeignKeyAnnotation annotation = this.getForeignKeyAnnotation();
		return (annotation == null) ? null : this.buildForeignKey(annotation);
	}

	protected ForeignKey buildForeignKey(ForeignKeyAnnotation annotation) {
		return getJpaFactory().buildForeignKey(this, annotation);
	}

	protected ForeignKeyAnnotation getForeignKeyAnnotation() {
		return (ForeignKeyAnnotation) this.getResourcePersistentAttribute().getAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
	}

	public Table getForeignKeyDbTable() {
		return getTypeMapping().getPrimaryDbTable();
	}

	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.validateForeignKey(messages, reporter, astRoot);
	}

	protected void validateForeignKey(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		if (foreignKey != null){
			foreignKey.validate(messages, reporter, astRoot);
		}
	}
}
