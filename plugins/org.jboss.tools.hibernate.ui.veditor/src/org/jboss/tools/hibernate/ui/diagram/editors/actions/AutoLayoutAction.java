/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import org.eclipse.jface.action.Action;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;

public class AutoLayoutAction extends Action {

	public static final String ACTION_ID = "Auto layout"; //$NON-NLS-1$

	private VisualEditor editor;

	public AutoLayoutAction(VisualEditor editor) {
		this.editor = editor;
		setId(ACTION_ID);
		setText(ACTION_ID);
		//setImageDescriptor(ImageDescriptor.createFromFile(
		//		VisualEditor.class, "icons/export.png"));
	}

	public void run() {
		editor.getViewerContents().update();
	}
}
