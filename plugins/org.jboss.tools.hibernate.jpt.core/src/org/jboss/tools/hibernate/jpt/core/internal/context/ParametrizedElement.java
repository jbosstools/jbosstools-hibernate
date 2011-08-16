/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.common.utility.internal.iterables.ListIterable;
import org.eclipse.jpt.common.utility.model.Model;

/**
 * @author Dmitry Geraskov
 *
 */
public interface ParametrizedElement extends Model {
	
	//************************ parameters ***********************
	
	String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	
	/**
	 * Return a list iterator of the parameters.  This will not be null.
	 */
	<T extends Parameter> ListIterable<T> getParameters();
	
	/**
	 * Return the number of parameters.
	 */
	int getParametersSize();
	
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
