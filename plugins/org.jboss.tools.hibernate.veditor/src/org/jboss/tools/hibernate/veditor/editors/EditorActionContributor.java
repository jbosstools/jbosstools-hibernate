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
package org.jboss.tools.hibernate.veditor.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.jboss.tools.hibernate.view.ViewPlugin;



public class EditorActionContributor extends ActionBarContributor {
	
	protected void buildActions() {
		IWorkbenchAction workbenchAction = ActionFactory.REFRESH.create(getPage().getWorkbenchWindow());
		workbenchAction.setImageDescriptor(ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("Explorer.refreshOrmGef"))); //$NON-NLS-1$
		workbenchAction.setToolTipText(Messages.EditorActionContributor_Refresh_Visual_Mapping);
		addAction(workbenchAction);
	}
	
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.REFRESH.getId()));
	}
	
	protected void declareGlobalActionKeys() {
	}
}