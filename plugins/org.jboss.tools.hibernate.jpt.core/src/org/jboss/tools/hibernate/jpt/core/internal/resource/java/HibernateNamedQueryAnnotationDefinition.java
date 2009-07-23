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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedQueryAnnotationDefinition implements AnnotationDefinition {
	
	// singleton
	private static final AnnotationDefinition INSTANCE = new HibernateNamedQueryAnnotationDefinition();

	
	/**
	 * Ensure single instance.
	 */
	private HibernateNamedQueryAnnotationDefinition() {
		super();
	}
	
	/**
	 * Return the singleton.
	 */
	public static AnnotationDefinition instance() {
		return INSTANCE;
	}

	public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member member) {
		return HibernateSourceNamedQueryAnnotation.createNamedQuery(parent, member);
	}

	public Annotation buildNullAnnotation(JavaResourcePersistentMember parent) {
		throw new UnsupportedOperationException();
	}

	public Annotation buildAnnotation(JavaResourcePersistentMember parent, IAnnotation jdtAnnotation) {
		//TODO return new BinaryNamedQueryAnnotation(parent, jdtAnnotation);
		throw new UnsupportedOperationException();
	}

	public String getAnnotationName() {
		return Hibernate.NAMED_QUERY;
	}
}
