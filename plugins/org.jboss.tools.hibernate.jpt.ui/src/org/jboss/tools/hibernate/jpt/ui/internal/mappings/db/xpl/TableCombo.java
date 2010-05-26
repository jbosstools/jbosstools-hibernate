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
import org.eclipse.jpt.db.Schema;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.iterators.EmptyIterator;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;

/**
 * This combo-box displays a schema's tables.
 */
public abstract class TableCombo<T extends JpaNode>
	extends DatabaseObjectCombo<T>
{
	public TableCombo(Pane<? extends T> parentPane, Composite parent) {
		super(parentPane, parent);
	}

	public TableCombo(
						Pane<?> parentPane,
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent
	) {
		super(parentPane, subjectHolder, parent);
	}

	public TableCombo(
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent,
						WidgetFactory widgetFactory
	) {
		super(subjectHolder, parent, widgetFactory);
	}

	@Override
	protected Iterator<String> values() {
		Schema dbSchema = this.getDbSchema();
		return (dbSchema == null) ? EmptyIterator.<String>instance() : dbSchema.sortedTableIdentifiers();
	}

	protected Schema getDbSchema() {
		return (this.getSubject() == null) ? null : this.getDbSchema_();
	}

	/**
	 * Assume the subject is not null.
	 */
	protected abstract Schema getDbSchema_();

}
