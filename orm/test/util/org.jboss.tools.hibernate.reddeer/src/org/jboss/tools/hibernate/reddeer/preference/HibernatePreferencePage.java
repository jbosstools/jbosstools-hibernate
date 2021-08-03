/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.preference;

import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.swt.api.TableItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.jboss.tools.common.reddeer.preferences.SourceLookupPreferencePage;

public class HibernatePreferencePage extends PreferencePage  {
	
	protected final static Logger log = Logger.getLogger(SourceLookupPreferencePage.class);
	
	public HibernatePreferencePage(ReferencedComposite composite) {
		super(composite, "JBoss Tools", "Hibernate");
	}
	
	public void enableAllRuntimes() {
		DefaultTable fileTypesTable = new DefaultTable(this);
		List<TableItem> fileTypeItems = fileTypesTable.getItems();
		for (TableItem tableItem : fileTypeItems) {
			tableItem.setChecked(true);
		}
		
	}
	
}