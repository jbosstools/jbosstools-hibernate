package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public abstract class EditorActionDelegate implements IEditorActionDelegate {
    protected IEditorPart editor;

    /**
     * Creates a new EditorActionDelegate.
     */
    public EditorActionDelegate() {
    }

    /** 
     * The <code>EditorActionDelegate</code> implementation of this
     * <code>IActionDelegate</code> method does nothing.
     *
     * Selection in the desktop has changed. Plugin provider
     * can use it to change the availability of the action
     * or to modify other presentation properties.
     *
     * <p>Action delegate cannot be notified about
     * selection changes before it is loaded. For that reason,
     * control of action's enable state should also be performed
     * through simple XML rules defined for the extension
     * point. These rules allow enable state control before
     * the delegate has been loaded.</p>
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing
    }

    /** 
     * The <code>EditorActionDelegate</code> implementation of this
     * <code>IEditorActionDelegate</code> method remembers the active editor.
     *
     * The matching editor has been activated. Notification
     * guarantees that only editors that match the type for which 
     * this action has been registered will be tracked.
     *
     * @param action action proxy that represents this delegate in the desktop
     * @param editor the matching editor that has been activated
     */
    public void setActiveEditor(IAction action, IEditorPart editor) {
        this.editor = editor;
    }
}
