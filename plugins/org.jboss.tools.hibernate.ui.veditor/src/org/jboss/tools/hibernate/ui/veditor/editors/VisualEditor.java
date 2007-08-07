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
package org.jboss.tools.hibernate.ui.veditor.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.veditor.editors.parts.OrmEditPartFactory;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;

public class VisualEditor extends GraphicalEditor {
	
	private OrmDiagram ormDiagram = null;
	
	public VisualEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
		
	public void doSave(IProgressMonitor monitor) {
		ormDiagram.save();
		ormDiagram.setDirty(false);
	}
	
	public void doSaveAs() {
	}

	protected void initializeGraphicalViewer() {
		final GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new OrmEditPartFactory());
		viewer.setRootEditPart(new GEFRootEditPart());
		viewer.addDropTargetListener(createTransferDropTargetListener());
		viewer.setContents(ormDiagram);
	}

	protected void createActions() {
		
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(),new WorkbenchPartAction(this){

			protected boolean calculateEnabled() {
				return true;
			}
			public void run() {
				ormDiagram.refresh();
			}
		});
		
		super.createActions();
		
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), getActionRegistry().getAction(ActionFactory.PRINT.getId()));
	}
		
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class) template);
			}
		};
	}
	
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public boolean isSaveOnCloseNeeded() {
		return true;
	}
		
	public void refreshDirty() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	public boolean isDirty() {
		return ormDiagram.isDirty();
	}
	
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)input;
		ConsoleConfiguration configuration = objectEditorInput.getConfiguration();
		RootClass rootClass = (RootClass)(objectEditorInput).getObject();
		setPartName("Diagram for " + rootClass.getEntityName());
		ormDiagram = new OrmDiagram(configuration, rootClass, objectEditorInput.getJavaProject());
	}
	
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			DiagramContentOutlinePage outline = new DiagramContentOutlinePage(
					new TreeViewer());
			outline.setGraphicalViewer(getGraphicalViewer());
			outline.setSelectionSynchronizer(getSelectionSynchronizer());
			return outline;
		}

		return super.getAdapter(type);
	}
}
