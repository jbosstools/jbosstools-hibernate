package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.ConfigurationEntitiesNode;
import org.hibernate.console.node.NodeFactory;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class LazySessionFactoryAdapter extends BasicWorkbenchAdapter {

	private ConfigurationEntitiesNode cfgNode;
	String label = "Session Factory";
	
	public Object[] getChildren(Object o) {
		if(cfgNode==null) {
			LazySessionFactory lazySessionFactory = getLazySessionFactory(o);
			if(lazySessionFactory.getConsoleConfiguration().getSessionFactory()==null) { 
			try {
				lazySessionFactory.getConsoleConfiguration().buildSessionFactory();
			} catch(Throwable t) {
				label = "<Sessionfactory error: " + t.getMessage() + ">";
				HibernateConsolePlugin.getDefault().logErrorMessage("Problems while creating sessionfactory", t);
			}
			}
			NodeFactory fac = new NodeFactory(lazySessionFactory.getConsoleConfiguration());
			cfgNode = fac.createConfigurationEntitiesNode(label);				
		}
		if(cfgNode!=null) {
			return toArray(cfgNode.children(),BaseNode.class);	
		} else {
			return new Object[0];
		}
	}

	private LazySessionFactory getLazySessionFactory(Object o) {
		return (LazySessionFactory)o;		
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor( ImageConstants.TYPES );
	}

	public String getLabel(Object o) {
		return "Session factory";
	}

	public Object getParent(Object o) {		
		return getLazySessionFactory( o ).getConsoleConfiguration();
	}

}
