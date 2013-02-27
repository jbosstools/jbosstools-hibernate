/*******************************************************************************
  * Copyright (c) 2013 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import org.eclipse.jpt.jpa.core.resource.persistence.JPA;

/**
 * @author Koen Aers
 *
 */
@SuppressWarnings("nls")
public interface JPA2_1
	extends JPA
{
	String SCHEMA_NAMESPACE = JPA.SCHEMA_NAMESPACE;
	String SCHEMA_LOCATION = "http://java.sun.com/xml/ns/persistence/persistence_2_1.xsd";
	String SCHEMA_VERSION = "2.1";
	
}
