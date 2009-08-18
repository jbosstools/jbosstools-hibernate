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

package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.internal.AbstractJpaProject;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateJpaProject extends AbstractJpaProject {
	

	public HibernateJpaProject(JpaProject.Config config) throws CoreException {
		super(config);
	}
	
	public NamingStrategy getNamingStrategy(){
		String ccName = getDefaultConsoleConfigurationName();
		if (ccName != null || "".equals(ccName)){//$NON-NLS-1$
			ConsoleConfiguration cc = KnownConfigurations.getInstance().find(ccName);
			if (cc != null){
				if (cc.getConfiguration() != null){					
					Configuration config = cc.getConfiguration();
					return config.getNamingStrategy();					
				}
			}
		}
		return null;
	}
	
	public String getDefaultConsoleConfigurationName(){
		IScopeContext scope = new ProjectScope(getProject());
		Preferences node = scope.getNode("org.hibernate.eclipse.console"); //$NON-NLS-1$
		if(node!=null) {
			return node.get("default.configuration", getName() ); //$NON-NLS-1$
		}
		return null;
	}
	
}
