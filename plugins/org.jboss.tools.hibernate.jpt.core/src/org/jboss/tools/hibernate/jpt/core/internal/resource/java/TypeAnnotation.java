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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.resource.java.Annotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface TypeAnnotation extends Annotation {
	String ANNOTATION_NAME = Hibernate.TYPE;
	
	/**
	 * Corresponds to the 'type' element of the TypeAnnotation annotation.
	 * Return null if the element does not exist in the annotation
	 */
	String getType();
		String TYPE_PROPERTY = Hibernate.TYPE__TYPE;
	
	/**
	 * Corresponds to the 'type' element of the TypeAnnotation annotation.
	 * Setting to null will remove the element.
	 */
	void setType(String value);
	
	/**
	 * Return an empty iterator if the element does not exist in Java.
	 */
	//ListIterator<ParameterAnnotation> parameters();
	//	String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	

	/**
	 * Return the {@link TextRange} for the 'type' element. 
	 */
	TextRange getTypeTextRange(CompilationUnit astRoot);
}
