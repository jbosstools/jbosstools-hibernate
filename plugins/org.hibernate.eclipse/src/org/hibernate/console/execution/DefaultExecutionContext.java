package org.hibernate.console.execution;

import java.net.URLClassLoader;
import java.util.Map;
import java.util.WeakHashMap;

public class DefaultExecutionContext implements ExecutionContext {

	
	final private URLClassLoader configurationClassLoader;	
	private volatile int installs;
	private Map previousLoaders = new WeakHashMap();
	
	public DefaultExecutionContext(URLClassLoader loader) {
		configurationClassLoader = loader;
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
			installLoader();
			return c.execute();
		} 
		finally {
			uninstallLoader();
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
