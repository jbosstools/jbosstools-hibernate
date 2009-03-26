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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.resource.java.GeneratorAnnotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GenericGeneratorAnnotation extends GeneratorAnnotation {
	
	String ANNOTATION_NAME = Hibernate.GENERIC_GENERATOR;
	
	/**
	 * Corresponds to the strategy element of the GenericGenerator annotation.
	 * Returns null if the strategy element does not exist in java.
	 */
	String getStrategy();
	
	/**
	 * Corresponds to the strategy element of the GenericGenerator annotation.
	 * Set to null to remove the strategy element.
	 */
	void setStrategy(String strategy);
		String STRATEGY_PROPERTY = "strategyProperty";	 //$NON-NLS-1$

	/**
	 * Return the {@link TextRange} for the strategy element.  If the strategy element 
	 * does not exist return the {@link TextRange} for the GenericGenerator annotation.
	 */
	TextRange getStrategyTextRange(CompilationUnit astRoot);
	
	JavaGenericGenerator buildJavaGenericGenerator(JavaJpaContextNode parent);

}
