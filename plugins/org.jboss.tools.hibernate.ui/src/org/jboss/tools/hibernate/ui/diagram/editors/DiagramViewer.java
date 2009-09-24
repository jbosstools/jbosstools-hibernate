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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ActionMenu;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.CollapseAllAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExpandAllAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExportImageAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenSourceAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.PrintDiagramViewerAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.RefreshAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleAssociationAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleClassMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleConnectionsAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleForeignKeyConstraintAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.TogglePropertyMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeExpandStateAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeVisibleStateAction;
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
 * @author ?
 * @author Vitali Yemialyanchyk
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
		ormDiagram.saveInFile();
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

	@SuppressWarnings("unchecked")
	protected void createActions() {

		//super.createActions();
		// BEGIN: redefine super.createActions
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new RefreshAction(this);
		registry.registerAction(action);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REFRESH.getId(), action);
		
		action = new UndoAction(this);
		registry.registerAction(action);
		getStackActions().add(action.getId());
		
		action = new RedoAction(this);
		registry.registerAction(action);
		getStackActions().add(action.getId());
		
		action = new SelectAllAction(this);
		registry.registerAction(action);
		
		action = new DeleteAction((IWorkbenchPart)this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		
		action = new SaveAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());
		
		registry.registerAction(new PrintDiagramViewerAction(this));
		// END: redefine super.createActions

		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), 
				getActionRegistry().getAction(ActionFactory.PRINT.getId()));
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), 
				getActionRegistry().getAction(ActionFactory.SELECT_ALL.getId()));

		action = new OpenMappingAction(this);
		registry.registerAction(action);

		action = new OpenSourceAction(this);
		registry.registerAction(action);

		action = new ExportImageAction(this);
		registry.registerAction(action);

		action = new AutoLayoutAction(this);
		registry.registerAction(action);
		
		ToggleConnectionsAction actionToggleConnections = new ToggleConnectionsAction(this);
		registry.registerAction(actionToggleConnections);
		
		action = new ToggleAssociationAction(this);
		registry.registerAction(action);
		
		action = new ToggleClassMappingAction(this);
		registry.registerAction(action);
		
		action = new ToggleForeignKeyConstraintAction(this);
		registry.registerAction(action);
		
		action = new TogglePropertyMappingAction(this);
		registry.registerAction(action);
		
		action = new ToggleShapeExpandStateAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new ToggleShapeVisibleStateAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new CollapseAllAction(this);
		registry.registerAction(action);

		action = new ExpandAllAction(this);
		registry.registerAction(action);

		action = new ZoomInAction(gefRootEditPart.getZoomManager());
		registry.registerAction(action);

		action = new ZoomOutAction(gefRootEditPart.getZoomManager());
		registry.registerAction(action);

		Action[] act = new Action[4];
		act[0] = (Action)registry.getAction(TogglePropertyMappingAction.ACTION_ID);
		act[1] = (Action)registry.getAction(ToggleClassMappingAction.ACTION_ID);
		act[2] = (Action)registry.getAction(ToggleAssociationAction.ACTION_ID);
		act[3] = (Action)registry.getAction(ToggleForeignKeyConstraintAction.ACTION_ID);
		actionToggleConnections.setMenuCreator(new ActionMenu(act));

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
	
	protected String getItemName(RootClass rootClass) {
		String res = rootClass.getEntityName();
		if (res == null) {
			res = rootClass.getClassName();
		}
		if (res == null) {
			res = rootClass.getNodeName();
		}
		res = res.substring(res.lastIndexOf(".") + 1); //$NON-NLS-1$
		return res;
	}

	protected void setInput(IEditorInput input) {
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)input;
		ConsoleConfiguration configuration = objectEditorInput.getConfiguration();
		Object obj = objectEditorInput.getObject();
		setPartName(DiagramViewerMessages.DiagramViewer_diagram_for + " " + getDiagramName(obj)); //$NON-NLS-1$
		if (obj instanceof RootClass) {
			RootClass rootClass = (RootClass)obj;
			ormDiagram = new OrmDiagram(configuration, rootClass);
		} else if (obj instanceof RootClass[]) {
			RootClass[] rootClasses = (RootClass[])obj;
			ormDiagram = new OrmDiagram(configuration, rootClasses);
		}
		super.setInput(input);
		loadProperties();
	}

	protected String getDiagramName(Object obj) {
		String name = ""; //$NON-NLS-1$
		if (obj instanceof RootClass) {
			RootClass rootClass = (RootClass)obj;
			name = getItemName(rootClass);
		} else if (obj instanceof RootClass[]) {
			RootClass[] rootClasses = (RootClass[])obj;
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 0; i < rootClasses.length; i++) {
				names.add(getItemName(rootClasses[i]));
			}
			// sort to get same name for same combinations of entities
			Collections.sort(names);
			name = names.size() > 0 ? names.get(0) : ""; //$NON-NLS-1$
			for (int i = 1; i < rootClasses.length; i++) {
				name += " & " + names.get(i); //$NON-NLS-1$
			}
		}
		return name;
	}

	public String getDiagramName() {
		IEditorInput input = getEditorInput();
		Object obj = null;
		if (input instanceof ObjectEditorInput) {
			ObjectEditorInput objectEditorInput = (ObjectEditorInput)input;
			obj = objectEditorInput.getObject();
		}
		return getDiagramName(obj);
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
			RefreshAction refreshAction = (RefreshAction)getActionRegistry().getAction(
					ActionFactory.REFRESH.getId());
			refreshAction.setOutlinePage(outline);
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
		ormDiagram.setZoom(getZoom());
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

	/**
	 * Returns the current zoom level.
	 * @return double the zoom level
	 */
	public double getZoom() {
		double zoom = 1.0;
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
			.getProperty(ZoomManager.class.toString());
		if (manager != null) {
			zoom = manager.getZoom();
		}
		return zoom;
	}
	
	public double getFitHeightZoomValue() {
		double res = 1.0;
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
			.getProperty(ZoomManager.class.toString());
		Method m = null;
		try {
			m = manager.getClass().getDeclaredMethod("getFitHeightZoomLevel"); //$NON-NLS-1$
			m.setAccessible(true);
			res = (Double)m.invoke(manager);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return res;
	}
	
	public double getFitPageZoomValue() {
		double res = 1.0;
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
			.getProperty(ZoomManager.class.toString());
		Method m = null;
		try {
			m = manager.getClass().getDeclaredMethod("getFitPageZoomLevel"); //$NON-NLS-1$
			m.setAccessible(true);
			res = (Double)m.invoke(manager);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return res;
	}
	
	public double getFitWidthZoomValue() {
		double res = 1.0;
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
			.getProperty(ZoomManager.class.toString());
		Method m = null;
		try {
			m = manager.getClass().getDeclaredMethod("getFitWidthZoomLevel"); //$NON-NLS-1$
			m.setAccessible(true);
			res = (Double)m.invoke(manager);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return res;
	}
	
	
	public boolean getConnectionsVisibilityAssociation() {
		return getViewerContents().getConnectionsVisibilityAssociation();
	}
	
	public void setConnectionsVisibilityAssociation(boolean connectionsVisibilityAssociation) {
		getViewerContents().setConnectionsVisibilityAssociation(connectionsVisibilityAssociation);
		ActionRegistry registry = getActionRegistry();
		IAction action = registry.getAction(ToggleAssociationAction.ACTION_ID);
		action.setChecked(connectionsVisibilityAssociation);
	}
	
	public boolean getConnectionsVisibilityClassMapping() {
		return getViewerContents().getConnectionsVisibilityClassMapping();
	}
	
	public void setConnectionsVisibilityClassMapping(boolean connectionsVisibilityClassMapping) {
		getViewerContents().setConnectionsVisibilityClassMapping(connectionsVisibilityClassMapping);
		ActionRegistry registry = getActionRegistry();
		IAction action = registry.getAction(ToggleClassMappingAction.ACTION_ID);
		action.setChecked(connectionsVisibilityClassMapping);
	}
	
	public boolean getConnectionsVisibilityForeignKeyConstraint() {
		return getViewerContents().getConnectionsVisibilityForeignKeyConstraint();
	}
	
	public void setConnectionsVisibilityForeignKeyConstraint(boolean connectionsVisibilityForeignKeyConstraint) {
		getViewerContents().setConnectionsVisibilityForeignKeyConstraint(connectionsVisibilityForeignKeyConstraint);
		ActionRegistry registry = getActionRegistry();
		IAction action = registry.getAction(ToggleForeignKeyConstraintAction.ACTION_ID);
		action.setChecked(connectionsVisibilityForeignKeyConstraint);
	}
	
	public boolean getConnectionsVisibilityPropertyMapping() {
		return getViewerContents().getConnectionsVisibilityPropertyMapping();
	}
	
	public void setConnectionsVisibilityPropertyMapping(boolean connectionsVisibilityPropertyMapping) {
		getViewerContents().setConnectionsVisibilityPropertyMapping(connectionsVisibilityPropertyMapping);
		ActionRegistry registry = getActionRegistry();
		IAction action = registry.getAction(TogglePropertyMappingAction.ACTION_ID);
		action.setChecked(connectionsVisibilityPropertyMapping);
	}
}
