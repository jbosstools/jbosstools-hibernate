/**
 * 
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurations.IConsoleConfigurationListener;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;

class RealKnownConfigurationsProvider extends DeferredContentProvider implements IConsoleConfigurationListener {

	private TreeViewer tv;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		super.inputChanged(v, oldInput, newInput);
		tv = (TreeViewer) v;
		KnownConfigurations old = (KnownConfigurations) oldInput;
		if(old!=null) {
			old.removeConfigurationListener(this);
		}
		KnownConfigurations newz = (KnownConfigurations) newInput;
		if(newz!=null) {
			newz.addConsoleConfigurationListener(this);
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
}