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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;

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

}
