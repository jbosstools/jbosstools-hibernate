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
package org.hibernate.eclipse.console.views.navigator;

import java.util.ArrayList;
import java.util.List;

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
		
		List<Object> children = new ArrayList<Object>();
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
