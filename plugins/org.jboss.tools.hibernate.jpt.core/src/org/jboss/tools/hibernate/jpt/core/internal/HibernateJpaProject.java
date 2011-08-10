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

import java.util.List;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaProject;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProject extends AbstractJpaProject {

	private Boolean cachedNamingStrategyEnable;

	public HibernateJpaProject(JpaProject.Config config){
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
		if (this.cachedNamingStrategyEnable == null){
			IScopeContext scope = new ProjectScope(getProject());
			Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
			if(node!=null) {
				this.cachedNamingStrategyEnable = node.getBoolean(HibernatePropertiesConstants.NAMING_STRATEGY_ENABLED, true );
			} else {
				this.cachedNamingStrategyEnable = true;
			}
		}
		return this.cachedNamingStrategyEnable;
	}

	@Override
	protected void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		this.validateConsoleConfiguration(messages);
	}

	/**
	 * @param messages
	 */
	protected void validateConsoleConfiguration(List<IMessage> messages) {
		if (KnownConfigurations.getInstance().find(getDefaultConsoleConfigurationName()) == null){
			IMessage message = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
					Messages.CC_NOT_EXISTS, new String[]{getDefaultConsoleConfigurationName()}, getResource());
			messages.add(message);
		}
	}

}
