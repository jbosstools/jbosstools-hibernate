/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
public class BuildSessionFactoryAction extends ConsoleConfigurationBasedAction {

	private final StructuredViewer viewer;

	public BuildSessionFactoryAction(StructuredViewer viewer) {
		super("Build SessionFactory");
		this.viewer = viewer;
		setEnabledWhenNoSessionFactory(true);
	}
	
	protected void doRun() {
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
        	try {
            Object node = i.next();
            if(node instanceof ConsoleConfiguration) {
            	ConsoleConfiguration config = (ConsoleConfiguration) node;
            	if(config.isSessionFactoryCreated() ) {
            		config.reset();            		
            	} else {
            		config.build();
            		config.buildSessionFactory();
            	}
            	updateState(config);
            }
			                     
        	} catch(HibernateConsoleRuntimeException he) {
        		 HibernateConsolePlugin.getDefault().showError(viewer.getControl().getShell(), "Exception while connecting/starting Hibernate",he);
        	} catch(UnsupportedClassVersionError ucve) {
				 HibernateConsolePlugin.getDefault().showError(viewer.getControl().getShell(), "Starting Hibernate resulted in a UnsupportedClassVersionError.\nThis can occur if you are running eclipse with JDK 1.4 and your domain classes require JDK 1.5. \n\nResolution: Run eclipse with JDK 1.5.",ucve);
        	}
        }
	}

	/**
	 * @param config
	 */
	protected boolean updateState(ConsoleConfiguration config) {
		setEnabledWhenNoSessionFactory(!config.isSessionFactoryCreated() );
		if(enabledWhenNoSessionFactory) {
			setText("Create SessionFactory");
		} else {
			setText("Close SessionFactory");
		}
		return super.updateState(config);
	}
}
