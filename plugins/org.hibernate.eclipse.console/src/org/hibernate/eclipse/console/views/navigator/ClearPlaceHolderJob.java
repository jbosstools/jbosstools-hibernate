/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:/*
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
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.progress.UIJob;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.views.navigator.PendingJob.NonConflictingRule;

public class ClearPlaceHolderJob extends UIJob {

	private AbstractTreeViewer viewer;
	private PendingNode placeHolder;
	private Object[] children;
	private Object parent;

	public ClearPlaceHolderJob(AbstractTreeViewer viewer, PendingNode placeHolder, Object parent, Object[] children) {
		super(HibernateConsoleMessages.ClearPlaceHolderJob_removing_place_holder);
		this.viewer = viewer;
		this.placeHolder = placeHolder;
		this.parent = parent;
		this.children = children;
		setRule(NonConflictingRule.INSTANCE);
	}

	public IStatus runInUIThread(IProgressMonitor monitor) {
		viewer.remove(placeHolder);
		viewer.add(parent, children);
		//viewer.update( children, null );
		return Status.OK_STATUS;
	}

}
