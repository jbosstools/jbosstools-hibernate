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

import java.util.Map;

/**
 * @author alex
 *
 * A Hibernate "any" type (ie. polymorphic association to
 * one-of-several tables).
 */
public interface IAnyMapping extends ISimpleValueMapping {
	public abstract String getIdentifierType();

	public abstract void setIdentifierType(String identifierType);

	public abstract String getMetaType();

	public abstract void setMetaType(String type);

	public abstract Map<String,String> getMetaValues();

	public abstract void setMetaValues(Map<String,String> metaValues);
}