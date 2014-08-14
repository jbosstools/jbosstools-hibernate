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
package org.hibernate.eclipse.console.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.hibernate.eclipse.console.common.ConsoleExtension;
import org.hibernate.eclipse.console.common.HibernateExtension;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleExtensionDefinition {
	
	public static final String CLASSNAME = "classname"; //$NON-NLS-1$
	
	public static final String HIBERNATE_VERSION = "version"; //$NON-NLS-1$
	
	private final String hibernateVersion;
	
	private IConfigurationElement element;
	
	public ConsoleExtensionDefinition(IConfigurationElement element) {
		this(element.getAttribute( CLASSNAME ),
			    element.getAttribute( HIBERNATE_VERSION ));
		this.element = element;
	}

	private ConsoleExtensionDefinition(String classname, String hibernateVersion) {
		this.hibernateVersion = hibernateVersion;
	}
	
	public ConsoleExtension createConsoleExtensionInstance(HibernateExtension hibernateExtension) {
		if (hibernateExtension != null) {
			try {
				ConsoleExtension consoleExtension = (ConsoleExtension) element
						.createExecutableExtension(CLASSNAME);
				consoleExtension.setHibernateExtention(hibernateExtension);
				return consoleExtension;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @return the hibernateVersion
	 */
	public String getHibernateVersion() {
		return hibernateVersion;
	}
}
