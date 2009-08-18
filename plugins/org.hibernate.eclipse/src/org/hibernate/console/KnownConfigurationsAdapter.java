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

/**
 * This adapter class provides default implementations for the
 * methods described by the <code>KnownConfigurationsListener</code> interface.
 * <p>
 * Classes that wish to deal with <code>KnownConfigurations</code> events can
 * extend this class and override only the methods which they are
 * interested in.
 * </p>
 *
 * @see KnownConfigurationsListener
 * @see KnownConfigurations
 * 
 * @author Dmitry Geraskov
 */

public abstract class KnownConfigurationsAdapter implements KnownConfigurationsListener {

	public void configurationAdded(ConsoleConfiguration root) {}

	public void configurationBuilt(ConsoleConfiguration ccfg) {}

	public void configurationRemoved(ConsoleConfiguration root,
			boolean forUpdate) {}

	public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
			SessionFactory builtFactory) {}

	public void sessionFactoryClosing(ConsoleConfiguration configuration,
			SessionFactory closingFactory) {}

}
