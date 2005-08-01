package org.hibernate.eclipse.console.editors;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.hibernate.eclipse.console.Messages;
import org.hibernate.eclipse.console.actions.ExecuteHQLAction;



/**
 * This class installs and manages actions for the HQL Editor. 
 */
public class HQLEditorActionContributor extends TextEditorActionContributor {

    protected RetargetTextEditorAction fContentAssistProposalAction;
    protected RetargetTextEditorAction fContentAssistTipAction;
    protected RetargetTextEditorAction fContentFormatAction;
    
    private IPropertyChangeListener    fConnectActionListener;
    private IPropertyChangeListener    fSetStatementTerminatorActionListener;
	private ExecuteHQLAction executeHQLAction;
	

    /**
     * Constructs an instance of this class.  This is the default constructor.
     */
    public HQLEditorActionContributor() {
        super();
        ResourceBundle bundle = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

        fContentAssistProposalAction = new RetargetTextEditorAction( bundle, "ContentAssistProposal." ); // $NON-NLS-1$
        fContentAssistTipAction =  new RetargetTextEditorAction( bundle, "ContentAssistTip." ); // $NON-NLS-1$
        fContentFormatAction = new RetargetTextEditorAction( bundle, "ContentFormat." ); // $NON-NLS-1$
        executeHQLAction = new ExecuteHQLAction();
        
    }

    /**
     * Contributes items to the Workbench Edit menu.
     * 
     * @param mm the MenuManager to use
     */
    public void contributeToMenu( IMenuManager mm ) {
        IMenuManager editMenu = mm.findMenuUsingPath( IWorkbenchActionConstants.M_EDIT );
        if (editMenu != null) {
            editMenu.add( new Separator() );
            editMenu.add( fContentAssistProposalAction );
            editMenu.add( fContentFormatAction );
            editMenu.add( fContentAssistTipAction );
        }
        /*IMenuManager hqlMenu = mm.findMenuUsingPath( "HQL" );
        if (hqlMenu != null) {
            hqlMenu.add( new Separator() );
            hqlMenu.add( fContentAssistTipAction );
        }*/
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
        
        // Set up the standard text editor actions.  These actions each have an
        // "retargetable action" associated with them.  The action needs to be
        // "retargeted" to associate it with the active editor whenever the active
        // editor changes.
        fContentAssistProposalAction.setAction( getAction( textEditor, "ContentAssistProposal" )); // $NON-NLS-1$
        fContentAssistTipAction.setAction( getAction( textEditor, "ContentAssistTip" )); // $NON-NLS-1$
        fContentFormatAction.setAction( getAction( textEditor, "ContentFormat" )); // $NON-NLS-1$
     //   IAction action = getAction( textEditor, "clearEditor");
       // action.equals(action);
        
        if(targetEditor instanceof HQLEditor) {
        	executeHQLAction.setHQLEditor((HQLEditor) targetEditor);
        }        
    }
    
    public void init(IActionBars bars, IWorkbenchPage page) {
    	super.init( bars, page );
    	
//    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ClearHQLEditorAction", fClearHQLEditorAction);
    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ExecuteHQLAction", executeHQLAction); 
    }
    
    
} 