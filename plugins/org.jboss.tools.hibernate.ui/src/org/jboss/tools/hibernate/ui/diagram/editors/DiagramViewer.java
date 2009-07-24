/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.CollapseAllAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExpandAllAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExportImageAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenSourceAction;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.GEFRootEditPart;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPart;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPartFactory;
import org.jboss.tools.hibernate.ui.diagram.editors.popup.PopupMenuProvider;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramRuler;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramRulerProvider;
import org.jboss.tools.hibernate.ui.view.ObjectEditorInput;

/**
 *
 */
public class DiagramViewer extends GraphicalEditor {

	private OrmDiagram ormDiagram = null;
	private GEFRootEditPart gefRootEditPart = new GEFRootEditPart();
	private RulerComposite rulerComp;

	public DiagramViewer() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public void doSave(IProgressMonitor monitor) {
		saveProperties();
		ormDiagram.save();
		ormDiagram.setDirty(false);
	}

	public void doSaveAs() {
	}

	protected void initializeGraphicalViewer() {
		final GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new OrmEditPartFactory());
		//
		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		gefRootEditPart.getZoomManager().setZoomLevelContributions(zoomLevels);
		IAction zoomIn = new ZoomInAction(gefRootEditPart.getZoomManager());
		IAction zoomOut = new ZoomOutAction(gefRootEditPart.getZoomManager());
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		//
		viewer.setRootEditPart(gefRootEditPart);
		viewer.addDropTargetListener(createTransferDropTargetListener());
		viewer.setContents(ormDiagram);

		PopupMenuProvider provider = new PopupMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(provider);
		getSite().registerContextMenu("FlowDiagramContextmenu", provider, viewer); //$NON-NLS-1$
		// Scroll-wheel Zoom
		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), 
			MouseWheelZoomHandler.SINGLETON);
		// Ruler properties
		DiagramRuler ruler = ormDiagram.getRuler(PositionConstants.WEST);
		RulerProvider rulerProvider = null;
		if (ruler != null) {
			rulerProvider = new DiagramRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, rulerProvider);
		ruler = ormDiagram.getRuler(PositionConstants.NORTH);
		rulerProvider = null;
		if (ruler != null) {
			rulerProvider = new DiagramRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, rulerProvider);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, 
				new Boolean(ormDiagram.getRulerVisibility()));
		loadProperties();
	}

	public GraphicalViewer getEditPartViewer() {
		return getGraphicalViewer();
	}

	protected void createActions() {

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REFRESH.getId(), new WorkbenchPartAction(this) {

			protected boolean calculateEnabled() {
				return true;
			}
			public void run() {
				ormDiagram.refresh();
			}
		});

		super.createActions();

		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), 
				getActionRegistry().getAction(ActionFactory.PRINT.getId()));
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), 
				getActionRegistry().getAction(ActionFactory.SELECT_ALL.getId()));

		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new OpenMappingAction(this);
		registry.registerAction(action);

		action = new OpenSourceAction(this);
		registry.registerAction(action);

		action = new ExportImageAction(this);
		registry.registerAction(action);

		action = new AutoLayoutAction(this);
		registry.registerAction(action);

		action = new CollapseAllAction(this);
		registry.registerAction(action);

		action = new ExpandAllAction(this);
		registry.registerAction(action);

		action = new ZoomInAction(gefRootEditPart.getZoomManager());
		registry.registerAction(action);

		action = new ZoomOutAction(gefRootEditPart.getZoomManager());
		registry.registerAction(action);

	}

	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class<?>) template);
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
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)input;
		ConsoleConfiguration configuration = objectEditorInput.getConfiguration();
		Object obj = objectEditorInput.getObject();
		if (obj instanceof RootClass) {
			RootClass rootClass = (RootClass)obj;
			setPartName(DiagramViewerMessages.DiagramViewer_diagram_for + rootClass.getEntityName());
			ormDiagram = new OrmDiagram(configuration, rootClass);
		} else if (obj instanceof RootClass[]) {
			RootClass[] rootClasses = (RootClass[])obj;
			String name = rootClasses.length > 0 ? rootClasses[0].getEntityName() : ""; //$NON-NLS-1$
			for (int i = 1; i < rootClasses.length; i++) {
				name += " & " + rootClasses[i].getEntityName(); //$NON-NLS-1$
			}
			setPartName(DiagramViewerMessages.DiagramViewer_diagram_for + name);
			ormDiagram = new OrmDiagram(configuration, rootClasses);
		}
		super.setInput(input);
		loadProperties();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			DiagramContentOutlinePage outline = new DiagramContentOutlinePage(
					new TreeViewer(), getActionRegistry());
			outline.setGraphicalViewer(getGraphicalViewer());
			outline.setSelectionSynchronizer(getSelectionSynchronizer());
			outline.setOrmDiagram(ormDiagram);
			outline.setEditor(this);
			return outline;
		}
		if (type == ZoomManager.class) {
			return getGraphicalViewer().getProperty(ZoomManager.class.toString());
		}
		return super.getAdapter(type);
	}

	public Set<Shape> getSelectedElements() {
		Set<Shape> ret = new HashSet<Shape>();
		List<?> selectedEditParts = getGraphicalViewer().getSelectedEditParts();
		Iterator<?> iterator = selectedEditParts.iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
			if (elem instanceof OrmEditPart) {
				Shape shape = (Shape)((OrmEditPart)elem).getModel();
				ret.add(shape);
			}
		}
		return ret;
	}

	public OrmDiagram getViewerContents() {
		return ormDiagram;
	}

	public DefaultEditDomain getDefaultEditDomain() {
		return getEditDomain();
	}

	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		//
		loadProperties();
		// Actions
		IAction showRulers = new ToggleRulerVisibilityAction(getGraphicalViewer());
		getActionRegistry().registerAction(showRulers);
		
		IAction snapAction = new ToggleSnapToGeometryAction(getGraphicalViewer());
		getActionRegistry().registerAction(snapAction);

		IAction showGrid = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(showGrid);
	}
	
	protected boolean loadProperties() {
		if (ormDiagram == null || getGraphicalViewer() == null) {
			return false;
		}
		// Ruler properties
		DiagramRuler ruler = ormDiagram.getRuler(PositionConstants.WEST);
		RulerProvider provider = null;
		if (ruler != null) {
			provider = new DiagramRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, provider);
		ruler = ormDiagram.getRuler(PositionConstants.NORTH);
		provider = null;
		if (ruler != null) {
			provider = new DiagramRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, 
				new Boolean(ormDiagram.getRulerVisibility()));
		
		// Snap to Geometry property
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, 
				new Boolean(ormDiagram.isSnapToGeometryEnabled()));
		
		// Grid properties
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, 
				new Boolean(ormDiagram.isGridEnabled()));
		// We keep grid visibility and enablement in sync
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, 
				new Boolean(ormDiagram.isGridEnabled()));
		
		// Zoom
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
				.getProperty(ZoomManager.class.toString());
		if (manager != null) {
			manager.setZoom(ormDiagram.getZoom());
		}
		return true;
	}

	protected boolean saveProperties() {
		if (ormDiagram == null || getGraphicalViewer() == null) {
			return false;
		}
		ormDiagram.setRulerVisibility(((Boolean)getGraphicalViewer()
				.getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY)).booleanValue());
		ormDiagram.setGridEnabled(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
		ormDiagram.setSnapToGeometry(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
				.getProperty(ZoomManager.class.toString());
		if (manager != null) {
			ormDiagram.setZoom(manager.getZoom());
		}
		return true;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected void createGraphicalViewer(Composite parent) {
		rulerComp = new RulerComposite(parent, SWT.NONE);
		super.createGraphicalViewer(rulerComp);
		rulerComp.setGraphicalViewer((ScrollingGraphicalViewer)getGraphicalViewer());
	}
}
