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

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.jpa.core.resource.java.Annotation;
import org.eclipse.jpt.jpa.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedNativeQueriesAnnotationDefinition implements AnnotationDefinition {

	// singleton
	private static final AnnotationDefinition INSTANCE = new HibernateNamedNativeQueriesAnnotationDefinition();

	/**
	 * Return the singleton.
	 */
	public static AnnotationDefinition instance() {
		return INSTANCE;
	}

	/**
	 * Ensure single instance.
	 */
	private HibernateNamedNativeQueriesAnnotationDefinition() {
		super();
	}

	public Annotation buildAnnotation(JavaResourceAnnotatedElement parent, AnnotatedElement annotatedElement) {
		return new HibernateSourceNamedNativeQueriesAnnotation(parent, annotatedElement);
	}

	public Annotation buildAnnotation(JavaResourceAnnotatedElement parent,
			IAnnotation jdtAnnotation) {
		throw new UnsupportedOperationException();
	}

	public Annotation buildNullAnnotation(JavaResourceAnnotatedElement parent) {
		throw new UnsupportedOperationException();
	}

	public String getAnnotationName() {
		return HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME;
	}

}
