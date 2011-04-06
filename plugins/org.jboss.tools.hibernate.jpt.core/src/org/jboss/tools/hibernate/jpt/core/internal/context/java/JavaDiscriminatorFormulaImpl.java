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
import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.DiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class JavaDiscriminatorFormulaImpl extends AbstractJavaJpaContextNode implements JavaDiscriminatorFormula {

	protected String value;

	protected DiscriminatorFormulaAnnotation annotation;

	public JavaDiscriminatorFormulaImpl(JpaContextNode parent, DiscriminatorFormulaAnnotation annotation) {
		super(parent);
		this.annotation = annotation;
		this.value = annotation.getValue();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setValue_(annotation.getValue());
	}

	//******************* value *********************
	public String getValue() {
		return this.value;
	}
	
	public void setValue_(String newValue) {
		String oldValue = this.value;
		this.value = newValue;
		firePropertyChanged(DiscriminatorFormula.VALUE_PROPERTY, oldValue, newValue);
	}

	public void setValue(String newValue) {
		this.annotation.setValue(newValue);
		setValue_(newValue);
	}

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.annotation.getTextRange(astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.value);
	}

	@Override
	public DiscriminatorFormulaAnnotation getDiscriminatorFormulaAnnotation() {
		return annotation;
	}
}
