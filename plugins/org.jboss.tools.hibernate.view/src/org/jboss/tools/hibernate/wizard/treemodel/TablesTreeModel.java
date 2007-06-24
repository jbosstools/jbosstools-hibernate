/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.treemodel;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 14, 2005
 */
public class TablesTreeModel extends TreeModel{
   private boolean isview;

    public TablesTreeModel(String name, TablesTreeModel root, boolean isview)
    {
    	super(name,root);
    	this.isview=isview;
    }

    public void addNode(String nodeName,boolean isview)
    {
    	addNode(new TablesTreeModel(nodeName,this,isview));
    }
    
	/**
	 * @return Returns the isview.
	 */
	public boolean isIsview() {
		return isview;
	}

	/**
	 * @param isview The isview to set.
	 */
	public void setIsview(boolean isview) {
		this.isview = isview;
	}
}
