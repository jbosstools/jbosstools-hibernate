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
package org.jboss.tools.hibernate.reddeer.jdt.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.table.DefaultTable;

/**
 * New Hibernate Mapping File Page
 * Here is summary what hbm.xml file will be created
 * @author jpeterka
 *
 */
public class NewHibernateMappingFilePage extends WizardPage {
	
	/**
	 * Select class for further hbm.xml generation
	 * @param clazz class name
	 */
	public void selectClasses(String clazz) {
		int headerIndex = new DefaultTable().getHeaderIndex("Class name");
		TableItem item = new DefaultTable().getItem(clazz,headerIndex);
		item.select();
	}
	
	public List<String> getClasses(){
		List<String> classes = new ArrayList<>();
		for(TableItem i: new DefaultTable().getItems()){
			classes.add(i.getText());
		}
		return classes;
	}
}
