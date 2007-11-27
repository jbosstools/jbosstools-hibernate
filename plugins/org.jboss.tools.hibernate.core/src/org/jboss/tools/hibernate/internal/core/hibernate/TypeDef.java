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
package org.jboss.tools.hibernate.internal.core.hibernate;


import java.io.Serializable;
import java.util.Properties;

/**
 * Placeholder for typedef information
 */
public class TypeDef implements Serializable {
	private static final long serialVersionUID = 1L;

	private String typeClass;
	private Properties parameters;

	public TypeDef(String typeClass, Properties parameters) {
		this.typeClass = typeClass;
		this.parameters = parameters;
	}

	public Properties getParameters() {
		return parameters;
	}
	public String getTypeClass() {
		return typeClass;
	}

}
