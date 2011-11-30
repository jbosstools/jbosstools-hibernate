/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.libs;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Dmitry Geraskov {geraskov@gmail.com}
 *
 */
public class HibernateLibsPlugin extends Plugin {
	
	private static Plugin plugin;
	
	public HibernateLibsPlugin(){
		plugin = this;
	}
	
	public static Plugin getDefault(){
		return plugin;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
		plugin = null;
	}

}
