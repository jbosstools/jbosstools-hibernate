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
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaParameter extends AbstractJavaJpaContextNode implements JavaParameter {

	protected String name;

	protected String value;

	protected ParameterAnnotation resourceParameter;
	
	public HibernateJavaParameter(JavaGenericGenerator parent) {
		super(parent);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		String oldName = this.name;
		this.name = newName;
		this.resourceParameter.setName(newName);
		firePropertyChanged(Parameter.NAME_PROPERTY, oldName, newName);
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String newValue) {
		String oldValue = this.value;
		this.value = newValue;
		this.resourceParameter.setValue(newValue);
		firePropertyChanged(Parameter.VALUE_PROPERTY, oldValue, newValue);
	}

	public void initialize(ParameterAnnotation resourceParameter) {
		this.resourceParameter = resourceParameter;
		this.name = resourceParameter.getName();
		this.value = resourceParameter.getValue();
	}
	
	public void update(ParameterAnnotation resourceParameter) {
		this.resourceParameter = resourceParameter;
		this.setName(resourceParameter.getName());
		this.setValue(resourceParameter.getValue());
	}
	
	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.resourceParameter.getTextRange(astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.name);
	}

}
