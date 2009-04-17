/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.Expression;

/**
 * Information about @Column annotation for particular field
 * 
 * @author Vitali Yemialyanchyk
 */
public class RefColumnInfo implements Comparable<RefFieldInfo> {
	/*
	 * field id
	 */
	protected String fieldId;
	/*
	 * is column annotation exist for the field
	 */
	protected boolean exist = false;
	/*
	 * column attributes
	 */
	protected Map<String, Expression> values = new HashMap<String, Expression>();
	//
	public RefColumnInfo(String fieldId) {
		this.fieldId = fieldId;
	}
	
	public int compareTo(RefFieldInfo rfi) {
		return fieldId.compareTo(rfi.fieldId);
	}
	
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj == null || !(obj instanceof RefFieldInfo)) {
			return false;
		}
		RefFieldInfo rfi = (RefFieldInfo)obj;
		return fieldId.equals(rfi.fieldId);
	}

	public int hashCode() {
		return fieldId.hashCode();
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public Map<String, Expression> getValues() {
		return values;
	}

	public void setValues(Map<String, Expression> values) {
		this.values = values;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}
}
