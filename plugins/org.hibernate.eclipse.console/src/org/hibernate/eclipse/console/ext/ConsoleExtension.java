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
package org.hibernate.eclipse.console.ext;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.console.ext.HibernateExtension;

/**
 * @author Dmitry Geraskov
 *
 */
public interface ConsoleExtension {
	
	public CompletionProposalsResult hqlCodeComplete(String query, int startPosition, int position);
	
	public void setHibernateException(HibernateExtension hibernateExtension);

	/**
	 * 
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 * @return the generated files separated by the contents
	 * @throws CoreException
	 */
	public Map<String, File[]> launchExporters(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException;
	
}
