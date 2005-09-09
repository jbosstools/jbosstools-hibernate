package org.hibernate.eclipse.graph.parts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.figures.TableFigure;
import org.hibernate.eclipse.graph.model.TableViewAdapter;

public class TableEditPart extends GraphNodeEditPart {

	public TableEditPart(TableViewAdapter tva) {
		setModel(tva);
	}
	
	protected IFigure createFigure() {
		String unqualify = getTableViewAdapter().getTable().getName();
		return new TableFigure(new EditableLabel(unqualify));
	}

	private TableViewAdapter getTableViewAdapter() {
		return ((TableViewAdapter)getModel());
	}

	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) getFigure();
		return figure.getColumnsFigure();
	}
	
	protected List getModelChildren() {
		TableViewAdapter tableViewAdapter = getTableViewAdapter();
		return tableViewAdapter.getColumns();		
	}

}
