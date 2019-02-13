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
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.viewers.xpl.MTreeViewer;

/**
 * @author Vitali Yemialyanchyk
 */
public class CloseConfigAction extends ConsoleConfigReadyUseBaseAction {

	public static final String CLOSECONFIG_ACTIONID = "actionid.closeconfig"; //$NON-NLS-1$

	/**
	 * @param text
	 */
	protected CloseConfigAction(String text) {
		super(text);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE));
		setId(CLOSECONFIG_ACTIONID);
		init(null);
	}

	public CloseConfigAction(StructuredViewer sv) {
		super(HibernateConsoleMessages.CloseConfigAction_close_config);
		setText(HibernateConsoleMessages.CloseConfigAction_close_config);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE));
		setId(CLOSECONFIG_ACTIONID);
		init(sv);
	}

	public void doRun() {
		doCloseConfig();
	}

	protected void doCloseConfig() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			Object node = i.next();
			if (!(node instanceof ConsoleConfiguration)) {
				continue;
			}
			ConsoleConfiguration config = (ConsoleConfiguration) node;
			((MTreeViewer)viewer).clearChildren(config);
			config.reset();
			viewer.refresh(node);
		}
	}
}
