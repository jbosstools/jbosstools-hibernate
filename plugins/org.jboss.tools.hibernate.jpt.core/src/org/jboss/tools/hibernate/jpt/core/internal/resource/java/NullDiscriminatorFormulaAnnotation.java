/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.NullAnnotation;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.utility.TextRange;

/**
 * @author Dmitry Geraskov
 *
 */
public class NullDiscriminatorFormulaAnnotation extends NullAnnotation implements
		DiscriminatorFormulaAnnotation {
	
	public NullDiscriminatorFormulaAnnotation(JavaResourcePersistentType parent) {
		super(parent);
	}
	
	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}
	
	@Override
	protected DiscriminatorFormulaAnnotation buildSupportingAnnotation() {
		return (DiscriminatorFormulaAnnotation) super.buildSupportingAnnotation();
	}

	// ***** value
	public String getValue() {
		return null;
	}

	public void setValue(String value) {
		if (value != null) {
			this.buildSupportingAnnotation().setValue(value);
		}
	}

	public TextRange getValueTextRange(CompilationUnit astRoot) {
		return null;
	}

}
