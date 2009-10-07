/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
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
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface GeneratedAnnotation extends Annotation {
	String ANNOTATION_NAME = Hibernate.GENERATED;
	
	/**
	 * Corresponds to the 'value' element of the GeneratedAnnotation annotation.
	 * Return null if the element does not exist in the annotation
	 */
	GenerationTime getValue();
		String VALUE_PROPERTY = Hibernate.GENERATED__VALUE;
	
	/**
	 * Corresponds to the 'value' element of the GeneratedAnnotation annotation.
	 * Setting to null will remove the element.
	 */
	void setValue(GenerationTime value);

	/**
	 * Return the {@link TextRange} for the 'value' element. 
	 */
	TextRange getValueTextRange(CompilationUnit astRoot);
}
