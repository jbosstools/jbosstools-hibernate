package org.hibernate.eclipse.console.workbench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.hibernate.console.KnownConfigurations;

public class KnownConfigurationsWorkbenchAdapter implements IDeferredWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		KnownConfigurations kc = (KnownConfigurations) o;
		return kc.getConfigurations();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		return "Configurations";
	}

	public Object getParent(Object o) {
		return null;
	}

	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		collector.add(getChildren(object),monitor);
	}

	public boolean isContainer() {
		return true;
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}

}
