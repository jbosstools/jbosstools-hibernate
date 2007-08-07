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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.parts.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.*;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;


public class DiagramContentOutlinePage extends ContentOutlinePage implements
		IAdaptable {
	private GraphicalViewer graphicalViewer;

	private SelectionSynchronizer selectionSynchronizer;

	private PageBook pageBook;

	/*
	 * surface for drawing 
	 */
	private Canvas overview;

	static final int ID_OUTLINE = 0;

	static final int ID_OVERVIEW = 1;

	private Thumbnail thumbnail;

	IPageSite pSite;

	/**
	 * The constructor
	 * 
	 * @param viewer
	 */
	public DiagramContentOutlinePage(EditPartViewer viewer) {
		super(viewer);
	}

	/**
	 * Sets graphical viewer
	 * 
	 * @param graphicalViewer
	 */
	public void setGraphicalViewer(GraphicalViewer graphicalViewer) {
		this.graphicalViewer = graphicalViewer;
	}
    
    public void update(GraphicalViewer graphicalViewer){
        if(this.graphicalViewer != null){
            if(this.graphicalViewer != graphicalViewer){
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
		this.pSite = pageSite;
	}

	/**
	 * Outline viewer configuration
	 * 
	 */
	protected void configureOutlineViewer() {
		showPage(ID_OUTLINE);
	}

	
	/**
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);

		overview = new Canvas(pageBook, SWT.NONE);
		pageBook.showPage(overview);
		configureOutlineViewer();
		hookOutlineViewer();
		initializeOutlineViewer();
	}

	
	/**
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		unhookOutlineViewer();
		if (thumbnail != null)
			thumbnail.deactivate();
		super.dispose();
	}

	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
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
	}

	
	/**
	 * 
	 */
	protected void initializeOverview() {
		LightweightSystem lws = new LightweightSystem(overview);
		RootEditPart rep = getGraphicalViewer().getRootEditPart();

		if (rep instanceof ScalableFreeformRootEditPart) {
			ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) rep;
			if(this.thumbnail != null) {
				this.thumbnail.deactivate();
			}
			thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
			thumbnail.setBorder(new MarginBorder(3));
			thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
			lws.setContents(thumbnail);
		}
	}

	/**
	 * @param id
	 */
	protected void showPage(int id) {

		if (thumbnail == null)
			initializeOverview();
		pageBook.showPage(overview);
		thumbnail.setVisible(true);

	}

	/**
	 * 
	 */
	protected void unhookOutlineViewer() {
		getSelectionSynchronizer().removeViewer(getViewer());
	}
	
	Set listeners = new HashSet();

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		super.addSelectionChangedListener(listener);
		listeners.add(listener);
	}
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		super.removeSelectionChangedListener(listener);
		listeners.remove(listener);
	}
	
	void replaceSelectionChangedListeners(GraphicalViewer graphicalViewer) {
		Iterator it = listeners.iterator();
		while(it.hasNext()) {
			ISelectionChangedListener l = (ISelectionChangedListener)it.next();
			getViewer().removeSelectionChangedListener(l);
			graphicalViewer.addSelectionChangedListener(l);
		}
	}

}
