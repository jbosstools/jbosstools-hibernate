 /*******************************************************************************
  * Copyright (c) 2017 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.condition;

import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.swt.api.Tree;
import org.jboss.reddeer.swt.api.TreeItem;

/**
 * Waits until hibernate configurations are loaded
 * @author rawagner
 *
 */
public class ConfigrationsAreLoaded extends AbstractWaitCondition{
	
	private Tree tree;
	
	public ConfigrationsAreLoaded(Tree tree) {
		this.tree = tree;
	}

	@Override
	public boolean test() {
		for(TreeItem item: tree.getItems()) {
			try {
				if(item.getText().contains("Pending")) {
					return false;
				}
			} catch (RedDeerException e) {
				if(item.isDisposed()) {
					return false;
				}
				throw e;
			}
		}
		return true;
	}

}
