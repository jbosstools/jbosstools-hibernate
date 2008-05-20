/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core.hibernate;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IMappingConfiguration;


/**
 * @author alex
 *
 * Interface of hibernate configuration object model
 */
public interface IHibernateConfiguration extends IMappingConfiguration {
	IPropertySource2 getManagedConfiguration();
	IPropertySource2 getPoolConfiguration();
	// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
	IPropertySource2 getSpringConfiguration();
	// #added#
}
