package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.actions.ExecuteHQLAction;


/**
 * This class installs and manages actions for the Criteria Editor. 
 */
public class CriteriaEditorActionContributor extends TextEditorActionContributor {

	private ExecuteHQLAction executeHQLAction;
	
    public CriteriaEditorActionContributor() {
        super();
        executeHQLAction = new ExecuteHQLAction();    
    }

    /**
     * Contributes items to the Workbench Edit menu.
     * 
     * @param mm the MenuManager to use
     */
    public void contributeToMenu( IMenuManager mm ) {
    	super.contributeToMenu(mm);
    }
    
    /**
     * Sets the active editor to this contributor.
     * This updates the actions to reflect the current editor.
     * 
     * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
     * @see EditorActionBarContributor#editorChanged
     */
    public void setActiveEditor( IEditorPart targetEditor ) {
        super.setActiveEditor( targetEditor );

        if(targetEditor instanceof QueryEditor) {
        	executeHQLAction.setHibernateQueryEditor((QueryEditor) targetEditor);
        }            
                
    }
    
    public void init(IActionBars bars, IWorkbenchPage page) {
    	super.init( bars, page );
    	
    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ExecuteHQLAction", executeHQLAction);
        bars.updateActionBars();
    }
    
    
} 