/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.parts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.figures.TableFigure;
import org.hibernate.eclipse.graph.model.ColumnViewAdapter;
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
	
	protected List<ColumnViewAdapter> getModelChildren() {
		TableViewAdapter tableViewAdapter = getTableViewAdapter();
		return tableViewAdapter.getColumns();		
	}

}
