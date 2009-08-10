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
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 */
public class ShapeTreeEditPart extends
		org.eclipse.gef.editparts.AbstractTreeEditPart implements
		PropertyChangeListener {

	/**
	 * Constructor initializes this with the given model.
	 * 
	 * @param model
	 *            Model for this.
	 */
	public ShapeTreeEditPart(Shape model) {
		super(model);
	}

	/**
	 * Returns the model of this as a ExpandeableShape.
	 * 
	 * @return Model of this.
	 */
	protected Shape getShape() {
		return (Shape) getModel();
	}

	/**
	 * Returns <code>null</code> as a Tree EditPart holds no children under
	 * it.
	 * 
	 * @return <code>null</code>
	 */
	protected List<?> getModelChildren() {
		return Collections.EMPTY_LIST;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
	}

	/**
	 * Refreshes the visual properties of the TreeItem for this part.
	 */
	protected void refreshVisuals() {
		if (getWidget() instanceof Tree) {
			return;
		}
		Shape model = (Shape) getModel();
		Object element = model.getOrmElement();
		setWidgetImage(getShape().getOrmDiagram().getLabelProvider().getImage(element));
		setWidgetText(getShape().getOrmDiagram().getLabelProvider().getText(element));
	}

}
