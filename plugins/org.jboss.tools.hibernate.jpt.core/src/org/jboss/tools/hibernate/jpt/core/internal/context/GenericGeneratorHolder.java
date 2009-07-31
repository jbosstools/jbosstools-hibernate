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
package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.ListIterator;

import org.eclipse.jpt.core.context.GeneratorHolder;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GenericGeneratorHolder extends GeneratorHolder {

	String GENERIC_GENERATORS_LIST = "genericGenerators"; //$NON-NLS-1$	
	
	/**
	 * Return a list iterator of the generic generators.
	 * This will not be null.
	 */
	<T extends GenericGenerator> ListIterator<T> genericGenerators();
	
	/**
	 * Return the number of generic generators.
	 */
	int genericGeneratorsSize();

	/**
	 * Add a generic generator to the entity return the object representing it.
	 */
	GenericGenerator addGenericGenerator(int index);
	
	/**
	 * Remove the generic generator at the index from the entity.
	 */
	void removeGenericGenerator(int index);
	
	/**
	 * Remove the generic generator from the entity.
	 */
	void removeGenericGenerator(GenericGenerator generator);
	
	/**
	 * Move the generic generator from the source index to the target index.
	 */
	void moveGenericGenerator(int targetIndex, int sourceIndex);

}
