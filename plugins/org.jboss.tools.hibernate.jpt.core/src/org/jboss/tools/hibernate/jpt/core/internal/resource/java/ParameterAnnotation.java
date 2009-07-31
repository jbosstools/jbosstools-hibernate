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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * Corresponds to the JPA annotation
 * org.hibernate.annotations.Parameter
 */

public interface ParameterAnnotation extends Annotation {
	String ANNOTATION_NAME = Hibernate.GENERATOR_PARAMETER;

	/**
	 * Corresponds to the 'name' element of the ParameterAnnotation annotation.
	 * Return null if the element does not exist in the annotation
	 */
	String getName();
		String NAME_PROPERTY = Hibernate.GENERATOR_PARAMETER__NAME;
	
	/**
	 * Corresponds to the 'name' element of the ParameterAnnotation annotation.
	 * Setting to null will remove the element.
	 */
	void setName(String name);
		
	/**
	 * Return the {@link TextRange} for the 'name' element. If the element 
	 * does not exist return the {@link TextRange} for the ParameterAnnotation annotation.
	 */
	TextRange getNameTextRange(CompilationUnit astRoot);


	/**
	 * Corresponds to the 'value' element of the ParameterAnnotation annotation.
	 * Return null if the element does not exist in the annotation
	 */
	String getValue();
		String VALUE_PROPERTY = Hibernate.GENERATOR_PARAMETER__VALUE;
	
	/**
	 * Corresponds to the 'value' element of the ParameterAnnotation annotation.
	 * Setting to null will remove the element.
	 */
	void setValue(String value);

	/**
	 * Return the {@link TextRange} for the 'value' element. If the element 
	 * does not exist return the {@link TextRange} for the ParameterAnnotation annotation.
	 */
	TextRange getValueTextRange(CompilationUnit astRoot);

}
