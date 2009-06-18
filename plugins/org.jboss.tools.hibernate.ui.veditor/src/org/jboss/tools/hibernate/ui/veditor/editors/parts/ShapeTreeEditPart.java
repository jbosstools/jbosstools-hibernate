package org.jboss.tools.hibernate.ui.veditor.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.ui.veditor.editors.model.ExpandeableShape;
import org.jboss.tools.hibernate.ui.veditor.editors.model.Shape;
import org.jboss.tools.hibernate.ui.view.views.OrmLabelProvider;
import org.jboss.tools.hibernate.ui.view.views.OrmModelImageVisitor;
import org.jboss.tools.hibernate.ui.view.views.OrmModelNameVisitor;

public class ShapeTreeEditPart extends
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
	public ShapeTreeEditPart(Object model) {
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
	 * Returns <code>null</code> as a Tree EditPart holds no children under
	 * it.
	 * 
	 * @return <code>null</code>
	 */
	protected List getModelChildren() {
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
		ConsoleConfiguration cfg = model.getOrmDiagram().getConsoleConfig();

		setWidgetImage(ormLabelProvider.getImage(element));
		setWidgetText(ormLabelProvider.getText(element, cfg));
	}

}
