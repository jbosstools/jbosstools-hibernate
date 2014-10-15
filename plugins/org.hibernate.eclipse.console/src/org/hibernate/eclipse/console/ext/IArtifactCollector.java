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
import java.util.Set;

/**
 * @author Dmitry Geraskov
 *
 */
public interface IArtifactCollector {
	
	public File[] getFiles(String type);
	
	public Set<?> getFileTypes();

}
