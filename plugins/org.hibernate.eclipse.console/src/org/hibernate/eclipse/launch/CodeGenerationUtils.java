/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * Utility class for code generation.
 */
public class CodeGenerationUtils {
	
	public static void refreshOutputDir(String outputdir) {
		IResource bufferRes = PathHelper.findMember(ResourcesPlugin.getWorkspace().getRoot(),
				outputdir);

		if (bufferRes != null && bufferRes.isAccessible()) {
			try {
				bufferRes.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				// ignore, maybe merge into possible existing status.
			}
		}
	}
}
