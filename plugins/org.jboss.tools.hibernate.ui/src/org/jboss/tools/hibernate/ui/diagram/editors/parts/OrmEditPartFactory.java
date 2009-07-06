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
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ComponentShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;


public class OrmEditPartFactory implements EditPartFactory {


	public EditPart createEditPart(EditPart context, Object modelElement) {
		EditPart part = getPartForElement(modelElement);
		part.setModel(modelElement);
		return part;
	}

	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof OrmDiagram) {
			return new DiagramEditPart();
		}
		if (modelElement instanceof OrmShape) {
			return new OrmShapeEditPart();
		}
		if (modelElement instanceof ComponentShape) {
			return new ComponentShapeEditPart();
		}
		if (modelElement instanceof ExpandeableShape) {
			return new ExpandeableShapeEditPart();
		}
		if (modelElement instanceof Shape) {
			return new ShapeEditPart();
		}
		if (modelElement instanceof Connection) {
			return new ConnectionEditPart();
		}
		throw new RuntimeException(
				DiagramViewerMessages.PartFactory_canot_create_part_for_model_element
				+ ((modelElement != null) ? modelElement.getClass().getName() : DiagramViewerMessages.PartFactory_null));
	}

}