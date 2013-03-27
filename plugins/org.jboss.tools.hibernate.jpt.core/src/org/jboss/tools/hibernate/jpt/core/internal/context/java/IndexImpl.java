/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ArrayList;

import java.util.List;

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaContextModel;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class IndexImpl extends AbstractJavaContextModel<JpaContextModel> implements JavaIndex {
	
	private IndexAnnotation annotation;
	
	private String name;
	
	private String[] columnNames = new String[0];

	public IndexImpl(JpaContextModel parent, IndexAnnotation annotation) {
		super(parent);
		this.annotation = annotation;
		this.name = annotation.getName();
		this.columnNames = annotation.getColumnNames();
	}

	@Override
	public void synchronizeWithResourceModel() {
		this.setName_(annotation.getName());
		this.setColumnNames_(annotation.getColumnNames());
	}

	// ***** name
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.getIndexAnnotation().setName(name);
		this.firePropertyChanged(INDEX_NAME, old, name);
	}
	
	public void setName_(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(INDEX_NAME, old, name);
	}
	
	// ***** columnNames
	
	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		if (columnNames == null) columnNames = new String[0];
		String[] old = this.columnNames;
		this.columnNames = columnNames;
		this.getIndexAnnotation().setColumnNames(columnNames);
		this.firePropertyChanged(INDEX_COLUMN_NAMES, old, columnNames);
	}
	
	public void setColumnNames_(String[] columnNames) {
		String[] old = this.columnNames;
		this.columnNames = columnNames;
		this.firePropertyChanged(INDEX_COLUMN_NAMES, old, columnNames);
	}
	
	@Override
	public IndexAnnotation getIndexAnnotation() {
		return annotation;
	}

	public TextRange getValidationTextRange() {
		return this.annotation.getTextRange();
	}

	public void addColumn(String columnName) {
		String[] old = this.columnNames;
		String[] newColumns = new String[old.length + 1];
		System.arraycopy(old, 0, newColumns, 0, old.length);
		newColumns[newColumns.length - 1] = columnName;
		this.setColumnNames(newColumns);
	}

	public void removeColumn(String columnName) {
		String[] old = this.columnNames;
		List<String> newColumns = new ArrayList<String>();
		for (String column : old) {
			if (!column.equals(columnName)) newColumns.add(column);
		}
		this.setColumnNames(newColumns.toArray(new String[newColumns.size()]));
	}	

}
