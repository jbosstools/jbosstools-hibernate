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
package org.jboss.tools.hibernate.internal.core.hibernate.validation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.internal.ide.IMarkerImageProvider;
/*
 * Created on 29.03.2005
 *
 */

/**
 * @author Tau from Minsk
 *
 */
public class HibernateMappingProblemImageProvider implements IMarkerImageProvider {

	/**
	 * 
	 */
	public HibernateMappingProblemImageProvider() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.ide.IMarkerImageProvider#getImagePath(org.eclipse.core.resources.IMarker)
	 */
	public String getImagePath(IMarker marker) {
		if (isMarkerType(marker, IMarker.PROBLEM)) {
			switch (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING)) {
				case IMarker.SEVERITY_ERROR:
					return "icons/error_tsk.gif"; 
				case IMarker.SEVERITY_WARNING:
					return "icons/warn_tsk.gif";
				case IMarker.SEVERITY_INFO:
					return "icons/info_tsk.gif";
			}
		}
		return null; 
	}
	
	private boolean isMarkerType(IMarker marker, String type) {
		try {
			return marker.isSubtypeOf(type);
		} catch (CoreException e) {
        	//TODO (tau-tau) for Exception			
			return false;
		}
	} 	

}

