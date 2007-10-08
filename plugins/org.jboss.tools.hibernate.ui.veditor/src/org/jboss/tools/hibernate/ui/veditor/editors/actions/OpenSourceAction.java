package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;
import org.jboss.tools.hibernate.ui.view.views.OpenFileActionUtils;

public class OpenSourceAction extends SelectionAction {
	public static String ACTION_ID = "org.jboss.tools.hibernate.ui.veditor.editors.actions.open.source";

	public OpenSourceAction(IWorkbenchPart part) {
		super(part);
		setId(ACTION_ID);
		setText("Open Source File");
	}

	public void run() {
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)((VisualEditor)getWorkbenchPart()).getEditorInput();
		ConsoleConfiguration consoleConfiguration = objectEditorInput.getConfiguration();
		IJavaProject proj = objectEditorInput.getJavaProject();

		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();

		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			PersistentClass rootClass = (PersistentClass) iterator.next();

			IResource resource = null;
			String fullyQualifiedName = rootClass.getClassName();
			if (fullyQualifiedName.indexOf("$") > 0) {
				fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("$"));
			}
			try {
				resource = proj.findType(fullyQualifiedName).getResource();
			} catch (JavaModelException e) {
				VisualEditorPlugin.getDefault().logError("Can't find source file.", e);
			}
			
			if (resource instanceof IFile){
	            try {
	            	OpenFileActionUtils.openEditor(ViewPlugin.getPage(), (IFile) resource);
	            } catch (PartInitException e) {
	            	VisualEditorPlugin.getDefault().logError("Can't open source file.", e);
	            }               
	        }
		}
	}

	protected boolean calculateEnabled() {
		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();
		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
			if (elem instanceof PersistentClass) return true; 
		}
		return false;
	}
}
