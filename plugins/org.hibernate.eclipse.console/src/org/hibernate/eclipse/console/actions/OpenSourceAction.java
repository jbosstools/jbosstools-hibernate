package org.hibernate.eclipse.console.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;

/**
 * @author Dmitry Geraskov
 */

public class OpenSourceAction extends SelectionListenerAction {

	public OpenSourceAction() { 
		super("Open Source File");
		setToolTipText("Open Source File");
		setEnabled( true );
	}
	
	public void run() {
		IStructuredSelection sel = getStructuredSelection();
		if (sel instanceof TreeSelection){
			TreePath path = ((TreeSelection)sel).getPaths()[0];
	    	PersistentClass persClass = getPersistentClass(path.getLastSegment());
	    	Assert.isNotNull(persClass);    	
			ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(path.getSegment(0));
			IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
	
			IResource resource = null;
			String fullyQualifiedName = OpenFileActionUtils.getPersistentClassName(persClass);
			IType type = null;
			try {
				type = proj.findType(fullyQualifiedName);
				if (type != null) resource = type.getResource();
			
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Can't find source file.", e);
			}
			
			if (resource instanceof IFile){
	        try {
	        	IEditorPart editorPart = OpenFileActionUtils.openEditor(HibernateConsolePlugin.getDefault().getActiveWorkbenchWindow().getActivePage(), (IFile) resource);
	        	if (editorPart instanceof JavaEditor) {
	        		IJavaElement jElement = null;
	        		if (path.getLastSegment() instanceof Property){
	        			jElement = type.getField(((Property)path.getLastSegment()).getName());
	        		} else {
	        			jElement = type;
	        		}        		
					JavaEditor jEditor = (JavaEditor) editorPart;
					selectionToEditor(jElement, jEditor);				
				}        	
	        } catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Can't open source file.", e);
	        }               
			}
			
			if (resource == null) {
				MessageDialog.openInformation(HibernateConsolePlugin.getDefault().getShell(), "Open Source File", "Source file for class '" + fullyQualifiedName + "' not found.");
			}
		}
	}
	
	private PersistentClass getPersistentClass(Object selection){
    	if (selection instanceof Property){
    		return ((Property)selection).getPersistentClass();
		} else if (selection instanceof Subclass){
			return (PersistentClass)selection;
		} else {
			return (RootClass)selection;
		}
	}

	private void selectionToEditor(IJavaElement jElement, JavaEditor jEditor) {
		if (jEditor != null) {
			jEditor.setSelection(jElement);
		}
	}

}
