/**
 * 
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

class KnownConfigurationsProvider extends DeferredContentProvider implements KnownConfigurationsListener {

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

	public void configurationRemoved(ConsoleConfiguration root) {
		refreshTree();		
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
}