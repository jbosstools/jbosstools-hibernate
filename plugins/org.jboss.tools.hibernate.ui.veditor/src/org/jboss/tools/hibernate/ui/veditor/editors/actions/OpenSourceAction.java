package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.view.views.HibernateUtils;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;

/**
 * @author Dmitry Geraskov
 *
 */
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
			//PersistentClass rootClass = (PersistentClass) iterator.next();
			Object selection = iterator.next();
			PersistentClass rootClass = null;
			if (selection instanceof PersistentClass) {
				rootClass = (PersistentClass) selection;				
			} else if (selection instanceof Property) {
				rootClass = ((Property) selection).getPersistentClass();				
			} else continue;
			
			IResource resource = null;
			String fullyQualifiedName = HibernateUtils.getPersistentClassName(rootClass);
			if (fullyQualifiedName.indexOf("$") > 0) {
				fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("$"));
			}
			new org.hibernate.eclipse.console.actions.OpenSourceAction().run(selection, proj, fullyQualifiedName);
		}
	}

	protected boolean calculateEnabled() {
		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();
		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
			if (elem instanceof PersistentClass
					|| elem instanceof Property) return true; 
		}
		return false;
	}
}
