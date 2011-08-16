/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
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
import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaParameter extends AbstractJavaJpaContextNode implements JavaParameter {

	protected String name;

	protected String value;

	protected ParameterAnnotation resourceParameter;
	
	public HibernateJavaParameter(JpaContextNode parent, ParameterAnnotation resourceParameter) {
		super(parent);
		this.resourceParameter = resourceParameter;
		this.name = resourceParameter.getName();
		this.value = resourceParameter.getValue();
	}
	
	// ********** synchronize/update **********
	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setName_(this.resourceParameter.getName());
		this.setValue_(this.resourceParameter.getValue());
	}
	
	// ********** name **********
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.resourceParameter.setName(name);
		this.setName_(name);
	}

	protected void setName_(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}

	// ********** value **********
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.resourceParameter.setValue(value);
		this.setValue_(value);
	}

	protected void setValue_(String value) {
		String old = this.value;
		this.value = value;
		this.firePropertyChanged(VALUE_PROPERTY, old, value);
	}

	// ********** validation **********
	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.resourceParameter.getTextRange(astRoot);
	}
	
	// ********** misc **********
	@Override
	public ParameterAnnotation getParameterAnnotation() {
		return resourceParameter;
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.name);
	}

}
