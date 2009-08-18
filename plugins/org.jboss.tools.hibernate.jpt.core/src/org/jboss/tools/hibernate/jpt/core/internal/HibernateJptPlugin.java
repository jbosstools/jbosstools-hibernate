/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.Iterator;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.internal.JpaModelManager;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsAdapter;
import org.osgi.framework.BundleContext;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HibernateJptPlugin extends Plugin {
	
	public static final String ID = "org.jboss.tools.hibernate.jpt.core"; //$NON-NLS-1$
	
	private static HibernateJptPlugin inst = null;
	
    public static HibernateJptPlugin getDefault() {
    	if (inst == null) {
    		inst = new HibernateJptPlugin();
    	}
        return inst;
    }

	/**
	 * Log message
	 *
	 */
	private static void log(int severity, String message, Throwable e) {
		getDefault().getLog().log(new Status(severity, ID, message, e));
	}
	
	/**
	 * Short exception log
	 *
	 */
	public static void logException(Throwable e) {
		log(IStatus.ERROR, e.getMessage(),  e);
	}
	
	/**
	 * Short exception log
	 *
	 */
	public static void logException(String message, Throwable e) {
		log(IStatus.ERROR, message,  e);
	}
	
	/**
	 * Short error log call
	 *
	 */
	public static void logError(String message) {
		log(IStatus.ERROR, message, null);
	}
	
	/**
	 * Short warning log call
	 *
	 */
	public static void logWarning(String message) {
		log(IStatus.WARNING, message, null);
	}
	
	/**
	 * Short information log call
	 *
	 */
	public static void logInfo(String message) {
		log(IStatus.INFO, message, null);
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		KnownConfigurations.getInstance().addConsoleConfigurationListener(new KnownConfigurationsAdapter(){
			
			private void revalidateProjects(ConsoleConfiguration ccfg){
				//FIXME: call only Dali's validator
				try {
					Iterator<JpaProject> jpaProjects = JpaModelManager.instance().getJpaModel().jpaProjects();
					while (jpaProjects.hasNext()) {
						JpaProject jpaProject = (JpaProject) jpaProjects.next();
						if (jpaProject instanceof HibernateJpaProject) {
							String ccName = ((HibernateJpaProject)jpaProject).getDefaultConsoleConfigurationName();
							if (ccfg.getName().equals(ccName)){
								jpaProject.getJavaProject().getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
							}							
						}
						
						
					}
				} catch (CoreException e) {
					logException(e);
				}
			}
			
			@Override
			public void configurationBuilt(ConsoleConfiguration ccfg) {
				if (ccfg.getConfiguration() == null
						|| ccfg.getConfiguration().getNamingStrategy() == null){
					return;
				}
				revalidateProjects(ccfg);
			}
			
			@Override
			public void configurationRemoved(ConsoleConfiguration root,
					boolean forUpdate) {
				if(forUpdate || root.getConfiguration() == null
						|| root.getConfiguration().getNamingStrategy() == null) {
					return;
				}
				revalidateProjects(root);
			}
			
		});
	}
	
}
