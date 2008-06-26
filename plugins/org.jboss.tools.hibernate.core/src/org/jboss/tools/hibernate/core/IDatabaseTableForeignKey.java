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
package org.jboss.tools.hibernate.core;

import java.util.Iterator;

/**
 * @author alex
 *
 * A foreign key constraint
 */
public interface IDatabaseTableForeignKey extends IDatabaseConstraint {
	public IDatabaseTable getReferencedTable();
	public boolean isCascadeDeleteEnabled();
	// added by Nick 28.07.2005
	public Iterator<IDatabaseColumn> getOrderedColumnIterator();
    // by Nick
}
