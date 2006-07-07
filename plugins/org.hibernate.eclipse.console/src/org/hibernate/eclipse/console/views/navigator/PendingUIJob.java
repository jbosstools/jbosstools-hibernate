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
