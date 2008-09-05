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
package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
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
			o1 = HibernateConsoleMessages.ConsoleConfigurationWorkbenchAdapter_empty_configuration;
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

	protected String getDefaultErrorMessage(Object object) {
		return NLS.bind(HibernateConsoleMessages.BasicWorkbenchAdapter_error_while_opening_configuration, getLabel(object));
	}
}
