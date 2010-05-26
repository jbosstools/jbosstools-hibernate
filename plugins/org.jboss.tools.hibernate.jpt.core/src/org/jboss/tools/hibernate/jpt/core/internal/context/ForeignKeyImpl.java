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

package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.ForeignKeyAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class ForeignKeyImpl extends AbstractJavaJpaContextNode implements ForeignKey {

	private ForeignKeyAnnotation foreignKeyResource;
	
	private String name;
	
	private String inverseName;

	public ForeignKeyImpl(JavaJpaContextNode parent) {
		super(parent);
	}

	public void initialize(ForeignKeyAnnotation foreignKeyResource) {
		this.foreignKeyResource = foreignKeyResource;
		this.name = foreignKeyResource.getName();
		this.inverseName = foreignKeyResource.getInverseName();
	}
	
	public void update(ForeignKeyAnnotation foreignKeyResource) {
		this.foreignKeyResource = foreignKeyResource;
		this.setName_(foreignKeyResource.getName());
		this.setInverseName_(foreignKeyResource.getInverseName());
	}
	
	private ForeignKeyAnnotation getResourceForeignKey() {
		return foreignKeyResource;
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
		return this.foreignKeyResource.getTextRange(astRoot);
	}

}
