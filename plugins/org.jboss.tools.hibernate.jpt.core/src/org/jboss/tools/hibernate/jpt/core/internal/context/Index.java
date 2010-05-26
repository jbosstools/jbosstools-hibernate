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

import org.eclipse.jpt.core.context.JpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.IndexAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface Index extends JpaContextNode {
	
	String getName();
	void setName(String name);
		String INDEX_NAME = "name"; //$NON-NLS-1$
	
	String[] getColumnNames();
	void addColumn(String columnName);
	void removeColumn(String columnName);
		String INDEX_COLUMN_NAMES = "ColumnNames"; //$NON-NLS-1$


	public void initialize(IndexAnnotation indexResource);

	public void update(IndexAnnotation indexResource);

}
