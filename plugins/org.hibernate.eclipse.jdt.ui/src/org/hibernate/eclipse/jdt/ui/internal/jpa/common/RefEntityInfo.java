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
 * 
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
	public boolean annotated = false;
	//
	public boolean resolvedAnnotationName = false;
	//
	public boolean hasGetter = false;
	//
	public boolean hasSetter = false;
	//
	public RefEntityInfo(String fullyQualifiedName, RefType refType) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.refType = refType;
	}
	
	public int compareTo(RefEntityInfo rei) {
		return fullyQualifiedName.compareTo(rei.fullyQualifiedName);
	}
}
