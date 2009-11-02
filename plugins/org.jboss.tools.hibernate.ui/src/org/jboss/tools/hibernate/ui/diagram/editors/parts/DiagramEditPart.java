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
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.rulers.RulerProvider;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IDiagramInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.data.DiagramInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl.AutoLayoutImpl;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
public class DiagramEditPart extends OrmEditPart {

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, 
			new ShapesXYLayoutEditPolicy((XYLayout)getContentPane().getLayoutManager()));
	}

	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());
		if (isManhattanConnectionRouter()) {
			getOrmDiagram().setupFanConnectionRouter();
			setupManhattanConnectionRouter();
		} else {
			getOrmDiagram().setupManhattanConnectionRouter();
			setupFanConnectionRouter();
		}
		return f;
	}

	public void setupManhattanConnectionRouter() {
		if (!isManhattanConnectionRouter()) {
			ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
			connLayer.setConnectionRouter(new ManhattanConnectionRouter());
			getOrmDiagram().setupManhattanConnectionRouter();
		}
	}

	public void setupFanConnectionRouter() {
		if (!isFanConnectionRouter()) {
			ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
			connLayer.setConnectionRouter(new FanRouter());
			getOrmDiagram().setupFanConnectionRouter();
		}
	}

	public boolean isManhattanConnectionRouter() {
		return getOrmDiagram().isManhattanConnectionRouter();
	}

	public boolean isFanConnectionRouter() {
		return getOrmDiagram().isFanConnectionRouter();
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (OrmDiagram.AUTOLAYOUT.equals(prop)) {
			refresh();
			autolayout();
		} else if (BaseElement.REFRESH.equals(prop)) {
			refresh();
		} else if (OrmDiagram.DIRTY.equals(prop)) {
			((DiagramViewer) ((DefaultEditDomain) getViewer().getEditDomain())
					.getEditorPart()).refreshDirty();
		}
		refresh();
	}

	/**
	 * Returns a <code>List</code> containing the children model objects.
	 * @return the List of children
	 */
	@Override
	protected List<Shape> getModelChildren() {
		List<Shape> res = new ArrayList<Shape>();
		Iterator<Shape> it = getOrmDiagram().getChildrenIterator();
		while (it.hasNext()) {
			res.add(it.next());
		}
		return res;
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((BaseElement) getModel()).addPropertyChangeListener(this);
			if (!getOrmDiagram().isFileLoadSuccessfull()) {
				refresh();
				autolayout();
				refresh();
				getOrmDiagram().setDirty(false);
			}
		}
	}

	public void autolayout() {
		IDiagramInfo process = new DiagramInfo(getViewer(), getOrmDiagram());
		AutoLayoutImpl layout = new AutoLayoutImpl();
		layout.setGridStep(5);
		layout.setOverride(true);
		layout.setProcess(process);
	}

	public void setToFront(EditPart ep) {
		int index = getChildren().indexOf(ep);
		if (index == -1) {
			return;
		}
		if (index != getChildren().size() - 1) {
			reorderChild(ep, getChildren().size() - 1);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((BaseElement) getModel()).removePropertyChangeListener(this);
		}
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List<SnapToHelper> snapStrategies = new ArrayList<SnapToHelper>();
			Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides(this));
			}
			val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry(this));
			}
			val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid(this));
			}
			if (snapStrategies.size() == 0) {
				return null;
			}
			if (snapStrategies.size() == 1) {
				return snapStrategies.get(0);
			}
			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++) {
				ss[i] = (SnapToHelper)snapStrategies.get(i);
			}
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}
}
