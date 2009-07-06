package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;

public class DiagramTreeEditPart extends org.eclipse.gef.editparts.AbstractTreeEditPart
	implements PropertyChangeListener
{

	/**
	 * Constructor initializes this with the given model.
	 *
	 * @param model  Model for this.
	 */
	public DiagramTreeEditPart(OrmDiagram model) {
		super(model);
	}

	/**
	 * Returns the model of this as a OrmDiagram.
	 *
	 * @return  Model of this.
	 */
	protected OrmDiagram getOrmDiagram() {
		return (OrmDiagram)getModel();
	}

	/**
	 * Returns <code>null</code> as a Tree EditPart holds no children under it.
	 * @return <code>null</code>
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

	public void propertyChange(PropertyChangeEvent change){
		refreshVisuals();
	}

	/**
	 * Refreshes the visual properties of the TreeItem for this part.
	 */
	protected void refreshVisuals() {
		if (getWidget() instanceof Tree) {
			return;
		}
	}
	
}
