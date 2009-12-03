/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.viewers.xpl.MTreeViewer;

/**
 * @author Vitali Yemialyanchyk
 */
public class CloseConfigAction extends SelectionListenerAction {

	public static final String CLOSECONFIG_ACTIONID = "actionid.closeconfig"; //$NON-NLS-1$

	private StructuredViewer viewer;

	public CloseConfigAction(StructuredViewer sv) {
		super(HibernateConsoleMessages.CloseConfigAction_close_config);
		setEnabled(false);
		this.viewer = sv;
		setText(HibernateConsoleMessages.CloseConfigAction_close_config);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE));
		setId(CLOSECONFIG_ACTIONID);
	}

	public void run() {
		doCloseConfig();
	}

	protected void doCloseConfig() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			Object node = i.next();
			if (!(node instanceof ConsoleConfiguration)) {
				continue;
			}
			ConsoleConfiguration config = (ConsoleConfiguration) node;
			((MTreeViewer)viewer).clearChildren(null);
			config.reset();
			viewer.refresh(node);
		}
	}

	@Override
	public boolean isEnabled() {
		boolean res = false;
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			Object node = i.next();
			if (!(node instanceof ConsoleConfiguration)) {
				continue;
			}
			ConsoleConfiguration config = (ConsoleConfiguration) node;
			if (config.hasConfiguration()) {
				res = true;
			}
		}
		//return res;
		return true;
	}
}
