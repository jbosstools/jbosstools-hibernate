/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.console.views;

import java.util.List;

import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;


/**
 * Hibernate Query Result View RedDeer implementation
 * @author Jiri Peterka
 *
 */
public class QueryPageTabView extends WorkbenchView {

	/**
	 * Initialization and lookup for Hibernate Query Result View
	 */
	public QueryPageTabView() {
		super("Hibernate Query Result");
	}
	
	
	/**
	 * Return Hibernate Query Result table items
	 */
	public List<TableItem> getResultItems() {
		return new DefaultTable().getItems();
	}

}
