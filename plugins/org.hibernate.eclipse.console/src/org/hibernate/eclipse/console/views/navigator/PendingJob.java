package org.hibernate.eclipse.console.views.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class PendingJob extends Job {

	private ITreeContentProvider contentProvider;
	private PendingNode placeHolder;
	private AbstractTreeViewer viewer;
	private Object parent;

	static public class NonConflictingRule implements ISchedulingRule {
		
		public static final NonConflictingRule INSTANCE = new NonConflictingRule();

		public boolean contains(ISchedulingRule rule) { 
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule) { 
			return rule == this;
		}

	}

	public PendingJob(AbstractTreeViewer viewer, PendingNode placeHolder, Object parent, ITreeContentProvider contentProvider) {
		super(placeHolder.getText());
		this.viewer = viewer;
		this.placeHolder = placeHolder;
		this.parent = parent;
		this.contentProvider = contentProvider;
		setRule(NonConflictingRule.INSTANCE);
	}

	protected IStatus run(IProgressMonitor monitor) { 

		PendingUIJob updateUIJob = new PendingUIJob(viewer, placeHolder);
		updateUIJob.schedule();
		
		List children = new ArrayList();
		Object[] rootObjects = (contentProvider != null) ? contentProvider.getChildren(parent) : null;
		if (rootObjects != null) {
			for (int x=0; x< rootObjects.length ; ++x) {
				children.add(rootObjects[x]);
			}
			
		}		
		updateUIJob.setComplete(true);
		new ClearPlaceHolderJob(viewer, placeHolder, parent, children.toArray()).schedule();
		return Status.OK_STATUS;
	}

}
