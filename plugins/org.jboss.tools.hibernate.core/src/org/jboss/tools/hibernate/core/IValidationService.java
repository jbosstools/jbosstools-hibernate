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

import org.eclipse.core.resources.IResource;

/**
 * @author alex
 *
 * Interface of mapping validation service
 */
public interface IValidationService {
	/**
	 * Start validation process for mapping
	 * */
	public void run();
    
    // added by Nick 22.09.2005
	public void incrementalRun(IResource resource);
    // by Nick
    
}
