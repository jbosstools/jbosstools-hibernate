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

package org.hibernate.eclipse.console.properties;

import org.hibernate.eclipse.nature.HibernateNature;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePropertiesConstants {

	public static final String HIBERNATE_NATURE = HibernateNature.ID;
	
	public static final String NAMING_STRATEGY_ENABLED = "namingStrategy.enabled"; //$NON-NLS-1$

	public static final String DEFAULT_CONFIGURATION = "default.configuration"; //$NON-NLS-1$

	public static final String HIBERNATE3_ENABLED = "hibernate3.enabled"; //$NON-NLS-1$

	public static final String HIBERNATE_CONSOLE_NODE = "org.hibernate.eclipse.console"; //$NON-NLS-1$

	public static final String HIBERNATE_JPA_PLATFORM_ID = "hibernate"; //$NON-NLS-1$

}
