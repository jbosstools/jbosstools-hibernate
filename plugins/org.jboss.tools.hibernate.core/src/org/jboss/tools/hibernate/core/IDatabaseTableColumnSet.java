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
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 14.07.2005
 * 
 */
public interface IDatabaseTableColumnSet {
    public Iterator<IDatabaseColumn> getColumnIterator();
    public Iterator<IDatabaseColumn> getOrderedColumnIterator();
    public int getColumnSpan();
    public IDatabaseTable getTable();
    public String getName();
    public boolean includes(IDatabaseTableColumnSet otherSet);
    public boolean containsMappedColumns();
    public boolean intersects(IDatabaseTableColumnSet otherSet);
    public boolean equals(IDatabaseTableColumnSet otherSet);
}
