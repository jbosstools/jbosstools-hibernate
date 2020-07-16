/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import org.eclipse.jpt.jpa.core.resource.persistence.JPA;

/**
 * @author Koen Aers, jkopriva@redhat.com
 *
 */
@SuppressWarnings("nls")
public interface JPA2_2
	extends JPA
{
	String SCHEMA_NAMESPACE = JPA.SCHEMA_NAMESPACE;
	String SCHEMA_LOCATION = "http://java.sun.com/xml/ns/persistence/persistence_2_2.xsd";
	String SCHEMA_VERSION = "2.2";
	
}
