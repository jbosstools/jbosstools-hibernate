package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.NodeFactory;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class LazySessionFactoryAdapter extends BasicWorkbenchAdapter {
	
	
	
	public Object[] getChildren(Object o) {
		LazySessionFactory lazySessionFactory = getLazySessionFactory(o);
		String label = "Session Factory";
		if(lazySessionFactory.getCfgNode()==null) {			
			if(lazySessionFactory.getConsoleConfiguration().getSessionFactory()==null) { 
				try {
					lazySessionFactory.getConsoleConfiguration().buildSessionFactory();
				} catch(Throwable t) {
					label = "<Sessionfactory error: " + t.getMessage() + ">";
					HibernateConsolePlugin.getDefault().logErrorMessage("Problems while creating sessionfactory", t);
				}
			}
			if(lazySessionFactory.getConsoleConfiguration().isSessionFactoryCreated()) {
				NodeFactory fac = new NodeFactory(lazySessionFactory.getConsoleConfiguration());
				lazySessionFactory.setCfgNode( fac.createConfigurationEntitiesNode(label) );
			}							
		}
		if(lazySessionFactory.getCfgNode()!=null) {
			return toArray(lazySessionFactory.getCfgNode().children(),BaseNode.class);	
		} else {
			return new Object[] { label };
		}
	}

	private LazySessionFactory getLazySessionFactory(Object o) {
		return (LazySessionFactory)o;		
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor( ImageConstants.TYPES );
	}

	public String getLabel(Object o) {
		return "Session Factory";
	}

	public Object getParent(Object o) {		
		return getLazySessionFactory( o ).getConsoleConfiguration();
	}

}
