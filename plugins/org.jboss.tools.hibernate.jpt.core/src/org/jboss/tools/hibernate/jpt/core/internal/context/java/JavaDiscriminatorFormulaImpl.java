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
import org.eclipse.jpt.core.context.JpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.DiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class JavaDiscriminatorFormulaImpl extends AbstractJavaJpaContextNode implements JavaDiscriminatorFormula {

	protected String value;

	protected DiscriminatorFormulaAnnotation dfResource;
	
	public JavaDiscriminatorFormulaImpl(JpaContextNode parent) {
		super(parent);
	}
	
	public void initialize(DiscriminatorFormulaAnnotation dfResource) {
		this.dfResource = dfResource;
		this.value = dfResource.getValue();		
	}

	public void update(DiscriminatorFormulaAnnotation dfResource) {
		this.dfResource = dfResource;
		this.setValue(dfResource.getValue());		
	}
	
	//******************* value *********************
	public String getValue() {
		return this.value;
	}

	public void setValue(String newValue) {
		String oldValue = this.value;
		this.value = newValue;
		this.dfResource.setValue(newValue);
		firePropertyChanged(DiscriminatorFormula.VALUE_PROPERTY, oldValue, newValue);
	}

	
	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.dfResource.getTextRange(astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.value);
	}
}
