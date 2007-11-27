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
import java.util.ArrayList;
import java.util.Collections;

import org.jboss.tools.hibernate.core.hibernate.IMetaAttribute;


/**
 * A meta attribute is a named value or values.
 */
public class MetaAttribute implements Serializable, IMetaAttribute {
	private static final long serialVersionUID = 1L;
	private String name;
	private java.util.List<String> values = new ArrayList<String>();

	public MetaAttribute(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	

	public java.util.List<String> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void addValue(String value) {
		values.add(value);
	}

	public String getValue() {
		if ( values.size()!=1 ) throw new IllegalStateException("no unique value");
		return (String) values.get(0);
	}

	public boolean isMultiValued() {
		return values.size()>1;
	}

	public String toString() {
		return "[" + name + "=" + values + "]";
	}
}
