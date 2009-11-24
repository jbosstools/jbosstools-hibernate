/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;

@SuppressWarnings("restriction")
public class KnownConfigurationsProvider extends DeferredContentProvider implements KnownConfigurationsListener,
	IResourceChangeListener, IPropertyChangeListener {

	private TreeViewer tv;

	public KnownConfigurationsProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
		
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		super.inputChanged(v, oldInput, newInput);
		tv = (TreeViewer) v;
		if(oldInput!=null && oldInput instanceof KnownConfigurations) {
			KnownConfigurations old = (KnownConfigurations) oldInput;
			if(old!=null) {
				old.removeConfigurationListener(this);
			}
		}
		if(newInput!=null && newInput instanceof KnownConfigurations) {
			KnownConfigurations newz = (KnownConfigurations) newInput;
			if(newz!=null) {
				newz.addConsoleConfigurationListener(this);
			}
		}
	}
	
	public void dispose() {
	}
	
	public void configurationAdded(ConsoleConfiguration root) {
		refreshTree();
	}

	private void refreshTree() {
		Runnable runnable = new Runnable() {
			public void run() {
				tv.refresh();
			}
		};
		tv.getControl().getDisplay().syncExec(runnable);
	}

	public void configurationRemoved(ConsoleConfiguration root, boolean forUpdate) {
		if (forUpdate) {
			refreshTree();
		}
	}
	
	protected IWorkbenchAdapter getAdapter(Object o) {
	    if (o instanceof IAdaptable) {
        	return (IWorkbenchAdapter) ((IAdaptable) o).getAdapter(IWorkbenchAdapter.class);
        } else {
        	return (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(o, IWorkbenchAdapter.class);
        }
	}

	public void sessionFactoryBuilt(final ConsoleConfiguration ccfg, SessionFactory builtFactory) {
		/*(Display.getDefault().syncExec(new Runnable() { Disabled as it will generate double entries in the child list
			public void run() {
				tv.refresh(ccfg);
			} 
		});	*/			
	}

	public void sessionFactoryClosing(final ConsoleConfiguration configuration, SessionFactory closingFactory) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				tv.collapseToLevel(configuration, AbstractTreeViewer.ALL_LEVELS);
				tv.refresh(configuration);
			}
		});				
	}

	public void configurationBuilt(ConsoleConfiguration ccfg) {
		//TODO refresh tree?
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			try {
				delta.accept(new KnownConfigurationsVisitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	class KnownConfigurationsVisitor implements IResourceDeltaVisitor {
		
        /**
		 * @see IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) {
			if (delta == null) {
				return false;
			}
			if (0 != (delta.getFlags() & IResourceDelta.OPEN)) {
				if (delta.getResource() instanceof IProject) {
					IProject project = (IProject)delta.getResource();
					if (project.isOpen()) {						
						try {
							ILaunchConfiguration[] configs = LaunchHelper.findProjectRelatedHibernateLaunchConfigs(project.getName());
							if (configs.length > 0) refreshTree();
						} catch (CoreException e) {
							e.printStackTrace();
							refreshTree();
						}
					}
				}
				return false;
			}
			IResource resource = delta.getResource();
			if (resource instanceof IProject) {
				IProject project = (IProject)resource;
				switch (delta.getKind()) {					
					case IResourceDelta.ADDED:
					case IResourceDelta.REMOVED :
						try {
							ILaunchConfiguration[] configs = LaunchHelper.findProjectRelatedHibernateLaunchConfigs(project.getName());
							if (configs.length > 0) refreshTree();
						} catch (CoreException e) {
							e.printStackTrace();
							refreshTree();
						}
				}
				return false;
			}
			return true;
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(IInternalDebugUIConstants.PREF_FILTER_LAUNCH_CLOSED) ||
				event.getProperty().equals(IInternalDebugUIConstants.PREF_FILTER_LAUNCH_DELETED)) {
			refreshTree();
		}		
	}
	

}