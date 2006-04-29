package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;



/**
 * This class installs and manages actions for the Criteria Editor. 
 */
public class CriteriaEditorActionContributor extends BasicTextEditorActionContributor {

    /**
     * Constructs an instance of this class.  This is the default constructor.
     */
    public CriteriaEditorActionContributor() {
        super();
        
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

        ITextEditor textEditor = null;
        if (targetEditor instanceof ITextEditor) {
            textEditor = (ITextEditor) targetEditor;
        }
        
                
    }
    
    public void init(IActionBars bars, IWorkbenchPage page) {
    	super.init( bars, page );
    	
    	bars.updateActionBars();
    }
    
    
} 