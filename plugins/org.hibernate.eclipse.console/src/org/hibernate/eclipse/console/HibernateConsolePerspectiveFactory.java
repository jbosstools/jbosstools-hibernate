/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.eclipse.console;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author max
 *
 */
public class HibernateConsolePerspectiveFactory implements IPerspectiveFactory {

	//	Folders
	public static final String ID_CONFIGURATION_FOLDERVIEW = "org.hibernate.eclipse.console.ConfigurationFolderView"; //$NON-NLS-1$
	public static final String ID_RESULT_FOLDERVIEW = "org.hibernate.eclipse.console.QueryResultsFolderView"; //$NON-NLS-1$
	public static final String ID_PROPERTY_SHEET_FOLDERVIEW = "org.hibernate.eclipse.console.PropertiesFolderView"; //$NON-NLS-1$

	public static final String ID_QUERYEDITOR_VIEW = "org.hibernate.eclipse.console.views.HQLEditorView";
	public static final String ID_CONFIGURATION_VIEW = "org.hibernate.eclipse.console.views.KnownConfigurationsView";
	public static final String ID_QUERYRESULTS_VIEW = "org.hibernate.eclipse.console.views.QueryPageTabView";
	public static final String ID_PROPERTY_SHEET_VIEW = "org.eclipse.ui.views.PropertySheet";
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		IFolderLayout side =
			layout.createFolder(
				ID_CONFIGURATION_FOLDERVIEW,
				IPageLayout.LEFT,
				0.33F,
				layout.getEditorArea() );
		
		side.addView(ID_CONFIGURATION_VIEW);
		
		IFolderLayout leftBottomLeft = 
			layout.createFolder(
				ID_PROPERTY_SHEET_FOLDERVIEW,
				IPageLayout.BOTTOM,
				0.75F,
				ID_CONFIGURATION_VIEW
			);
			
		leftBottomLeft.addView(ID_PROPERTY_SHEET_VIEW);
		
		//layout.addView(ID_QUERYEDITOR_VIEW, IPageLayout.TOP, 0.33F, layout.getEditorArea() ); //$NON-NLS-1$

		IFolderLayout bottomRight =
			layout.createFolder(
				ID_RESULT_FOLDERVIEW,
				IPageLayout.BOTTOM,
				0.50F,
				layout.getEditorArea() );
		
		bottomRight.addView(ID_QUERYRESULTS_VIEW);
		bottomRight.addView("org.eclipse.pde.runtime.LogView");
		
		layout.setEditorAreaVisible(true);
		
		//HibernateConsolePlugin.getDefault().openScratchHQLEditor(null);
	}

}
