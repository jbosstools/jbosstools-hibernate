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
 * Describes relation between 2 entities
 * 
 * @author Vitali
 */
public class RefEntityInfo implements Comparable<RefEntityInfo> {
	/*
	 * fully qualified entity name
	 */
	public String fullyQualifiedName;
	/*
	 * ONE2ONE - one2one reference
	 * ONE2MANY - one2many reference
	 * MANY2ONE - many2one reference
	 * MANY2MANY - many2many reference
	 * so it be possible to resolve @OneToMany & @ManyToMany 
	 */
	public RefType refType;
	/*
	 * mappedBy attribute for reference
	 */
	public String mappedBy = null;
	//
	public OwnerType owner = OwnerType.UNDEF;
	//
	public boolean annotated = false;
	//
	public boolean resolvedAnnotationName = false;
	//
	public boolean hasGetter = false;
	//
	public boolean hasSetter = false;
	// should count number of update operations - cause it is possible to get conflicting information
	public int updateCounter = 0;
	//
	public RefEntityInfo(String fullyQualifiedName, RefType refType) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.refType = refType;
	}
	
	public int compareTo(RefEntityInfo rei) {
		return fullyQualifiedName.compareTo(rei.fullyQualifiedName);
	}
	
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj == null || !(obj instanceof RefEntityInfo)) {
			return false;
		}
		RefEntityInfo rei = (RefEntityInfo)obj;
		return fullyQualifiedName.equals(rei.fullyQualifiedName);
	}

	public int hashCode() {
		return fullyQualifiedName.hashCode();
	}
}
