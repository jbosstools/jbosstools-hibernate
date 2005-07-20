package org.hibernate.eclipse.console.workbench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.cfg.reveng.ProgressListener;

public class ProgressListenerMonitor implements ProgressListener {

	private final IProgressMonitor monitor;

	public ProgressListenerMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;		
	}

	public void startSubTask(String name) {
		monitor.subTask(name);
	}

}
