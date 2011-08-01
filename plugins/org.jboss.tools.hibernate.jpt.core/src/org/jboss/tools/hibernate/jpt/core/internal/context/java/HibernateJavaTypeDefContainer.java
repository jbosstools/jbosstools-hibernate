/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ListIterator;

import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateJavaTypeDefContainer extends JavaJpaContextNode {

	//******************** typeDef *****************
	
	String TYPE_DEFS_LIST = "typeDefs"; //$NON-NLS-1$	
	
	/**
	 * Return a list iterator of the typeDefs.
	 * This will not be null.
	 */
	ListIterator<? extends JavaTypeDef> typeDefs();
	
	/**
	 * Return the number of typeDefs.
	 */
	int typeDefsSize();

	/**
	 * Add a typeDef to the entity return the object representing it.
	 */
	JavaTypeDef addTypeDef(int index);
	
	/**
	 * Remove the typeDef at the index from the entity.
	 */
	void removeTypeDef(int index);
	
	/**
	 * Remove the typeDef from the entity.
	 */
	void removeTypeDef(JavaTypeDef typeDef);
	
	/**
	 * Move the typeDef from the source index to the target index.
	 */
	void moveTypeDef(int targetIndex, int sourceIndex);
	
	void initialize(JavaResourceAnnotatedElement jrpm);
	
	/**
	 * Update the JavaGeneratorContainer context model object to match the JavaResourcePersistentMember 
	 * resource model object. see {@link org.eclipse.jpt.jpa.core.JpaProject#update()}
	 */
	void update(JavaResourceAnnotatedElement jrpm);
	
}
