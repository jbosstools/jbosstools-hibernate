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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.LexicalSortingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleConnectionsAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeExpandStateAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ToggleShapeVisibleStateAction;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.TreePartFactory;
import org.jboss.tools.hibernate.ui.diagram.editors.popup.PopupMenuProvider;

/**
 *
 */
public class DiagramContentOutlinePage extends ContentOutlinePage implements
		IAdaptable {
	
	private GraphicalViewer graphicalViewer;
	
	private DiagramViewer editor;
	
	private OrmDiagram ormDiagram;

	private SelectionSynchronizer selectionSynchronizer;

	private PageBook pageBook;

	private Control outline;
	/*
	 * surface for drawing 
	 */
	private Canvas overview;

	private IAction showOutlineAction, showOverviewAction;

	static final int ID_OUTLINE = 0;

	static final int ID_OVERVIEW = 1;

	private Thumbnail thumbnail;
	
	private ActionRegistry actionRegistry;

	/**
	 * The constructor
	 * 
	 * @param viewer
	 */
	public DiagramContentOutlinePage(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	/**
	 * Sets graphical viewer
	 * 
	 * @param graphicalViewer
	 */
	public void setGraphicalViewer(GraphicalViewer graphicalViewer) {
		this.graphicalViewer = graphicalViewer;
	}
    
    public void update(GraphicalViewer graphicalViewer) {
        if (this.graphicalViewer != null) {
            if (this.graphicalViewer != graphicalViewer) {
                getSelectionSynchronizer().removeViewer(this.graphicalViewer);
                replaceSelectionChangedListeners(graphicalViewer);
                this.graphicalViewer = graphicalViewer;
                getSelectionSynchronizer().addViewer(graphicalViewer);
                initializeOverview();
            }
        }
        
    }

	
	/**
	 * @return graphical viewer
	 */
	public GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}

	/**
	 * sets selection synchronizer
	 * 
	 * @param selectionSynchronizer
	 */
	public void setSelectionSynchronizer(
			SelectionSynchronizer selectionSynchronizer) {
		this.selectionSynchronizer = selectionSynchronizer;
	}

	/**
	 * @return returns selection synchronizer
	 */
	public SelectionSynchronizer getSelectionSynchronizer() {
		return selectionSynchronizer;
	}

	/**
	 * initializator
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		ActionRegistry registry = getActionRegistry();
		if (registry == null) {
			return;
		}
		IActionBars bars = pageSite.getActionBars();
		String id = ActionFactory.UNDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ActionFactory.REDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ActionFactory.REFRESH.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ActionFactory.PRINT.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = AutoLayoutAction.ACTION_ID;
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ToggleConnectionsAction.ACTION_ID;
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ToggleShapeExpandStateAction.ACTION_ID;
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ToggleShapeVisibleStateAction.ACTION_ID;
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = LexicalSortingAction.ACTION_ID;
		bars.setGlobalActionHandler(id, registry.getAction(id));
		bars.updateActionBars();
	}

	/**
	 * Outline viewer configuration
	 * 
	 */
	protected void configureOutlineViewer() {
		getViewer().setEditDomain(editor.getDefaultEditDomain());
		getViewer().setEditPartFactory(new TreePartFactory());
		ContextMenuProvider provider = new PopupMenuProvider(getViewer(), getActionRegistry());
		getViewer().setContextMenu(provider);
		getSite().registerContextMenu(
			"org.jboss.tools.hibernate.ui.diagram.editors.popup.outline.contextmenu", //$NON-NLS-1$
			provider, getSite().getSelectionProvider());
		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		
		tbm.add(editor.getLexicalSortingAction());

		showOutlineAction = new Action() {
			public void run() {
				showPage(ID_OUTLINE);
			}
		};
		showOutlineAction.setToolTipText(DiagramViewerMessages.DiagramContentOutlinePage_Outline);
		showOutlineAction.setImageDescriptor(ImageDescriptor.createFromFile(
				DiagramViewer.class, "icons/outline.gif")); //$NON-NLS-1$
		tbm.add(showOutlineAction);
		showOverviewAction = new Action() {
			public void run() {
				showPage(ID_OVERVIEW);
			}
		};
		showOverviewAction.setToolTipText(DiagramViewerMessages.DiagramContentOutlinePage_Overview);
		showOverviewAction.setImageDescriptor(ImageDescriptor.createFromFile(
				DiagramViewer.class, "icons/overview.gif")); //$NON-NLS-1$
		tbm.add(showOverviewAction);
		showPage(ID_OVERVIEW);
	}

	
	/**
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		outline = getViewer().createControl(pageBook);
		overview = new Canvas(pageBook, SWT.NONE);
		pageBook.showPage(outline);
		configureOutlineViewer();
		hookOutlineViewer();
		initializeOutlineViewer();
	}

	
	/**
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		unhookOutlineViewer();
		if (thumbnail != null) {
			thumbnail.deactivate();
			thumbnail = null;
		}
		super.dispose();
	}

	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
		}
		return null;
	}

	
	/**
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return pageBook;
	}

	/**
	 * 
	 */
	protected void hookOutlineViewer() {
		getSelectionSynchronizer().addViewer(getViewer());
	}

	/**
	 * 
	 */
	protected void initializeOutlineViewer() {
		setContents(getOrmDiagram());
	}

	/**
	 * 
	 */
	protected void initializeOverview() {
		LightweightSystem lws = new LightweightSystem(overview);
		RootEditPart rep = getGraphicalViewer().getRootEditPart();

		if (rep instanceof ScalableFreeformRootEditPart) {
			ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) rep;
			if (this.thumbnail != null) {
				this.thumbnail.deactivate();
			}
			thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
			thumbnail.setBorder(new MarginBorder(3));
			thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
			lws.setContents(thumbnail);
		}
	}

	public void setContents(Object contents) {
		getViewer().setContents(contents);
	}

	/**
	 * @param id
	 */
	protected void showPage(int id) {

		if (id == ID_OUTLINE) {
			showOutlineAction.setChecked(true);
			showOverviewAction.setChecked(false);
			pageBook.showPage(outline);
			if (thumbnail != null) {
				thumbnail.setVisible(false);
			}
		} else if (id == ID_OVERVIEW) {
			if (thumbnail == null) {
				initializeOverview();
			}
			showOutlineAction.setChecked(false);
			showOverviewAction.setChecked(true);
			pageBook.showPage(overview);
			thumbnail.setVisible(true);
		}

	}

	/**
	 * 
	 */
	protected void unhookOutlineViewer() {
		getSelectionSynchronizer().removeViewer(getViewer());
	}
	
	Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		super.addSelectionChangedListener(listener);
		listeners.add(listener);
	}
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		super.removeSelectionChangedListener(listener);
		listeners.remove(listener);
	}
	
	void replaceSelectionChangedListeners(GraphicalViewer graphicalViewer) {
		for (ISelectionChangedListener l : listeners) {
			getViewer().removeSelectionChangedListener(l);
			graphicalViewer.addSelectionChangedListener(l);
		}
	}

	/**
	 * @return the ormDiagram
	 */
	public OrmDiagram getOrmDiagram() {
		return ormDiagram;
	}

	/**
	 * @param ormDiagram the ormDiagram to set
	 */
	public void setOrmDiagram(OrmDiagram ormDiagram) {
		this.ormDiagram = ormDiagram;
	}
	
	public Control getOutline() {
		return outline;
	}
	
	public DiagramViewer getEditor() {
		return editor;
	}

	public void setEditor(DiagramViewer editor) {
		this.editor = editor;
	}

	protected ActionRegistry getActionRegistry() {
		return actionRegistry;
	}
}
