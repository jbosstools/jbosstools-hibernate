/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.jpa.core.context.Generator;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GenericGenerator extends Generator, ParametrizedElement {
	
	Integer DEFAULT_INITIAL_VALUE = Integer.valueOf(1);

	String getStrategy();
	
	void setStrategy(String value);
		String GENERIC_STRATEGY_PROPERTY = "genericStrategyProperty"; //$NON-NLS-1$

	
}
