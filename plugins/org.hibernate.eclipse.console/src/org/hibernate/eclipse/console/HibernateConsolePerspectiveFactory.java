/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.eclipse.console;

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

	static final String ID_CONSOLE_PERSPECTIVE = "org.hibernate.eclipse.console.HibernateConsolePerspective";
	//	Folders
	private static final String ID_CONFIGURATION_VIEW = "org.hibernate.eclipse.console.views.KnownConfigurationsView";
	private static final String ID_QUERYRESULTS_VIEW = "org.hibernate.eclipse.console.views.QueryPageTabView";
	private static final String ID_PROPERTY_SHEET_VIEW = "org.eclipse.ui.views.PropertySheet";
	private static final String ID_DYNAMIC_QUERY_TRANSLATOR_VIEW = "org.hibernate.eclipse.console.views.DynamicQueryTranslatorView";
	private static final String ID_ENTITY_MODEL_VIEW = "org.hibernate.eclipse.graph.EntityGraphView";
	
	private static final String ID_QUERY_PARAMETERS = "org.hibernate.eclipse.console.views.QueryParametersView";
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		IFolderLayout folder= layout.createFolder("left", IPageLayout.LEFT, (float)0.25, editorArea); //$NON-NLS-1$
		folder.addView(ID_CONFIGURATION_VIEW);
		folder.addView(JavaUI.ID_PACKAGES);
		folder.addPlaceholder(IPageLayout.ID_RES_NAV);
		
		IFolderLayout propertiesFolder= layout.createFolder("leftBottom", IPageLayout.BOTTOM, (float)0.75, "left"); //$NON-NLS-1$
		propertiesFolder.addView(ID_PROPERTY_SHEET_VIEW);
				
		
		IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
		outputfolder.addView("org.eclipse.pde.runtime.LogView");
		outputfolder.addView(ID_QUERYRESULTS_VIEW);
		outputfolder.addView(ID_DYNAMIC_QUERY_TRANSLATOR_VIEW);
		outputfolder.addView(ID_ENTITY_MODEL_VIEW);
		outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
				
		IFolderLayout outlines = layout.createFolder("outlines", IPageLayout.RIGHT, 0.75f, editorArea);
		outlines.addView(ID_QUERY_PARAMETERS);
		outlines.addView(IPageLayout.ID_OUTLINE);
		
		
		layout.setEditorAreaVisible(true);
		
		//HibernateConsolePlugin.getDefault().openScratchHQLEditor(null);
	}

}
