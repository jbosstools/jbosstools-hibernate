package org.hibernate.eclipse.console.workbench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.ConfigurationEntitiesNode;
import org.hibernate.console.node.NodeFactory;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ConsoleConfigurationWorkbenchAdapter implements
		IDeferredWorkbenchAdapter {

	public void fetchDeferredChildren(Object object,
			IElementCollector collector, IProgressMonitor monitor) {
		collector.add(getChildren(object), monitor);
	}

	public boolean isContainer() {
		return true;
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}

	public Object[] getChildren(Object o) {
		ConsoleConfiguration ccfg = (ConsoleConfiguration) o;
		String sfError = null;
		if(ccfg.getConfiguration()==null) {
			ccfg.build();
			try {
				ccfg.initSessionFactory();
			} catch(Throwable t) {
				sfError = "<Sessionfactory error: " + t.getMessage() + ">";
				HibernateConsolePlugin.getDefault().logErrorMessage("Problems while creating sessionfactory", t);
			}
		}
		
		Configuration configuration = ccfg.getConfiguration();
		Object o1 = (configuration==null?"<Empty Configuration>":(Object)configuration);
		
		Object o2;
		if(sfError==null) {
			NodeFactory fac = new NodeFactory(ccfg);
			ConfigurationEntitiesNode cfgNode = fac.createConfigurationEntitiesNode();
			o2 = cfgNode;
		} else {
			o2 = sfError;
		}
		
		return new Object[] { o1, o2 };
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.CONFIGURATION);
	}

	public String getLabel(Object o) {
		ConsoleConfiguration cfg = (ConsoleConfiguration) o;
		return cfg.getName();
	}

	public Object getParent(Object o) {
		return KnownConfigurations.getInstance();
	}

}
