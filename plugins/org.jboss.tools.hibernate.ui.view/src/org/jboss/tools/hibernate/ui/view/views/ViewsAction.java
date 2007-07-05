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
import java.util.ResourceBundle;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;


public class ViewsAction {
    static  ActionOrmTree openEditorAction;

    static private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
    static private ResourceBundle BUNDLE = ResourceBundle.getBundle(ViewsAction.class.getPackage().getName() + ".views");

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
					IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");
				} catch (PartInitException e) {
//					ExceptionHandler.logThrowableError(e,"OpenEditor");              
				}
			}
		}; 
		openEditorAction.setText(BUNDLE.getString("Explorer.OpenEditorActionName"));
		openEditorAction.setToolTipText(BUNDLE.getString("Explorer.OpenEditorActionToolTipText"));
    }
}
