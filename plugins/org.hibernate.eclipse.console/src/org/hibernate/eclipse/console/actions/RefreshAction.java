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
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author max
 *
 */
public class RefreshAction extends SelectionListenerAction {

	public static final String REFRESH_ACTIONID = "actionid.refresh"; //$NON-NLS-1$

	private final StructuredViewer viewer;
	private String imageFilePath = "icons/images/refresh_run.gif"; //$NON-NLS-1$

	public RefreshAction(StructuredViewer viewer) {
		super(HibernateConsoleMessages.RefreshAction_refresh);
		this.viewer = viewer;
		setImageDescriptor(HibernateConsolePlugin.getImageDescriptor(imageFilePath  ));
		setId(REFRESH_ACTIONID);
	}

	public void run() {
		List selectedNonResources = getSelectedNonResources();

		Iterator iter = selectedNonResources.iterator();
		while (iter.hasNext() ) {
			Object element = iter.next();
			viewer.refresh(element);
		}
	}

}
