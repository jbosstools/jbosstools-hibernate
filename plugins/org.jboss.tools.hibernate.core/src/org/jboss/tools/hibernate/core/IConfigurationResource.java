/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author alex
 *
 * Base interface of all configuration resources such as properties files and congiguration files.
 */
public interface IConfigurationResource {
	public IResource getResource();
	public void save() throws IOException, CoreException ;
	
	// add tau 14.02.2006 
	public void save(boolean flagSaveMappingStorages) throws IOException, CoreException ;
	
	public void reload() throws IOException, CoreException;
    //added by Nick 19.05.2005
    public boolean resourceChanged();
    //by Nick
}
