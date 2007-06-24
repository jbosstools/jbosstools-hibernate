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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 29, 2005
 */
public class ReversStatistic {
	private int tablesNumber;
	private int foundCUNumber;
	private int skippedTablesNumber; 
	private int linkTablesNumber;
	/**
	 * @return Returns the foundCUNumber.
	 */
	public int getFoundCUNumber() {
		return foundCUNumber;
	}
	/**
	 * @param foundCUNumber The foundCUNumber to set.
	 */
	public void setFoundCUNumber(int foundCUNumber) {
		this.foundCUNumber = foundCUNumber;
	}
	/**
	 * @return Returns the linkTablesNumber.
	 */
	public int getLinkTablesNumber() {
		return linkTablesNumber;
	}
	/**
	 * @param linkTablesNumber The linkTablesNumber to set.
	 */
	public void setLinkTablesNumber(int linkTablesNumber) {
		this.linkTablesNumber = linkTablesNumber;
	}
	/**
	 * @return Returns the skippedTablesNumber.
	 */
	public int getSkippedTablesNumber() {
		return skippedTablesNumber;
	}
	/**
	 * @param skippedTablesNumber The skippedTablesNumber to set.
	 */
	public void setSkippedTablesNumber(int skippedTablesNumber) {
		this.skippedTablesNumber = skippedTablesNumber;
	}
	/**
	 * @return Returns the tablesNumber.
	 */
	public int getTablesNumber() {
		return tablesNumber;
	}
	/**
	 * @param tablesNumber The tablesNumber to set.
	 */
	public void setTablesNumber(int tablesNumber) {
		this.tablesNumber = tablesNumber;
	}
	
	public String[] getAllResults()
	{
		String[] res={
				(new Integer(tablesNumber-skippedTablesNumber-linkTablesNumber)).toString(),
				(new Integer(skippedTablesNumber)).toString(),
				(new Integer(linkTablesNumber)).toString()
				}; 

		return res;
	}
}
