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



/**
 * @author alex
 *
 */
public interface IOneToManyMapping extends IHibernateValueMapping {

	public String getReferencedEntityName();

	/** Associated persistent class on the "Many" side. e.g. if a parent has a one-to-many to it's children, the associated class will be the Child. */
	public void setReferencedEntityName(String referencedEntityName);
	
	public IHibernateClassMapping getAssociatedClass();
	public void setAssociatedClass(IHibernateClassMapping associatedClass);

	public boolean isIgnoreNotFound();
	public void setIgnoreNotFound(boolean ignoreNotFound);
	
}