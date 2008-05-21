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
package org.hibernate.console.execution;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.NDC;
import org.hibernate.eclipse.logging.CurrentContext;

public class DefaultExecutionContext implements ExecutionContext {

	final private URLClassLoader configurationClassLoader;	
	private volatile int installs;
	private Map previousLoaders = new WeakHashMap();

	final String key;
	
	public DefaultExecutionContext(String key, URLClassLoader loader) {
		configurationClassLoader = loader;
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.console.IExecutionContext#installLoader()
	 */
	public void installLoader() {
		installs++;
		if(configurationClassLoader!=null && Thread.currentThread().getContextClassLoader() != configurationClassLoader) {
			previousLoaders.put(Thread.currentThread(), Thread.currentThread().getContextClassLoader() );
			Thread.currentThread().setContextClassLoader(configurationClassLoader);
		}		
		
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.console.IExecutionContext#execute(org.hibernate.console.ExecutionContext.Command)
	 */
	public Object execute(Command c) {
		try {
			CurrentContext.push( key );
			installLoader();
			return c.execute();
		} 
		finally {
			uninstallLoader();
			CurrentContext.pop();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.console.IExecutionContext#uninstallLoader()
	 */
	public void uninstallLoader() {
		installs--; // TODO: make more safe (synchronized) bookkeeping of the classloader installation.
		
		if(installs==0) {
			ClassLoader cl = (ClassLoader) previousLoaders.get(Thread.currentThread() );
			if(configurationClassLoader!=null && Thread.currentThread().getContextClassLoader() != configurationClassLoader) {
				throw new IllegalStateException("Existing classloader is not equal to the previously installed classloader! Existing=" + Thread.currentThread().getContextClassLoader() + " Previous=" + configurationClassLoader);
			}
			
			if(cl!=null) {
				previousLoaders.remove(Thread.currentThread() );
				Thread.currentThread().setContextClassLoader(cl);
			}		
		}				
	}
	
}
