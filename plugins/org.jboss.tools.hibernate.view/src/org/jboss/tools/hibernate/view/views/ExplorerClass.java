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
package org.jboss.tools.hibernate.view.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author Tau from Minsk
 *
 */
public class ExplorerClass extends OrmExplorer {

	/**
	 * The constructor.
	 */
	public ExplorerClass() {
		super();
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// add 22.06.2005
		this.makeActions();
		contributeToActionBars();
		actionRootPackageClassField.run();
		
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.add(actionRootPackageClassField);
		
		// del tau 29.07.2005
		//manager.add(actionRootClassField);
		
		manager.add(actionRootStorageClassField);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	protected void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionRootPackageClassField);
		
		// del tau 29.07.2005		
		//manager.add(actionRootClassField);
		manager.add(actionRootStorageClassField);
	}
	
	static public String getExplorerId(){
	    return "org.jboss.tools.hibernate.view.views.ExplorerClass";
	}
	
	private void makeActions() {
		
		// ExplorerClass actions
		//***********
		actionRootPackageClassField = new Action(BUNDLE.getString("Explorer.ActionRootPackageClassFieldName"),Action.AS_CHECK_BOX) {
			public void run() {
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("actionRootPackageClassField.run()");
				
				viewerSetInput(ormModel, contentProvider_PACKAGE_CLASS_FIELD_CONTENT_PROVIDER);
				actionRootPackageClassField.setChecked(true);
				// del tau 29.07.2005
				//actionRootClassField.setChecked(false);
				actionRootStorageClassField.setChecked(false);
				//setContentDescription(BUNDLE.getString("Explorer.ActionRootPackageClassFieldContentDescription"));						
			}
		};
		actionRootPackageClassField.setToolTipText(BUNDLE.getString("Explorer.ActionRootPackageClassFieldToolTipText"));
		actionRootPackageClassField.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.RootPackageClassField")));
		
		/* del tau 29.07.2005
		actionRootClassField = new Action(BUNDLE.getString("Explorer.ActionRootClassFieldName"),Action.AS_CHECK_BOX) {
			public void run() {
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.log("actionRootClassField.run()");
				
				viewerSetInput(ormProjects, contentProvider_CLASS_FIELD_CONTENT_PROVIDER);
				actionRootPackageClassField.setChecked(false);
				actionRootClassField.setChecked(true);					
				actionRootStorageClassField.setChecked(false);
				//setContentDescription(BUNDLE.getString("Explorer.ActionRootClassFieldContentDescription"));
			}
		};
		actionRootClassField.setToolTipText(BUNDLE.getString("Explorer.ActionRootClassFieldToolTipText"));
		actionRootClassField.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.RootClassField")));
		*/

		//***********
		actionRootStorageClassField = new Action(BUNDLE.getString("Explorer.ActionRootStorageClassFieldName"),Action.AS_CHECK_BOX) {
			public void run() {
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("actionRootStorageClassField.run()");
			
				viewerSetInput(ormModel, contentProvider_STORAGE_CLASS_FIELD_CONTENT_PROVIDER);
				// del tau 29.07.2005				
				//actionRootClassField.setChecked(false);					
				actionRootPackageClassField.setChecked(false);
				actionRootStorageClassField.setChecked(true);
				//setContentDescription(BUNDLE.getString("Explorer.ActionRootStorageClassFieldContentDescription"));
			}
		    		
		};
		actionRootStorageClassField.setToolTipText(BUNDLE.getString("Explorer.ActionRootStorageClassFieldToolTipText"));
		actionRootStorageClassField.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.RootStorageClassField")));
	}
	
	
}