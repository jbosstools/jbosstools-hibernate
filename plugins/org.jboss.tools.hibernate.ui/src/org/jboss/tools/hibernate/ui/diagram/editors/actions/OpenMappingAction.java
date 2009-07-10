/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.SpecialRootClass;
import org.jboss.tools.hibernate.ui.view.ObjectEditorInput;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenMappingAction extends SelectionAction {
	public static final String ACTION_ID = "org.jboss.tools.hibernate.ui.diagram.editors.actions.open.mapping"; //$NON-NLS-1$

	public OpenMappingAction(IWorkbenchPart part) {
		super(part);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.OpenMappingAction_open_mapping_file);
		setImageDescriptor(UiPlugin.getImageDescriptor("icons/mapping.gif")); //$NON-NLS-1$
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
			if (selection instanceof Property
					&& ((Property)selection).getPersistentClass() instanceof SpecialRootClass){
				Property compositSel = ((Property)selection);
				Property parentProperty = ((SpecialRootClass)compositSel.getPersistentClass()).getProperty();
				try {
					editorPart = org.hibernate.eclipse.console.actions.OpenMappingAction.run(consoleConfig, compositSel, parentProperty);
				} catch (PartInitException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_canot_find_or_open_mapping_file, e);
				} catch (JavaModelException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_canot_find_or_open_mapping_file, e);
				} catch (FileNotFoundException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_canot_find_or_open_mapping_file, e);
				}
				continue;
			}
			if (selection instanceof SpecialRootClass) {
    			selection = ((SpecialRootClass)selection).getProperty();
			}
			Shape shapeParent = null;
			Object selectionParent = null;
			if (selection instanceof Column){
				shapeParent = (Shape)shape.getParent();
				selectionParent = shapeParent.getOrmElement();
			}
			try {
				editorPart = org.hibernate.eclipse.console.actions.OpenMappingAction.run(consoleConfig, selection, selectionParent);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_open_mapping_file, e);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_open_mapping_file, e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(DiagramViewerMessages.OpenMappingAction_open_mapping_file, e);
			}
		}
	}

	protected boolean calculateEnabled() {
		DiagramViewer part = (DiagramViewer)getWorkbenchPart();
		return part.getSelectedElements().size() > 0;
	}
}
