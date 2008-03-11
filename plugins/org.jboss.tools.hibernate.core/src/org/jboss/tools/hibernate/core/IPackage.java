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
package org.jboss.tools.hibernate.core;

/**
 * @author alex
 *
 * A Java package
 */
public interface IPackage extends IOrmElement {
	public IPersistentClass[] getPersistentClasses();
	public IMapping getProjectMapping();
    // added by Nick 16.06.2005
	public boolean hasMappedFields();
    // by Nick

	// added by Nick 30.06.2005
	public String getProjectQualifiedName(); 
    // by Nick
}
