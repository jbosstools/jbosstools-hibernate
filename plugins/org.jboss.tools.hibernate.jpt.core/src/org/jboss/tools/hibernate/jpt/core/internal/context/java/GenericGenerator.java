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

import org.eclipse.jpt.core.context.Generator;

public interface GenericGenerator extends Generator {
	
	Integer DEFAULT_INITIAL_VALUE = Integer.valueOf(1);

	String getStrategy();
	
	void setSpecifiedStrategy(String value);
		String GENERIC_STRATEGY_PROPERTY = "genericStrategyProperty"; //$NON-NLS-1$

}