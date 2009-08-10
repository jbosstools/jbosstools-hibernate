/*******************************************************************************
 * Copyright (c) 2007-2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * @author some modifications from Vitali
 */
public class AutoLayoutAction extends DiagramBaseAction {

	public static final String ACTION_ID = "auto_layout_id"; //$NON-NLS-1$
	private static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/autolayout.png"); //$NON-NLS-1$

	public AutoLayoutAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.AutoLayoutAction_auto_layout);
		setToolTipText(DiagramViewerMessages.AutoLayoutAction_auto_layout);
		setImageDescriptor(img);
	}

	public void run() {
		getDiagramViewer().getViewerContents().autolayout();
	}
}
