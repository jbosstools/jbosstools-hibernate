package org.hibernate.eclipse.console.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.tool.hbm2x.XMLPrettyPrinter;

public class JTidyFormatAction implements IObjectActionDelegate {

	private IFile file;
    private IWorkbenchPart targetPart;
    private Properties properties;

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
		
        if(file!=null) {
            try {
                InputStream contents = null ;
                ByteArrayOutputStream bos = null;
                ByteArrayInputStream stream = null;
                try {
                    contents = file.getContents();
                    bos = new ByteArrayOutputStream();
                    properties = new Properties();
                    properties.load(this.getClass().getResourceAsStream("jtidy.properties"));
                    XMLPrettyPrinter.prettyPrint(properties, contents, bos);
                    stream = new ByteArrayInputStream(bos.toByteArray());
                    file.setContents(stream, true, false, null);
                } finally {
                    if(stream!=null) stream.close();
                    if(bos!=null) bos.close();
                    if(contents!=null) contents.close();                    
                }
                
            } catch (CoreException e) {
                HibernateConsolePlugin.showError(targetPart.getSite().getShell(), "Error while running JTidy", e);
            } catch (IOException io) {
                HibernateConsolePlugin.showError(targetPart.getSite().getShell(), "Error while running JTidy", io);
            }
        }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
        if(selection instanceof StructuredSelection) {
            StructuredSelection sel = (StructuredSelection)selection;
            if(!sel.isEmpty()) {
                if(sel.getFirstElement() instanceof IFile) {
                    this.file = (IFile)sel.getFirstElement();
                    action.setEnabled(true);
                    return;
                }
            }
        }
        file = null;
        action.setEnabled(false);
	}

}
