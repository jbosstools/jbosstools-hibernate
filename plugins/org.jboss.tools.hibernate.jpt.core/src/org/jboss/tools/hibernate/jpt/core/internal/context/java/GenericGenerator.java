/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ListIterator;

import org.eclipse.jpt.core.context.Generator;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GenericGenerator extends Generator {
	
	Integer DEFAULT_INITIAL_VALUE = Integer.valueOf(1);

	String getStrategy();
	
	void setStrategy(String value);
		String GENERIC_STRATEGY_PROPERTY = "genericStrategyProperty"; //$NON-NLS-1$
		
		
	//************************ parameters ***********************
	
	String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	
	/**
	 * Return a list iterator of the parameters.  This will not be null.
	 */
	<T extends Parameter> ListIterator<T> parameters();
	
	/**
	 * Return the number of parameters.
	 */
	int parametersSize();
	
	/**
	 * Add a parameter to the generator and return the object representing it.
	 */
	Parameter addParameter(int index);
	
	/**
	 * Remove the parameter from the generator.
	 */
	void removeParameter(int index);
	
	/**
	 * Remove the parameter at the index from the query.
	 */
	void removeParameter(Parameter queryParameter);
	
	/**
	 * Move the parameter from the source index to the target index.
	 */
	void moveParameter(int targetIndex, int sourceIndex);
	
}