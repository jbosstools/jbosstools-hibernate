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
package org.jboss.tools.hibernate.view.views;


import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;


/**
 * @author Konstantin Mishin
 * 
 */

public class HBMViewerFilter extends ViewerFilter {
	private IMappingStorage fSelectedMappingStorages[];
	private HashSet<String> fSrc;
	private HashSet<IResource> fFiles;
	
	public HBMViewerFilter(IMappingStorage m[]) {
		fSelectedMappingStorages = m;
		fFiles = new HashSet<IResource>();
		fSrc = new HashSet<String>();
		traverse(ResourcesPlugin.getWorkspace().getRoot());
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// edit tau 14.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?
		//return fFiles.contains(element);
		boolean result = fFiles.contains(element);
		if (result && element instanceof IResource) {
				ResourceAttributes attributesResource = ((IResource)element).getResourceAttributes();
				if ((attributesResource != null) && attributesResource.isReadOnly()) {
					result = false;
				}			
		}
		return result;
	}

	private boolean traverse(IContainer container) {
		boolean added = false;
		if (container.exists() && container instanceof IProject) {
			try {
				// #added# by Konstantin Mishin on 19.09.2005 fixed for ESORM-109
				if(((IProject)container).hasNature(JavaCore.NATURE_ID)) {
					// #added#
					IPackageFragmentRoot pfr[] = JavaCore.create((IProject)container).getPackageFragmentRoots();
					for (int i = 0; i < pfr.length; i++)
						fSrc.add(pfr[i].getPath().toString());
				}
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e, null);
			}

		}
		try {
			IResource[] resources = container.members();
			for (int i = 0; i < resources.length; i++) {
				IResource resource = resources[i];
				if (resource instanceof IFile) {
					String ext = resource.getName();
					if ((ext != null && (ext.toLowerCase().endsWith(".hbm") || ext.toLowerCase().endsWith(".hbm.xml"))) && isSelectedMappingStorage(resource) && isSrc(resource.getFullPath().toString())) {
						fFiles.add(resource);
						added = true;
					}
				} else if (resource.isAccessible() && resource instanceof IContainer) {
					if (traverse((IContainer)resource)) {
						fFiles.add(resource);	
						added = true;
					}
				}
			}
		} catch (CoreException e) {
			ExceptionHandler.logThrowableError(e, null);
		}
		return added;
	}
	
	public boolean isSelectedMappingStorage(Object element) {
		for (int i = 0; i < fSelectedMappingStorages.length; i++) 
			if (fSelectedMappingStorages[i].getResource().equals(element)) 
				return false;			
		return true;	
	}
	
	public boolean isSrc(String str) {
		Iterator<String> i = fSrc.iterator();
		while (i.hasNext()) {
			if (str.startsWith(i.next()))
				return true;			
		}
		return false;	
	}

}
