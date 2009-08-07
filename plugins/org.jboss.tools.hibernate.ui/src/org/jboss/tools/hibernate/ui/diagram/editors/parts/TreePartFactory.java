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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

/**
 * @author some modifications from Vitali
 * @see org.eclipse.gef.EditPartFactory
 */
public class TreePartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object baseElement) {
		EditPart res = null;
		if (baseElement instanceof OrmDiagram) {
			res = new DiagramTreeEditPart((OrmDiagram)baseElement);
		} else if (baseElement instanceof ExpandableShape) {
			res = new ExpandableShapeTreeEditPart((ExpandableShape)baseElement);
		} else if (baseElement instanceof Shape) {
			res = new ShapeTreeEditPart((Shape)baseElement);
		}
		if (res == null) {
			throw new RuntimeException(DiagramViewerMessages.PartFactory_canot_create_part_for_model_element
				+ ((baseElement != null) ? baseElement.getClass().getName()
						: DiagramViewerMessages.PartFactory_null));
		}
		return res;
	}

}
