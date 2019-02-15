/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXml;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.persistence.GenericPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistence extends GenericPersistence {

	/**
	 * @param parent
	 * @param xmlPersistence
	 */
	public HibernatePersistence(PersistenceXml parent,
			XmlPersistence xmlPersistence) {
		super(parent, xmlPersistence);
	}

}
