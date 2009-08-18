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
package org.hibernate.console;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * This adapter class provides default implementations for the
 * methods described by the <code>ConcoleConfigurationListener</code> interface.
 * <p>
 * Classes that wish to deal with <code>ConcoleConfiguration</code> events can
 * extend this class and override only the methods which they are
 * interested in.
 * </p>
 *
 * @see ConcoleConfigurationListener
 * @see ConcoleConfiguration
 * 
 * @author Dmitry Geraskov
 */
public abstract class ConcoleConfigurationAdapter implements
		ConsoleConfigurationListener {

	public void queryPageCreated(QueryPage qp) {}

	public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
			SessionFactory builtSessionFactory) {}

	public void sessionFactoryClosing(ConsoleConfiguration configuration,
			SessionFactory aboutToCloseFactory) {}

	public void configurationBuilt(ConsoleConfiguration ccfg){};
}
