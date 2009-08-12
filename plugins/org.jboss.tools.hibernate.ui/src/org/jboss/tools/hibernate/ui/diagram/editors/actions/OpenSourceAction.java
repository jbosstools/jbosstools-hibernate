package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.view.ObjectEditorInput;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenSourceAction extends SelectionAction {
	public static final String ACTION_ID = "org.jboss.tools.hibernate.ui.diagram.editors.actions.open.source"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		UiPlugin.getImageDescriptor("icons/java.gif"); //$NON-NLS-1$

	public OpenSourceAction(IWorkbenchPart part) {
		super(part);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.OpenSourceAction_open_source_file);
		setImageDescriptor(img);
	}

	public void run() {
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)((DiagramViewer)getWorkbenchPart()).getEditorInput();
		ConsoleConfiguration consoleConfig = objectEditorInput.getConfiguration();

		DiagramViewer part = (DiagramViewer)getWorkbenchPart();
		Set<Shape> selectedElements = part.getSelectedElements();

		IEditorPart editorPart = null;
		Iterator<Shape> iterator = selectedElements.iterator();
		// open only first editor - no sense to open all of them
		while (iterator.hasNext() && editorPart == null) {
			Shape shape = iterator.next();
			Object selection = shape.getOrmElement();
			if (selection instanceof Column || selection instanceof Table) {
				Iterator<Connection> targetConnections = shape.getTargetConnections().iterator();
				while (targetConnections.hasNext()) {
					Connection connection = targetConnections.next();
					Shape sh1 = connection.getSource();
					Shape sh2 = connection.getTarget();
					if (shape == sh1 && sh2 != null) {
						shape = sh2;
						break;
					} else if (shape == sh2 && sh1 != null) {
						shape = sh1;
						break;
					}
				}
				selection = shape.getOrmElement();
			}
			PersistentClass rootClass = null;
			if (selection instanceof PersistentClass) {
				rootClass = (PersistentClass) selection;
			} else if (selection instanceof Property) {
				rootClass = ((Property) selection).getPersistentClass();
			} else {
				continue;
			}

			String fullyQualifiedName = rootClass.getClassName();//HibernateUtils.getPersistentClassName(rootClass);
			/*if (fullyQualifiedName.indexOf("$") > 0) {
				fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("$"));
			}*/
			try {
				editorPart = org.hibernate.eclipse.console.actions.OpenSourceAction.run(consoleConfig, selection, fullyQualifiedName);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenSourceAction_canot_open_source_file, e);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenSourceAction_canot_find_source_file, e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenSourceAction_canot_find_source_file, e);
			}
		}
	}

	protected boolean calculateEnabled() {
		DiagramViewer part = (DiagramViewer)getWorkbenchPart();
		return part.getSelectedElements().size() > 0;
	}
}
