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

import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.resource.java.GeneratorAnnotation;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GenericGeneratorAnnotation extends 
	NestableAnnotation, GeneratorAnnotation {
	
	String ANNOTATION_NAME = Hibernate.GENERIC_GENERATOR;
	
	/**
	 * Corresponds to the strategy element of the GenericGenerator annotation.
	 * Returns null if the strategy element does not exist in java.
	 */
	String getStrategy();
	
	/**
	 * Corresponds to the strategy element of the GenericGenerator annotation.
	 * Set to null to remove the strategy element.
	 */
	void setStrategy(String strategy);
	String STRATEGY_PROPERTY = "strategyProperty";	 //$NON-NLS-1$
	
	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 * Return an empty iterator if the element does not exist in Java.
	 */
	ListIterator<ParameterAnnotation> parameters();
		String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	
	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	int parametersSize();

	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	ParameterAnnotation parameterAt(int index);
	
	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	int indexOfParameter(ParameterAnnotation parameter);
	
	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	ParameterAnnotation addParameter(int index);
	
	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	void moveParameter(int targetIndex, int sourceIndex);

	/**
	 * Corresponds to the 'parameters' element of the *Generic Generator annotation.
	 */
	void removeParameter(int index);	

	/**
	 * Return the {@link TextRange} for the strategy element.  If the strategy element 
	 * does not exist return the {@link TextRange} for the GenericGenerator annotation.
	 */
	TextRange getStrategyTextRange(CompilationUnit astRoot);

}
