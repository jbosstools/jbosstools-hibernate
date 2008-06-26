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

/**
 * @author alex
 *
 * A relational constraint.
 */
public interface IDatabaseConstraint extends IOrmElement, /* by Nick 14.04.2005 */IDatabaseTableColumnSet /**/ {
//	public Iterator getColumnIterator();
//	public int getColumnSpan();
//  public IDatabaseTable getTable();
    public void addColumn(IDatabaseColumn column);
	public void setTable(IDatabaseTable table);
	public boolean containsColumn(String columnName); //by Nick 22.04.2005
	public IDatabaseColumn removeColumn(String columnName);
	public void clear();
	// added by Nick 04.07.2005
	public boolean isUnique();
    // by Nick
    // added by Nick 28.07.2005
    public int getColumnIndex(IDatabaseColumn column);
	// by Nick
}
