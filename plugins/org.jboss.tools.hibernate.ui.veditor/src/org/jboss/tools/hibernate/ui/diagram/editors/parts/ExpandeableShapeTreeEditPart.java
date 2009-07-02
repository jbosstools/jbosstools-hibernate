package org.jboss.tools.hibernate.ui.veditor.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Shape;
import org.jboss.tools.hibernate.ui.view.views.OrmLabelProvider;
import org.jboss.tools.hibernate.ui.view.views.OrmModelImageVisitor;
import org.jboss.tools.hibernate.ui.view.views.OrmModelNameVisitor;

public class ExpandeableShapeTreeEditPart extends
		org.eclipse.gef.editparts.AbstractTreeEditPart implements
		PropertyChangeListener {

	static final protected OrmLabelProvider ormLabelProvider = new OrmLabelProvider(
			new OrmModelImageVisitor(), new OrmModelNameVisitor());

	/**
	 * Constructor initializes this with the given model.
	 * 
	 * @param model
	 *            Model for this.
	 */
	public ExpandeableShapeTreeEditPart(Object model) {
		super(model);
	}

	/**
	 * Returns the model of this as a ExpandeableShape.
	 * 
	 * @return Model of this.
	 */
	protected ExpandeableShape getExpandeableShape() {
		return (ExpandeableShape) getModel();
	}

	/**
	 * Returns <code>null</code> as a Tree EditPart holds no children under it.
	 * @return <code>null</code>
	 */
	@Override
	protected List<Shape> getModelChildren() {
		List<Shape> res = new ArrayList<Shape>();
		Iterator<Shape> it = getExpandeableShape().getChildrenIterator();
		while (it.hasNext()) {
			res.add(it.next());
		}
		return res;
	}

	public void propertyChange(PropertyChangeEvent change) {
		refreshVisuals();
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
		ConsoleConfiguration cfg = model.getOrmDiagram().getConsoleConfig();

		setWidgetImage(ormLabelProvider.getImage(element));
		setWidgetText(ormLabelProvider.getText(element, cfg));
	}

}
