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
package org.hibernate.eclipse.console.actions;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfigurationListener;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.console.QueryPage;

/**
 * @author Vitali Yemialyanchyk
 */
public abstract class ConsoleConfigReadyUseBaseAction extends ConsoleConfigurationBasedAction implements ConsoleConfigurationListener, KnownConfigurationsListener {

	protected StructuredViewer viewer;
	
	private HashSet<ConsoleConfiguration> vecMonitorConsoleConfigs = new HashSet<ConsoleConfiguration>();

	protected ConsoleConfigReadyUseBaseAction(String text) {
		super(text);
	}

	public void init(StructuredViewer viewer) {
		this.viewer = viewer;
		KnownConfigurations.getInstance().addConsoleConfigurationListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<ConsoleConfiguration> cloneMonitorConsoleConfigs() {
		return (HashSet<ConsoleConfiguration>)vecMonitorConsoleConfigs.clone();
	}
	
	@SuppressWarnings("rawtypes")
	protected void clearCache() {
		final IStructuredSelection selection = getStructuredSelection();
		HashSet<ConsoleConfiguration> vecMonitorConsoleConfigsTmp = 
			cloneMonitorConsoleConfigs();
        for (ConsoleConfiguration consoleConfig : vecMonitorConsoleConfigsTmp) {
        	consoleConfig.removeConsoleConfigurationListener(this);
        }
        vecMonitorConsoleConfigsTmp.clear();
        for (Iterator i = selection.iterator(); i.hasNext(); ) {
        	Object obj = i.next();
        	if (obj instanceof ConsoleConfiguration) {
        		ConsoleConfiguration consoleConfig = (ConsoleConfiguration)obj;
        		vecMonitorConsoleConfigsTmp.add(consoleConfig);
            	consoleConfig.addConsoleConfigurationListener(this);
        	}
        }
        vecMonitorConsoleConfigs = vecMonitorConsoleConfigsTmp;
        super.clearCache();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.eclipse.console.actions.SessionFactoryBasedAction#updateState(org.hibernate.console.ConsoleConfiguration)
	 */
	protected boolean updateState(ConsoleConfiguration ccfg) {
		return ccfg.hasConfiguration();
	}

	public void updateEnableState() {
		selectionChanged(getStructuredSelection());
	}

	public void queryPageCreated(QueryPage qp) {
	}

	public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtSessionFactory) {
	}

	public void sessionFactoryClosing(ConsoleConfiguration ccfg, SessionFactory aboutToCloseFactory) {
	}

	public void configurationBuilt(ConsoleConfiguration ccfg) {
		updateEnableState();
	}

	public void configurationReset(ConsoleConfiguration ccfg) {
		updateEnableState();
	}

	public void configurationAdded(ConsoleConfiguration root) {
	}

	public void configurationRemoved(ConsoleConfiguration ccfg, boolean forUpdate) {
		if (vecMonitorConsoleConfigs.remove(ccfg)) {
			ccfg.removeConsoleConfigurationListener(this);
		}
	}
}
