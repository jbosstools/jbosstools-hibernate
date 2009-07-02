package org.jboss.tools.hibernate.ui.veditor.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.hibernate.ui.veditor.UIVEditorMessages;
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
		throw new RuntimeException(UIVEditorMessages.PartFactory_canot_create_part_for_model_element
				+ ((modelElement != null) ? modelElement.getClass().getName()
						: UIVEditorMessages.PartFactory_null));
	}

}
