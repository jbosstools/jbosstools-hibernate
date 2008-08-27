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
package org.jboss.tools.hibernate.jpt.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleConfigurationFacetModel {
	
	private String projectName;
	
	private String consoleConfigurationName;
	
	private String connectionProfileName;
	
	public static class Factory implements IActionConfigFactory {
		public Object create() throws CoreException {
			return new ConsoleConfigurationFacetModel();
		}		
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		if (projectName != null) projectName = projectName.trim();
		this.projectName = projectName;
	}

	/**
	 * @return the consoleConfigurationName
	 */
	public String getConsoleConfigurationName() {
		return consoleConfigurationName;
	}

	/**
	 * @param consoleConfigurationName the consoleConfigurationName to set
	 */
	public void setConsoleConfigurationName(String ccName) {
		this.consoleConfigurationName = ccName;
	}

	/**
	 * @return the connectionProfileName
	 */
	public String getConnectionProfileName() {
		return connectionProfileName;
	}

	/**
	 * @param connectionProfileName the connectionProfileName to set
	 */
	public void setConnectionProfileName(String cpName) {
		this.connectionProfileName = cpName;
	}
	

}
