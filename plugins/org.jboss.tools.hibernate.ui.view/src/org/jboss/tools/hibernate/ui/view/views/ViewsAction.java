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
package org.jboss.tools.hibernate.ui.view.views;

import java.util.HashMap;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;


public class ViewsAction {
    static  ActionOrmTree openEditorAction;

    static {
        openEditorAction = new ActionOrmTree() { 
        	HashMap<Object,ObjectEditorInput> hashMap = new HashMap<Object,ObjectEditorInput>();
			public void rush() {
				ObjectEditorInput input = hashMap.get(this.getViewer().getTree().getSelection()[0].getData());
				if(input == null) {
					input = new ObjectEditorInput(this.getViewer().getTree().getSelection()[0].getData());
					hashMap.put(this.getViewer().getTree().getSelection()[0].getData(), input);
				}
				try {
					IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor"); //$NON-NLS-1$
				} catch (PartInitException e) {
					ViewPlugin.getDefault().logError(e);
				}
			}
		}; 
		openEditorAction.setText(Messages.getString("Explorer.OpenEditorActionName")); //$NON-NLS-1$
		openEditorAction.setToolTipText(Messages.getString("Explorer.OpenEditorActionToolTipText")); //$NON-NLS-1$
    }
}
