/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.utility.model.event.ListChangeEvent;
import org.eclipse.jpt.utility.model.listener.ListChangeListener;

/**
 * @author Dmitry Geraskov
 *
 */
public class PersistenceUnitPropertyListListener implements ListChangeListener
{
	private PersistenceUnitProperties parent;

	// ********** constructors / initialization **********
	public PersistenceUnitPropertyListListener(PersistenceUnitProperties parent) {
		this.parent = parent;
	}

	// ********** ListChangeListener implementation **********
	public void itemsAdded(ListChangeEvent e) {
		model().updateProperties();
	}

	public void itemsRemoved(ListChangeEvent e) {
		model().updateProperties();
	}

	public void itemsReplaced(ListChangeEvent e) {
		model().updateProperties();
	}

	public void itemsMoved(ListChangeEvent e) {
		model().updateProperties();
	}

	public void listCleared(ListChangeEvent e) {
		model().updateProperties();
	}

	public void listChanged(ListChangeEvent e) {
		model().updateProperties();
	}

	// ********** internal methods **********
	private PersistenceUnitProperties model() {
		return this.parent;
	}

}