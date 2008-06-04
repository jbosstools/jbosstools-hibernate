package org.jboss.tools.hibernate.ui.veditor.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Shape;

public class TreePartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object modelElement) {
		if (modelElement instanceof OrmDiagram) {
			return new DiagramTreeEditPart(modelElement);
		}
		if (modelElement instanceof ExpandeableShape) {
			return new ExpandeableShapeTreeEditPart(modelElement);
		}
		if (modelElement instanceof Shape) {
			return new ShapeTreeEditPart(modelElement);
		}
		throw new RuntimeException(Messages.TREEPARTFACTORY_CANOT_CREATE_PART_FOR_MODEL_ELEMENT
				+ ((modelElement != null) ? modelElement.getClass().getName()
						: Messages.TREEPARTFACTORY_NULL));
	}

}
