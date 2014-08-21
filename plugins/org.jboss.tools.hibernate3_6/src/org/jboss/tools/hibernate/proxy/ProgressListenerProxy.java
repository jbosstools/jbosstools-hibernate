package org.jboss.tools.hibernate.proxy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.cfg.reveng.ProgressListener;
import org.jboss.tools.hibernate.spi.IProgressListener;

public class ProgressListenerProxy implements IProgressListener {

	private ProgressListener target = new ProgressListener() {
		@Override
		public void startSubTask(String name) {
			monitor.subTask(name);
		}		
	};
	
	private IProgressMonitor monitor = null;
	
	public ProgressListenerProxy(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	ProgressListener getTarget() {
		return target ;
	}

	@Override
	public void startSubTask(String name) {
		target.startSubTask(name);
	}
}
