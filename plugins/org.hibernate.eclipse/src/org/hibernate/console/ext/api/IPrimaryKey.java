/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console.ext.api;

import java.util.List;

/**
 * @author Dmitry Geraskov {geraskov@gmail.com}
 *
 */
public interface IPrimaryKey {

	boolean containsColumn(IColumn col);

	int getColumnSpan();

	IColumn getColumn(int i);

	List<IColumn> getColumns();

	ITable getTable();

}
