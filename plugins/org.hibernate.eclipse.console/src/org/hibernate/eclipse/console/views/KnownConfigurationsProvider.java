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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;

public class KnownConfigurationsProvider extends DeferredContentProvider implements KnownConfigurationsListener {

	private TreeViewer tv;

	public KnownConfigurationsProvider() {
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
}