/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

/**
 * Holds information about field reference annotation
 * 
 * @author Vitali
 */
public class RefFieldInfo implements Comparable<RefFieldInfo> {
	/*
	 * field id
	 */
	public String fieldId;
	/*
	 * ONE2ONE - one2one reference
	 * ONE2MANY - one2many reference
	 * MANY2ONE - many2one reference
	 * MANY2MANY - many2many reference
	 * so it be possible to resolve @OneToMany & @ManyToMany 
	 */
	public RefType refType;
	//
	public RefFieldInfo(String fieldId, RefType refType) {
		this.fieldId = fieldId;
		this.refType = refType;
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
}
