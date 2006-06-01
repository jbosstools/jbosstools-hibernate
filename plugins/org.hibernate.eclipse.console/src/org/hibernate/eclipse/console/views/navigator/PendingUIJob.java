package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.progress.UIJob;

/**
 * Job that refreshs a pending node, triggering a new image/text to occur.
 * @author Max Rydahl Andersen
 *
 */
public class PendingUIJob extends UIJob {
	
	private static final long DELAY = 200;
	  
	private PendingNode placeHolder;
	private StructuredViewer viewer;

	private boolean complete; 
	 
	public PendingUIJob(StructuredViewer viewer, PendingNode placeHolder) {
		super(placeHolder.getText()); 
		this.viewer = viewer;
		this.placeHolder = placeHolder;
		setSystem(true);
		//setRule(NonConflictingRule.INSTANCE);
	}

	public IStatus runInUIThread(IProgressMonitor monitor) { 
		
		if(!complete) {
			viewer.refresh(placeHolder, true);			
			schedule(DELAY);		
		}
		return Status.OK_STATUS;
		
	}

	public void setComplete(boolean newComplete) {
		complete = newComplete;
	}
}
