package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

public class TreePartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object modelElement) {
		if (modelElement instanceof OrmDiagram) {
			return new DiagramTreeEditPart((OrmDiagram)modelElement);
		}
		if (modelElement instanceof ExpandeableShape) {
			return new ExpandeableShapeTreeEditPart((ExpandeableShape)modelElement);
		}
		if (modelElement instanceof Shape) {
			return new ShapeTreeEditPart((Shape)modelElement);
		}
		throw new RuntimeException(DiagramViewerMessages.PartFactory_canot_create_part_for_model_element
				+ ((modelElement != null) ? modelElement.getClass().getName()
						: DiagramViewerMessages.PartFactory_null));
	}

}
