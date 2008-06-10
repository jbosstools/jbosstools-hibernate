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
package org.hibernate.eclipse.console;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author max
 *
 */
public class HibernateConsolePerspectiveFactory implements IPerspectiveFactory {

	static public final String ID_CONSOLE_PERSPECTIVE = "org.hibernate.eclipse.console.HibernateConsolePerspective"; //$NON-NLS-1$
	//	Folders
	private static final String ID_CONFIGURATION_VIEW = "org.hibernate.eclipse.console.views.KnownConfigurationsView"; //$NON-NLS-1$
	private static final String ID_QUERYRESULTS_VIEW = "org.hibernate.eclipse.console.views.QueryPageTabView"; //$NON-NLS-1$
	private static final String ID_PROPERTY_SHEET_VIEW = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
	private static final String ID_DYNAMIC_QUERY_TRANSLATOR_VIEW = "org.hibernate.eclipse.console.views.DynamicSQLPreviewView"; //$NON-NLS-1$
	//private static final String ID_ENTITY_MODEL_VIEW = "org.hibernate.eclipse.graph.EntityGraphView";
	private static final String ID_CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView"; //TODO: could not find constant for it in eclipse  //$NON-NLS-1$
	
	private static final String ID_QUERY_PARAMETERS = "org.hibernate.eclipse.console.views.QueryParametersView"; //$NON-NLS-1$
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		IFolderLayout folder= layout.createFolder("left", IPageLayout.LEFT, (float)0.25, editorArea); //$NON-NLS-1$
		folder.addView(ID_CONFIGURATION_VIEW);
		folder.addView(JavaUI.ID_PACKAGES);
		folder.addPlaceholder(IPageLayout.ID_RES_NAV);
		
		IFolderLayout propertiesFolder= layout.createFolder("leftBottom", IPageLayout.BOTTOM, (float)0.75, "left"); //$NON-NLS-1$ //$NON-NLS-2$
		propertiesFolder.addView(ID_PROPERTY_SHEET_VIEW);
				
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		
		IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
		outputfolder.addView("org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$
		outputfolder.addView(ID_QUERYRESULTS_VIEW);
		outputfolder.addView(ID_DYNAMIC_QUERY_TRANSLATOR_VIEW);
		//outputfolder.addView(ID_ENTITY_MODEL_VIEW);
		outputfolder.addView(ID_CONSOLE_VIEW);
		outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
				
		IFolderLayout outlines = layout.createFolder("outlines", IPageLayout.RIGHT, 0.75f, editorArea); //$NON-NLS-1$
		outlines.addView(ID_QUERY_PARAMETERS);
		outlines.addView(IPageLayout.ID_OUTLINE);
		
		
		layout.setEditorAreaVisible(true);
		
		//HibernateConsolePlugin.getDefault().openScratchHQLEditor(null);
	}

}
