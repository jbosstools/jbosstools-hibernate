package org.hibernate.eclipse.console;



import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.editors.HQLEditor;
import org.hibernate.eclipse.console.editors.HQLEditorInput;


public class OpenHQLEditorAction implements IWorkbenchWindowActionDelegate {
    public OpenHQLEditorAction() {
    }

    public void run(IAction action) {
        try {
            final IWorkbenchWindow activeWorkbenchWindow =
                PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
                final HQLEditorInput editorInput =
                    new HQLEditorInput("<enter hql here>");
                page.openEditor(editorInput, HQLEditor.ID, true);
        } catch (PartInitException e) {
            throw new RuntimeException("Problem creating editor", e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }
}
