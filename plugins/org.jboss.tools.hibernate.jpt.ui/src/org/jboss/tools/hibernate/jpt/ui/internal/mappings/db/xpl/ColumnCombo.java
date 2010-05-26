/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl;

import java.util.Iterator;
import org.eclipse.jpt.core.JpaNode;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;

/**
 * This combo-box displays a table's columns.
 */
public abstract class ColumnCombo<T extends JpaNode>
	extends DatabaseObjectCombo<T>
{
	public ColumnCombo(Pane<? extends T> parentPane, Composite parent) {
		super(parentPane, parent);
	}

	public ColumnCombo(
						Pane<?> parentPane,
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent
	) {
		super(parentPane, subjectHolder, parent);
	}

	public ColumnCombo(
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent,
						WidgetFactory widgetFactory
	) {
		super(subjectHolder, parent, widgetFactory);
	}

	@Override
	protected Iterator<String> values() {
		Table dbTable = this.getDbTable();
		return (dbTable == null) ? EmptyIterator.<String>instance() : dbTable.sortedColumnIdentifiers();
	}

	protected Table getDbTable() {
		return (this.getSubject() == null) ? null : this.getDbTable_();
	}

	/**
	 * Assume the subject is not null.
	 */
	protected abstract Table getDbTable_();

	@Override
	protected void tableChanged_(Table table) {
		super.tableChanged_(table);
		if (this.getDbTable() == table) {
			this.doPopulate();
		}
	}

}
