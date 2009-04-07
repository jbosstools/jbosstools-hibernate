/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.veditor.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.jboss.tools.hibernate.ui.veditor.UIVEditorMessages;
import org.jboss.tools.hibernate.ui.view.ImageBundle;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class EditorActionContributor extends ActionBarContributor {

	protected void buildActions() {
		IWorkbenchAction workbenchAction = ActionFactory.REFRESH.create(getPage().getWorkbenchWindow());
		workbenchAction.setImageDescriptor(ViewPlugin.getImageDescriptor(ImageBundle.getString("Explorer.refreshOrmGef"))); //$NON-NLS-1$
		workbenchAction.setToolTipText(UIVEditorMessages.EditorActionContributor_refresh_visual_mapping);
		addAction(workbenchAction);
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
	}

	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.REFRESH.getId()));
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
	}

	protected void declareGlobalActionKeys() {
	}
}