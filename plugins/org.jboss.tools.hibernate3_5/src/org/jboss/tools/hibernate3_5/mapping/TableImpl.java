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
package org.jboss.tools.hibernate3_5.mapping;

import org.hibernate.console.ext.api.ITable;
import org.hibernate.mapping.Table;

/**
 * @author Dmitry Geraskov {geraskov@gmail.com}
 *
 */
public class TableImpl implements ITable {
	
	private Table table;
	
	public TableImpl(Table table){
		this.table = table;
	}

	@Override
	public String getName() {
		return table.getName();
	}

	@Override
	public String getSchema() {
		return table.getSchema();
	}

	@Override
	public String getCatalog() {
		return table.getCatalog();
	}

}
