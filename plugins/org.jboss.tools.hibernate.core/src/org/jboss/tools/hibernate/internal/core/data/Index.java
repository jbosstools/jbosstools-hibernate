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
package org.jboss.tools.hibernate.internal.core.data;


import org.jboss.tools.hibernate.core.IDatabaseTableIndex;

/**
 * @author alex
 *
 * A relational table index
 */
public class Index extends Constraint implements IDatabaseTableIndex {
	private static final long serialVersionUID = 1L;
	private boolean unique;
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTableIndex#isUnique()
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
}
