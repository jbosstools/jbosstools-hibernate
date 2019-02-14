/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.common.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.common.core.resource.java.NestableAnnotationDefinition;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorAnnotationDefinition implements NestableAnnotationDefinition
{
	// singleton
	private static final NestableAnnotationDefinition INSTANCE = new GenericGeneratorAnnotationDefinition();

	/**
	 * Return the singleton.
	 */
	public static NestableAnnotationDefinition instance() {
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private GenericGeneratorAnnotationDefinition() {
		super();
	}

	@Override
	public NestableAnnotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement annotatedElement, int index) {
		return GenericGeneratorAnnotationImpl.buildGenericGeneratorAnnotation(parent, annotatedElement, index);
	}
	
	@Override
	public NestableAnnotation buildAnnotation(JavaResourceAnnotatedElement parent, IAnnotation jdtAnnotation, int index) {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getNestableAnnotationName() {
		return Hibernate.GENERIC_GENERATOR;
	}

	@Override
	public String getContainerAnnotationName() {
		return Hibernate.GENERIC_GENERATORS;
	}

	@Override
	public String getElementName() {
		return Hibernate.GENERIC_GENERATORS__VALUE;
	}

	


}