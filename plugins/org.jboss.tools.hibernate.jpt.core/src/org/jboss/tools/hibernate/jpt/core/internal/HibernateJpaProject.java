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
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProject extends AbstractJpaProject {
	
	private Boolean cachedNamingStrategyEnable;
	

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
		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
		if(node!=null) {
			return node.get(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, getName() );
		}
		return null;
	}
	
	public boolean isNamingStrategyEnabled(){
		// as this flag cannot be changed without cleaning up and
		// rebuilding ( == creating new instance) of jpa project we cache it
		if (cachedNamingStrategyEnable == null){
			IScopeContext scope = new ProjectScope(getProject());
			Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
			if(node!=null) {
				cachedNamingStrategyEnable = node.getBoolean(HibernatePropertiesConstants.NAMING_STRATEGY_ENABLED, true );
			} else {
				cachedNamingStrategyEnable = true;
			}			
		}
		return cachedNamingStrategyEnable;
	}

}
