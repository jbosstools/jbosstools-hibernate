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

package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class IndexImpl extends AbstractJavaJpaContextNode implements Index {
	
	private IndexAnnotation indexResource;
	
	private String name;
	
	private String[] columnNames = new String[0];

	public IndexImpl(JavaJpaContextNode parent) {
		super(parent);
	}

	public void initialize(IndexAnnotation indexResource) {
		this.indexResource = indexResource;
		this.name = indexResource.getName();
		this.columnNames = indexResource.getColumnNames();
	}
	
	public void update(IndexAnnotation indexResource) {
		this.indexResource = indexResource;
		this.setName_(indexResource.getName());
		this.setColumnNames_(indexResource.getColumnNames());
	}

	// ***** name
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.getResourceIndex().setName(name);
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
		this.getResourceIndex().setColumnNames(columnNames);
		this.firePropertyChanged(INDEX_COLUMN_NAMES, old, columnNames);
	}
	
	public void setColumnNames_(String[] columnNames) {
		String[] old = this.columnNames;
		this.columnNames = columnNames;
		this.firePropertyChanged(INDEX_COLUMN_NAMES, old, columnNames);
	}
	
	private IndexAnnotation getResourceIndex() {
		return indexResource;
	}

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.indexResource.getTextRange(astRoot);
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
