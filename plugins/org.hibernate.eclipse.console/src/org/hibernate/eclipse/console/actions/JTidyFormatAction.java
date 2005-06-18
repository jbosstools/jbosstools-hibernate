package org.hibernate.eclipse.console.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.PluginAction;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.tool.hbm2x.XMLPrettyPrinter;

public class JTidyFormatAction implements IObjectActionDelegate {

    private IWorkbenchPart targetPart;
    
    /**
	 * Constructor for Action1.
	 */
	public JTidyFormatAction() {
		super();
        
        
        
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection selection = (IStructuredSelection) ( (PluginAction)action).getSelection();
        
        if(selection!=null && MessageDialog.openQuestion(targetPart.getSite().getShell(), "Format with JTidy", "Do you want to format " + selection.size() + " xml files with JTidy ?") ) {
            Iterator iterator = selection.iterator();
            try {
            while(iterator.hasNext() ) {
                IFile file = (IFile) iterator.next();
                InputStream contents = null ;
                ByteArrayOutputStream bos = null;
                ByteArrayInputStream stream = null;
                try {
                    contents = file.getContents();
                    bos = new ByteArrayOutputStream();
                    XMLPrettyPrinter.prettyPrint(contents, bos);
                    stream = new ByteArrayInputStream(bos.toByteArray() );
                    file.setContents(stream, true, true, null);
                } finally {
                    if(stream!=null) stream.close();
                    if(bos!=null) bos.close();
                    if(contents!=null) contents.close();                    
                }
            }
            } catch (CoreException e) {
                HibernateConsolePlugin.getDefault().showError(targetPart.getSite().getShell(), "Error while running JTidy", e);
            } catch (IOException io) {
                HibernateConsolePlugin.getDefault().showError(targetPart.getSite().getShell(), "Error while running JTidy", io);
            }
        }
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub
        
    }

}
