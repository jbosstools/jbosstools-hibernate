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
