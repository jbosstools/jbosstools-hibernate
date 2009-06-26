package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.ui.veditor.UIVEditorMessages;
import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenSourceAction extends SelectionAction {
	public static final String ACTION_ID = "org.jboss.tools.hibernate.ui.veditor.editors.actions.open.source"; //$NON-NLS-1$

	public OpenSourceAction(IWorkbenchPart part) {
		super(part);
		setId(ACTION_ID);
		setText(UIVEditorMessages.OpenSourceAction_open_source_file);
		setImageDescriptor(VisualEditorPlugin.getImageDescriptor("icons/java.gif")); //$NON-NLS-1$
	}

	public void run() {
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)((VisualEditor)getWorkbenchPart()).getEditorInput();
		ConsoleConfiguration consoleConfig = objectEditorInput.getConfiguration();

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
			} else {
				continue;
			}

			IResource resource = null;
			String fullyQualifiedName = rootClass.getClassName();//HibernateUtils.getPersistentClassName(rootClass);
			/*if (fullyQualifiedName.indexOf("$") > 0) {
				fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("$"));
			}*/
			try {
				new org.hibernate.eclipse.console.actions.OpenSourceAction().run(consoleConfig, selection, fullyQualifiedName);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(UIVEditorMessages.OpenSourceAction_canot_open_source_file, e);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(UIVEditorMessages.OpenSourceAction_canot_find_source_file, e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(UIVEditorMessages.OpenSourceAction_canot_find_source_file, e);
			}
		}
	}

	protected boolean calculateEnabled() {
		//VisualEditor part = (VisualEditor)getWorkbenchPart();
		//return part.getSelectedElements().size() > 0;
		/**/
		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();
		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
			if (elem instanceof PersistentClass
					|| elem instanceof Property) return true;
		}
		return false;
		/**/
	}
}
