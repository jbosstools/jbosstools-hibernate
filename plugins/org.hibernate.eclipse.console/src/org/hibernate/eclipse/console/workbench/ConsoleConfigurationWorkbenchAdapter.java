package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ConsoleConfigurationWorkbenchAdapter extends BasicWorkbenchAdapter {
	
	public Object[] getChildren(Object o) {
		final ConsoleConfiguration ccfg = getConsoleConfiguration( o );
		//String sfError = null;
		if(ccfg.getConfiguration()==null) {
			ccfg.build();
			ccfg.execute( new ExecutionContext.Command() {
			
				public Object execute() {
					if(ccfg.hasConfiguration()) {
						ccfg.getConfiguration().buildMappings();
					}
					return ccfg;
				}			
			} );
		} 
		
		/*if(ccfg.getSessionFactory()==null) { // initialize later?
			try {
				ccfg.buildSessionFactory();
			} catch(Throwable t) {
				sfError = "<Sessionfactory error: " + t.getMessage() + ">";
				HibernateConsolePlugin.getDefault().logErrorMessage("Problems while creating sessionfactory", t);
			}
		}*/
		
		
		Configuration configuration = ccfg.getConfiguration();
		Object o1;
		if(configuration!=null) {
			o1 = configuration;
		} else {
			o1 = "<Empty Configuration>";
		}
		
		/*Object o2;
		
		if(sfError==null) {
			NodeFactory fac = new NodeFactory(ccfg);
			ConfigurationEntitiesNode cfgNode = fac.createConfigurationEntitiesNode("Session factory");
			o2 = cfgNode;			
		} else {
			o2 = sfError;			
		}*/
		
		return new Object[] { o1, new LazySessionFactory(ccfg), new LazyDatabaseSchema(ccfg) };
	}

	private ConsoleConfiguration getConsoleConfiguration(Object o) {
		return (ConsoleConfiguration) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.CONFIGURATION);
	}

	public String getLabel(Object o) {
		ConsoleConfiguration cfg = getConsoleConfiguration( o );
		return cfg.getName();
	}

	public Object getParent(Object o) {
		return KnownConfigurations.getInstance();
	}

}
