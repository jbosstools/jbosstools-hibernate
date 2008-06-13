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
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.core.JpaFactory;
import org.eclipse.jpt.core.internal.platform.GenericJpaPlatform;

/**
 * @author Dmitry Geraskov
 *
 */

public class HibernatePlatform extends GenericJpaPlatform {
	
	public static String ID = "hibernate";

	@Override
	public String getId() {
		return ID;
	}

	/* use GenericJpaPlatform's methods while under progress
	@Override
	protected JpaFactory buildJpaFactory() {
		return new HibernateFactory();
	}*/
}
