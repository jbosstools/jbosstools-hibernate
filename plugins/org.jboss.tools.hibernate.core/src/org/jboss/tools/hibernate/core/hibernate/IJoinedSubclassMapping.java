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
package org.jboss.tools.hibernate.core.hibernate;

import org.jboss.tools.hibernate.core.IDatabaseTable;

/**
 * @author alex
 *
 */
public interface IJoinedSubclassMapping extends ISubclassMapping {
	public void setDatabaseTable(IDatabaseTable table);
	public IDatabaseTable getDatabaseTable();
	public IHibernateKeyMapping getKey();
	public void setKey(IHibernateKeyMapping key);

}